const express = require('express');
const mysql = require('mysql2');
const dotenv = require('dotenv');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const cors = require('cors');
const fs = require('fs');
const path = require('path');


dotenv.config();

const app = express();
const PORT = process.env.PORT || 8080;
const SECRET_KEY = process.env.JWT_SECRET || 'your_jwt_secret'; // Replace with a secure secret

app.use(express.json()); // For parsing JSON body

app.use(cors({
    origin: '*', // Allow all origins
    methods: ['GET', 'POST', 'PUT', 'DELETE'],
    allowedHeaders: ['Content-Type', 'Authorization']
}));

// MySQL Connection
const db = mysql.createConnection({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME || 'robot_info',
    port: process.env.DB_PORT || 3306,
    multipleStatements: true // Allow running multiple SQL commands
});

// Connect to MySQL
db.connect(err => {
    if (err) {
        console.error('Database connection failed:', err);
        return;
    }
    console.log('Connected to MySQL database');

    // Run the SQL dump file
    initializeDatabase();
});

// Function to execute SQL dump file
function initializeDatabase() {
    const sqlDumpPath = path.join(__dirname, '/SQL_Files/robot_info_RoboNav_dump'); // Path to your SQL file
    const sqlDump = fs.readFileSync(sqlDumpPath, 'utf8');

    db.query(sqlDump, (err, results) => {
        if (err) {
            console.error('Error executing SQL dump:', err);
        } else {
            console.log('Database initialized successfully');
        }
    });
}

// Middleware to verify JWT token
const authenticateToken = (req, res, next) => {
    const authHeader = req.header('Authorization');
    if (!authHeader) return res.status(401).json({ message: 'Unauthorized' });

    const token = authHeader.split(' ')[1];
    jwt.verify(token, SECRET_KEY, (err, user) => {
        if (err) return res.status(403).json({ message: 'Invalid or expired token' });

        req.user = user;
        next();
    });
};

// User Registration (Storing Hashed Password)
app.post('/api/open/users/register', async (req, res) => {
    const { username, password, email } = req.body;

    if (!username || !password || !email) {
        return res.status(400).json({ message: 'Username, email, and password are required' });
    }

    try {
        // Check if email already exists
        db.query('SELECT * FROM users WHERE email = ?', [email], async (err, results) => {
            if (err) return res.status(500).json({ message: 'Database error', error: err });

            if (results.length > 0) {
                return res.status(400).json({ message: 'Email already in use' });
            }

            // Hash password
            const hashedPassword = await bcrypt.hash(password, 10);

            // Insert user into database
            db.query('INSERT INTO users (username, hashed_password, email) VALUES (?, ?, ?)',
                [username, hashedPassword, email],
                (err, result) => {
                    if (err) {
                        return res.status(500).json({ message: 'Error inserting user', error: err });
                    }
                    res.status(201).json({ message: 'User registered successfully' });
                }
            );
        });
    } catch (error) {
        res.status(500).json({ message: 'Error processing request', error });
    }
});


// User Login (JWT Token Generation)
app.post('/api/open/users/login', (req, res) => {
    const { username, password } = req.body;

    db.query('SELECT * FROM users WHERE username = ?', [username], async (err, results) => {
        if (err) return res.status(500).json({ message: 'Database error', error: err });

        if (results.length === 0) return res.status(401).json({ message: 'Invalid credentials' });

        const user = results[0];
        const passwordMatch = await bcrypt.compare(password, user.hashed_password);

        if (!passwordMatch) return res.status(401).json({ message: 'Invalid credentials' });

        // Generate JWT Token
        const token = jwt.sign({ userId: user.id, username: user.username }, SECRET_KEY, { expiresIn: '1h' });

        res.json({ message: 'Login successful', token });
    });
});

// Get robot's tasks
app.get('/api/robot/:robotId/tasks', authenticateToken, (req, res) => {
    const robotId = req.params.robotId;
    // Query to get the task for the robot
    db.query('SELECT * FROM task WHERE robot_id = ?', [robotId], (err, results) => {
        // Error handling
        if (err) {
            return res.status(500).json({ message: 'Database error', error: err });
        }
        if (results.length === 0) {
            return res.status(404).json({ message: 'No task found for this robot' });
        }
        // Retrieving task information
        const task = results;
        res.json({ robotId, task });
    });
});

app.get('/api/robot/robots', authenticateToken, (req, res) => {
    const robotQuery = 'SELECT * FROM robot';  // Get all robots
    const taskQuery = 'SELECT task_id FROM task WHERE robot_id = ?';  // Get tasks for each robot
    const locationQuery = `
        SELECT rl.*, l.name 
        FROM robot_location rl
        LEFT JOIN location l 
        ON rl.x = l.x AND rl.y = l.y AND rl.robot_id = l.robot_id
        WHERE rl.robot_id = ? 
        ORDER BY rl.r_loc_id DESC LIMIT 1`;  // Get the most recent location for each robot

    console.log(`Running query: ${robotQuery}`);
    
    // Retrieve all robot info
    db.query(robotQuery, (err, robotResults) => {
        if (err) {
            return res.status(500).json({ message: 'Database error', error: err });
        }
        
        console.log('Robot query result:', robotResults);  // Print robot query results
        if (robotResults.length === 0) {
            return res.status(404).json({ message: 'No robots found' });
        }

        // Create an array to store all the robot details with tasks and locations
        const robotsData = [];

        // Loop through each robot and get their tasks and location
        const robotQueries = robotResults.map((robot) => {
            return new Promise((resolve, reject) => {
                // Fetch tasks for each robot
                db.query(taskQuery, [robot.robot_id], (err, taskResults) => {
                    if (err) return reject({ message: 'Database error fetching tasks', error: err });

                    console.log('Task query result for robot_id', robot.robot_id, taskResults);  // Print task query results

                    // Fetch the most recent location for the robot
                    db.query(locationQuery, [robot.robot_id], (err, locationResults) => {
                        if (err) return reject({ message: 'Database error fetching location', error: err });

                        console.log('Location query result for robot_id', robot.robot_id, locationResults);  // Print location query results

                        const tasks = taskResults.length > 0 ? taskResults.map(task => task.task_id) : [];
                        const location = locationResults[0] || {};

                        // Construct response for this robot
                        const robotData = {
                            id: robot.robot_id,
                            name: robot.name,
                            ping: `${robot.ping || 'N/A'}ms`,
                            battery: robot.battery,
                            location_name: location.name || 'Unknown',
                            location_coordinates: `${location.x || 'N/A'},${location.y || 'N/A'}`,
                            tasks: tasks
                        };

                        robotsData.push(robotData);  // Add this robot's data to the final array
                        resolve();
                    });
                });
            });
        });

        // Wait for all queries to complete and then send the final response
        Promise.all(robotQueries)
            .then(() => {
                res.json(robotsData);  // Return the collected data for all robots
            })
            .catch((error) => {
                res.status(500).json(error);  // Handle errors in any query
            });
    });
});






// Test Route
app.get('/', (req, res) => {
    res.send('Simple Express MySQL API with JWT Authentication is running...');
});

// Start HTTPS Server
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on http://0.0.0.0:${PORT}`);
});
