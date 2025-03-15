const db = require('../config/db');
const logger = require('../utils/logger');

// Get the last known position of a robot
const getRobotPosition = (req, res) => {
    const { robotId } = req.params;

    if (!robotId) {
        return res.status(400).json({ message: 'Robot ID is required' });
    }

    const sql = `
        SELECT latitude, longitude, timestamp 
        FROM robot_location 
        WHERE robot_id = ? 
        ORDER BY timestamp DESC 
        LIMIT 1;
    `;

    db.query(sql, [robotId], (err, results) => {
        if (err) {
            logger.error(`Error fetching live coordinates for robot ${robotId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: 'No location data found for this robot' });
        }

        res.json(results[0]);
    });
};

const saveRobotLocation = (req, res) => {
    const { robotId, locationName } = req.body;

    if (!robotId || !locationName) {
        return res.status(400).json({ message: 'Robot ID and location name are required' });
    }

    const fetchLocationSQL = `
        SELECT latitude, longitude FROM robot_location 
        WHERE robot_id = ? 
        ORDER BY timestamp DESC 
        LIMIT 1;
    `;

    db.query(fetchLocationSQL, [robotId], (err, results) => {
        if (err) {
            logger.error(`Error fetching robot ${robotId} location:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: 'No recorded location for this robot' });
        }

        const { latitude, longitude } = results[0];

        const insertLocationSQL = `
            INSERT INTO location (name, latitude, longitude, robot_id) 
            VALUES (?, ?, ?, ?);
        `;

        db.query(insertLocationSQL, [locationName, latitude, longitude, robotId], (err) => {
            if (err) {
                logger.error(`Error saving location ${locationName} for robot ${robotId}:`, err);
                return res.status(500).json({ message: 'Database error' });
            }

            res.status(201).json({ message: `Location '${locationName}' saved successfully.` });
        });
    });
};

const removeAllRobotLocations = (req, res) => {
    const { robotId } = req.params;

    if (!robotId) {
        return res.status(400).json({ message: 'Robot ID is required' });
    }

    const sql = `DELETE FROM location WHERE robot_id = ?`;

    db.query(sql, [robotId], (err, result) => {
        if (err) {
            logger.error(`Error deleting locations for robot ${robotId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: 'No locations found for this robot' });
        }

        res.json({ message: `All locations removed for robot ${robotId}.` });
    });
};

const getCoordinatesByLocation = (req, res) => {
    const { locationName } = req.params;

    if (!locationName) {
        return res.status(400).json({ message: 'Location name is required' });
    }

    const sql = `SELECT latitude, longitude FROM location WHERE name = ?`;

    db.query(sql, [locationName], (err, results) => {
        if (err) {
            logger.error(`Error fetching coordinates for location ${locationName}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: 'Location not found' });
        }

        res.json(results[0]);
    });
};

const getAllLocations = (req, res) => {
    const sql = `SELECT name, latitude, longitude FROM location`;

    db.query(sql, (err, results) => {
        if (err) {
            logger.error('Error fetching all locations:', err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: 'No locations found' });
        }

        res.json(results);
    });
};

