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


// Test Route
app.get('/', (req, res) => {
    res.send('Simple Express MySQL API with JWT Authentication is running...');
});

// Start HTTPS Server
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on http://0.0.0.0:${PORT}`);
});