// Delete a task by ID
const deleteTask = (req, res) => {
    const { taskId } = req.params;

    // Input validation
    if (!taskId) {
        logger.warn('Task ID is required for deletion.');
        return res.status(400).json({ message: 'Task ID is required' });
    }

    // Step 1: Check the task state
    const getTaskSQL = `SELECT state, instruction_ids, robot_id FROM task WHERE task_id = ?`;

    db.query(getTaskSQL, [taskId], (err, results) => {
        if (err) {
            logger.error(`Database error while checking task state for task ID ${taskId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            logger.warn(`Task with ID ${taskId} not found.`);
            return res.status(404).json({ message: 'Task not found' });
        }

        const { state, instruction_ids, robot_id } = results[0];

        // Step 2: If state = 2, delete task immediately
        if (state === 2) {
            const deleteTaskSQL = `DELETE FROM task WHERE task_id = ?`;
            db.query(deleteTaskSQL, [taskId], (err, result) => {
                if (err) {
                    logger.error(`Database error while deleting task ID ${taskId}:`, err);
                    return res.status(500).json({ message: 'Database error' });
                }

                logger.info(`Task with ID ${taskId} deleted successfully.`);
                return res.json({ message: `Task with ID ${taskId} deleted successfully.` });
            });
            return;
        }

        // Step 3: If state ≠ 2, handle active instructions
        if (!instruction_ids) {
            logger.warn(`No instructions associated with task ID ${taskId}.`);
            return res.status(400).json({ message: 'Task has no instructions to stop.' });
        }

        const instructionList = instruction_ids.split(',').map(id => parseInt(id.trim()));

        // Fetch all instructions related to this task
        const getInstructionsSQL = `
            SELECT ins_id, instruction, status FROM ins_send WHERE ins_id IN (?)
        `;

        db.query(getInstructionsSQL, [instructionList], (err, instructions) => {
            if (err) {
                logger.error(`Database error while fetching instructions for task ID ${taskId}:`, err);
                return res.status(500).json({ message: 'Database error' });
            }

            let activeInstruction = null;
            let orderInstructions = [];

            // Determine active instruction and order instructions
            instructions.forEach(instr => {
                if (instr.status === 'accept') {
                    activeInstruction = instr;
                } else if (instr.status === 'order') {
                    orderInstructions.push(instr.ins_id);
                }
            });

            // Step 4: Delete all "order" instructions **before** stopping the active one
            if (orderInstructions.length > 0) {
                const deleteOrderInstructionsSQL = `DELETE FROM ins_send WHERE ins_id IN (?)`;

                db.query(deleteOrderInstructionsSQL, [orderInstructions], (err) => {
                    if (err) {
                        logger.error(`Database error while deleting pending instructions for task ID ${taskId}:`, err);
                        return res.status(500).json({ message: 'Database error' });
                    }

                    logger.info(`Deleted ${orderInstructions.length} pending instructions for task ID ${taskId}`);
                });
            }

            // Step 5: Stop active instruction if found **after deleting "order" instructions**
            if (activeInstruction) {
                const stopInstruction = `navigation:stopNavigation:${activeInstruction.instruction.split(':')[2]}`;

                const insertInstructionSQL = `
                    INSERT INTO ins_send (robot_id, instruction, status, ctime) VALUES (?, ?, 'order', NOW())
                `;

                db.query(insertInstructionSQL, [robot_id, stopInstruction], (err) => {
                    if (err) {
                        logger.error(`Failed to send stop navigation instruction for task ID ${taskId}:`, err);
                        return res.status(500).json({ message: 'Failed to send stop navigation instruction.' });
                    }

                    logger.info(`Stop navigation instruction sent for instruction ID ${activeInstruction.ins_id}`);
                });
            }

            // Step 6: Delete the task after stopping/removing instructions
            const deleteTaskSQL = `DELETE FROM task WHERE task_id = ?`;

            db.query(deleteTaskSQL, [taskId], (err) => {
                if (err) {
                    logger.error(`Database error while deleting task ID ${taskId}:`, err);
                    return res.status(500).json({ message: 'Database error' });
                }

                logger.info(`Task with ID ${taskId} deleted successfully.`);
                return res.json({ message: `Task with ID ${taskId} deleted successfully.` });
            });
        });
    });
};

// Create a new task for a robot
const createTask = (req, res) => {
    const { name, robot_id } = req.body;

    // Input validation
    if (!name || !robot_id) {
        logger.warn('Missing required fields for creating a task.');
        return res.status(400).json({ message: 'Missing required fields: name, robot_id' });
    }

    // Default values
    const state = 0; // Default task state (e.g., Not Started)
    const timestamp = new Date(); // Current timestamp

    // SQL query to insert new task
    const sql = `
        INSERT INTO task (name, robot_id, state, timeStamp)
        VALUES (?, ?, ?, ?)
    `;

    db.query(sql, [name, robot_id, state, timestamp], (err, result) => {
        if (err) {
            logger.error('Database error while inserting task:', err);
            return res.status(500).json({ message: 'Database error' });
        }

        logger.info(`New task created with ID: ${result.insertId}`);
        res.status(201).json({
            message: 'Task created successfully',
            task_id: result.insertId
        });
    });
};

// Delete a robot by ID
const deleteRobot = (req, res) => {
    const { robotId } = req.params;

    // Input validation
    if (!robotId) {
        logger.warn('Robot ID is required for deletion.');
        return res.status(400).json({ message: 'Robot ID is required' });
    }

    // SQL query to check if the robot has active instructions
    const checkInstructionsSQL = `
        SELECT COUNT(*) AS count FROM ins_send
        WHERE robot_id = ? AND status IN ('accept', 'order')
    `;

    db.query(checkInstructionsSQL, [robotId], (err, results) => {
        if (err) {
            logger.error(`Database error while checking instructions for robot ID ${robotId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        // If robot has active instructions, prevent deletion
        if (results[0].count > 0) {
            logger.warn(`Cannot delete robot ID ${robotId} - it has pending instructions.`);
            return res.status(403).json({ message: 'Cannot delete robot with active instructions (accept/order status).' });
        }

        // If no active instructions, proceed with deletion
        const deleteSQL = `DELETE FROM robot WHERE robot_id = ?`;

        db.query(deleteSQL, [robotId], (err, result) => {
            if (err) {
                logger.error(`Database error while deleting robot ID ${robotId}:`, err);
                return res.status(500).json({ message: 'Database error' });
            }

            if (result.affectedRows === 0) {
                logger.warn(`Robot with ID ${robotId} not found.`);
                return res.status(404).json({ message: 'Robot not found' });
            }

            logger.info(`Robot with ID ${robotId} deleted successfully.`);
            res.json({ message: `Robot with ID ${robotId} deleted successfully.` });
        });
    });
};

// Create a new robot
const createRobot = (req, res) => {
    const { type, ip_add, port } = req.body;

    // Input validation
    if (!type || !ip_add || !port) {
        logger.warn('Missing required fields for creating a robot.');
        return res.status(400).json({ message: 'Missing required fields: type, ip_add, port' });
    }

    // Default values
    const site_id = 1;  // Set site_id to 1 (London)
    const serial_num = "";  // Manually set serial number to an empty string

    // SQL query to insert new robot
    const sql = `
        INSERT INTO robot (type, ip_add, port, serial_num, site_id)
        VALUES (?, ?, ?, ?, ?)
    `;

    db.query(sql, [type, ip_add, port, serial_num, site_id], (err, result) => {
        if (err) {
            logger.error('Database error while inserting robot:', err);
            return res.status(500).json({ message: 'Database error' });
        }

        logger.info(`New robot created with ID: ${result.insertId}`);
        res.status(201).json({
            message: 'Robot created successfully',
            robot_id: result.insertId
        });
    });
};

// Get Robot Tasks
const getRobotTasks = (req, res) => {
    logger.info('Fetching robot tasks...');
    db.query('SELECT * FROM task', (err, results) => {
        if (err) {
            logger.error('Database error while fetching tasks:', err);
            return res.status(500).json({ message: 'Database error' });
        }
        if (results.length === 0) {
            logger.warn('No tasks found in the database.');
            return res.status(404).json({ message: 'No tasks found' });
        }

        const tasks = results.map(task => ({
            id: task.task_id,
            name: task.name,
            robot: task.robot_id,
            progress: 50,
            createdBy: 'n/a',
            dateCreated: task.start,
            state: task.state,
            dateCompleted: task.end
        }));

        logger.info(`Successfully retrieved ${tasks.length} tasks.`);
        res.json(tasks);
    });
};
// Get all robots
const getAllRobots = (req, res) => {
    logger.info('Fetching all robots...');

    const robotQuery = 'SELECT * FROM robot';
    const taskQuery = 'SELECT task_id FROM task WHERE robot_id = ?';
    const locationQuery = `
      SELECT rl.*, l.name 
      FROM robot_location rl
      LEFT JOIN location l 
      ON rl.x = l.x AND rl.y = l.y AND rl.robot_id = l.robot_id
      WHERE rl.robot_id = ? 
      ORDER BY rl.r_loc_id DESC LIMIT 1`;

    db.query(robotQuery, (err, robotResults) => {
        if (err) {
            logger.error('Database error while fetching robots:', err);
            return res.status(500).json({ message: 'Database error' });
        }
        if (robotResults.length === 0) {
            logger.warn('No robots found in the database.');
            return res.status(404).json({ message: 'No robots found' });
        }

        logger.info(`Found ${robotResults.length} robots, fetching tasks and locations...`);

        const robotsData = [];
        const robotQueries = robotResults.map((robot) => {
            return new Promise((resolve, reject) => {
                db.query(taskQuery, [robot.robot_id], (err, taskResults) => {
                    if (err) {
                        logger.error(`Error fetching tasks for robot ${robot.robot_id}:`, err);
                        return reject({ message: 'Database error' });
                    }

                    db.query(locationQuery, [robot.robot_id], (err, locationResults) => {
                        if (err) {
                            logger.error(`Error fetching location for robot ${robot.robot_id}:`, err);
                            return reject({ message: 'Database error' });
                        }

                        const tasks = taskResults.length > 0 ? taskResults.map(task => task.task_id) : [];
                        const location = locationResults[0] || {};

                        const robotData = {
                            id: robot.robot_id,
                            name: `Robot #${robot.robot_id}`,
                            ip_add: robot.ip_add || 'Unknown',
                            battery: robot.battery,
                            location_name: location.name || "Unknown",
                            location_coordinates: (location.x && location.y) ? `${location.x},${location.y}` : "Unknown",
                            tasks: tasks,
                            charging: robot.is_charging
                        };

                        robotsData.push(robotData);
                        resolve();
                    });
                });
            });
        });

        Promise.all(robotQueries)
            .then(() => {
                logger.info(`Successfully retrieved data for ${robotsData.length} robots.`);
                res.json(robotsData);
            })
            .catch((error) => {
                logger.error('Error processing robot data:', error);
                res.status(500).json(error);
            });
    });
};

// Get robot location
const getRobotLocation = (req, res) => {
    const robotId = req.params.robotId;

    logger.info(`Fetching locations for robot ID: ${robotId}...`);

    const locationQuery = `
      SELECT x, y, name FROM location
      WHERE robot_id = ?;`;

    db.query(locationQuery, [robotId], (err, locationResults) => {
        if (err) {
            logger.error(`Database error while fetching locations for robot ${robotId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (locationResults.length === 0) {
            logger.warn(`No locations found for robot ${robotId}.`);
            return res.status(200).json([]); // Returning empty array instead of 404
        }

        const uniqueLocations = locationResults.map(location => ({
            location_name: location.name || 'Unknown',
            location_coordinates: `${location.x},${location.y}`
        }));

        logger.info(`Successfully retrieved ${uniqueLocations.length} locations for robot ${robotId}.`);
        res.json(uniqueLocations);
    });
};

// Get the 30 most recent robot callbacks
const getRobotCallbacks = (req, res) => {
    logger.info('Fetching the 30 most recent callbacks...');

    const query = `
        SELECT robot_id, callback, ctime
        FROM callback_rec
        ORDER BY cb_id DESC
        LIMIT 30
    `;

    db.query(query, (err, results) => {
        if (err) {
            logger.error('Database error while fetching callbacks:', err);
            return res.status(500).json({ message: 'Database error', error: err });
        }

        if (results.length === 0) {
            logger.warn('No callbacks found.');
            return res.status(404).json({ message: 'No callbacks found' });
        }

        // Format results
        const callbackMessages = results.map(callback =>
            `Robot ${callback.robot_id}: ${callback.callback} [${callback.ctime}]`
        );

        logger.info(`Successfully retrieved ${callbackMessages.length} callbacks.`);
        res.json({ message: 'Callbacks retrieved', data: callbackMessages });
    });
};


// Send robot instructions
const sendRobotInstruction = (req, res) => {
    const { robot_id, instruction } = req.body;

    if (!robot_id || !instruction) {
        logger.warn('Missing required fields: Robot ID or instruction.');
        return res.status(400).json({ message: 'Robot ID and instruction are required' });
    }

    logger.info(`Queuing instruction for robot ${robot_id}: ${instruction}`);

    const insertQuery = `
      INSERT INTO ins_send (robot_id, instruction, status, ctime)
      VALUES (?, ?, 'order', NOW())
  `;

    db.query(insertQuery, [robot_id, instruction], (err, result) => {
        if (err) {
            logger.error(`Database error while queuing instruction for robot ${robot_id}:`, err);
            return res.status(500).json({ message: 'Database error', error: err });
        }

        logger.info(`Instruction successfully queued for robot ${robot_id} with ID: ${result.insertId}`);
        res.json({
            message: `Instruction queued for robot ${robot_id}`,
            instructionId: result.insertId
        });
    });
};

module.exports = { getAllLocations, getCoordinatesByLocation, removeAllRobotLocations, saveRobotLocation, getRobotPosition, deleteTask, getRobotTasks, getAllRobots, getRobotLocation, getRobotCallbacks, sendRobotInstruction, createRobot, deleteRobot, createTask };
