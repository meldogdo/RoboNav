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

const getLocationsByRobotId = (req, res) => {
    const { robotId } = req.params;

    if (!robotId || isNaN(robotId)) {
        return res.status(400).json({ message: 'Valid Robot ID is required' });
    }

    const sql = `SELECT loc_id, name FROM location WHERE robot_id = ?`;

    db.query(sql, [robotId], (err, results) => {
        if (err) {
            logger.error(`Error fetching locations for robot ID ${robotId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: `No locations found for robot ID ${robotId}.` });
        }

        logger.info(`Fetched ${results.length} location(s) for robot ID ${robotId}.`);

        res.status(200).json(results);
    });
};

const removeRobotLocationById = (req, res) => {
    const { locId } = req.params;

    if (!locId || isNaN(locId)) {
        return res.status(400).json({ message: 'Valid Location ID is required' });
    }

    const sql = `DELETE FROM location WHERE loc_id = ?`;

    db.query(sql, [locId], (err, result) => {
        if (err) {
            logger.error(`Error deleting location with location ID ${locId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        logger.info(`Deleted ${result.affectedRows} location(s) with location ID ${locId}.`);

        res.status(200).json({
            message: result.affectedRows > 0
                ? `Location with ID ${locId} removed.`
                : `No location found with location ID ${locId}.`
        });
    });
};

const getCoordinatesByLocation = (req, res) => {
    const { locId } = req.params;

    if (!locId) {
        return res.status(400).json({ message: 'Location ID is required' });
    }

    const sql = `SELECT x, y, z, theta, name, robot_id FROM location WHERE loc_id = ?`;

    db.query(sql, [locId], (err, results) => {
        if (err) {
            logger.error(`Error fetching coordinates for location ID '${locId}':`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: `Location ID '${locId}' not found.` });
        }

        logger.info(`Fetched coordinates for location ID '${locId}'.`);

        const { x, y, z, theta, name, robot_id } = results[0];

        res.json({
            locId,
            locationName: name,
            robotId: robot_id,
            coordinates: { x, y, z, theta }
        });
    });
};


const getAllLocations = (req, res) => {
    const sql = `
        SELECT loc_id, robot_id, name, x, y, z, theta 
        FROM location 
        ORDER BY robot_id ASC;
    `;

    db.query(sql, (err, results) => {
        if (err) {
            logger.error('Database error while fetching all locations:', err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            logger.warn('No locations found.');
            return res.status(200).json([]); // Return an empty array instead of 404
        }

        logger.info(`Successfully retrieved ${results.length} locations.`);

        res.json({
            totalLocations: results.length,
            locations: results.map(({ loc_id, robot_id, name, x, y, z, theta }) => ({
                locId: loc_id,
                robotId: robot_id,
                locationName: name,
                coordinates: { x, y, z, theta }
            }))
        });
    });
};

// Delete a task by ID
const deleteTask = (req, res) => {
    const { taskId } = req.params;

    if (!taskId) {
        logger.warn('Task ID is required for deletion.');
        return res.status(400).json({ message: 'Task ID is required' });
    }

    // Fetch task state
    const getTaskSQL = `SELECT state FROM task WHERE task_id = ?`;

    db.query(getTaskSQL, [taskId], (err, results) => {
        if (err) {
            logger.error(`Database error while checking task state for task ID ${taskId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            logger.warn(`Task with ID ${taskId} not found.`);
            return res.status(404).json({ message: 'Task not found' });
        }

        const { state } = results[0];

        // Allow deletion if task is Not Started (0) or Completed (2) or Stopped (3)
        if (state === 0 || state === 2 || state === 3 ) {
            const deleteTaskSQL = `DELETE FROM task WHERE task_id = ?`;
            db.query(deleteTaskSQL, [taskId], (err) => {
                if (err) {
                    logger.error(`Database error while deleting task ID ${taskId}:`, err);
                    return res.status(500).json({ message: 'Database error' });
                }

                logger.info(`Task with ID ${taskId} deleted successfully.`);
                return res.json({ message: `Task with ID ${taskId} deleted successfully.` });
            });
            return;
        }

        // Prevent deletion of Running tasks (state = 1)
        logger.warn(`Task ID ${taskId} is still running. Stop the task before deleting.`);
        return res.status(400).json({
            message: `Task ID ${taskId} is still running. Please stop the task before deleting.`
        });
    });
};

const startTask = (req, res) => {
    const { taskId } = req.params;

    if (!taskId) {
        logger.warn('Task ID is required.');
        return res.status(400).json({ message: 'Task ID is required.' });
    }

    // Step 1: Fetch task details (robot ID + instruction list)
    const getTaskSQL = `SELECT robot_id, instruction_list FROM task WHERE task_id = ? AND state = 0 OR state = 3`;

    db.query(getTaskSQL, [taskId], (err, results) => {
        if (err) {
            logger.error(`Database error while fetching task ${taskId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            logger.warn(`Task ${taskId} not found or already started.`);
            return res.status(400).json({ message: 'Task not found or has already started.' });
        }

        const { robot_id, instruction_list } = results[0];

        if (!instruction_list) {
            logger.warn(`Task ${taskId} has no instructions.`);
            return res.status(400).json({ message: 'Task has no instructions.' });
        }

        let instructions;
        try {
            instructions = JSON.parse(instruction_list);
        } catch (parseError) {
            logger.error(`Failed to parse instruction list for task ${taskId}:`, parseError);
            return res.status(500).json({ message: 'Invalid instruction list format.' });
        }

        if (!Array.isArray(instructions) || instructions.length === 0) {
            logger.warn(`Task ${taskId} has an empty instruction list.`);
            return res.status(400).json({ message: 'Task has no valid instructions.' });
        }

        // Step 2: Check if another task for the same robot is already running
        const checkActiveTaskSQL = `SELECT task_id FROM task WHERE robot_id = ? AND state = 1`;

        db.query(checkActiveTaskSQL, [robot_id], (err, activeResults) => {
            if (err) {
                logger.error(`Database error while checking active task for robot ${robot_id}:`, err);
                return res.status(500).json({ message: 'Database error' });
            }

            if (activeResults.length > 0) {
                const activeTaskId = activeResults[0].task_id;
                logger.warn(`Robot ${robot_id} already has an active task (ID: ${activeTaskId}). Cannot start a new task.`);
                return res.status(400).json({
                    message: `Robot ${robot_id} is already running task ID ${activeTaskId}. Please complete that task before starting a new one.`
                });
            }

            // Step 3: No active task exists, update task state to running
            const updateTaskSQL = `UPDATE task SET state = 1 WHERE task_id = ?`;

            db.query(updateTaskSQL, [taskId], (err) => {
                if (err) {
                    logger.error(`Database error while updating task ${taskId} to running:`, err);
                    return res.status(500).json({ message: 'Failed to update task state' });
                }

                logger.info(`Task ${taskId} updated to Running. Starting queue.`);

                // Step 4: Start queueing the instructions
                queueTaskInstructions(taskId, robot_id, instructions);

                return res.json({ message: `Task ${taskId} started successfully.` });
            });
        });
    });
};

const queueTaskInstructions = (taskId, robotId, instructions, index = 0) => {
    const processNextInstruction = (taskId, robotId, instructions, index) => {
        if (index >= instructions.length) {
            logger.info(`All instructions for task ${taskId} completed. Marking as complete...`);

            // Step 1: Mark task as complete (`state = 2`) and set end time
            const completeTaskSQL = `UPDATE task SET state = 2, current_instruction_index = 0, end = NOW() WHERE task_id = ?`;

            db.query(completeTaskSQL, [taskId], (err) => {
                if (err) {
                    logger.error(`Database error updating task ${taskId} to complete:`, err);
                } else {
                    logger.info(`Task ${taskId} marked as complete with end time recorded.`);
                }
            });

            return; // No more instructions to process
        }

        // Step 2: Check if task has been stopped before proceeding
        const checkTaskStateSQL = `SELECT state FROM task WHERE task_id = ?`;

        db.query(checkTaskStateSQL, [taskId], (err, results) => {
            if (err) {
                logger.error(`Database error while checking state of task ${taskId}:`, err);
                return;
            }

            if (results.length === 0) {
                logger.warn(`Task ID ${taskId} not found, stopping queue.`);
                return;
            }

            const { state } = results[0];

            if (state === 3) {
                const adjustedIndex = Math.max(0, index - 1); // Ensure it's at least 0
                
                logger.warn(`Task ${taskId} has been stopped. Storing current index ${adjustedIndex}.`);
                
                // Store `index - 1` to redo the last instruction when resuming
                const updateIndexSQL = `UPDATE task SET current_instruction_index = ? WHERE task_id = ?`;
                db.query(updateIndexSQL, [adjustedIndex, taskId], (err) => {
                    if (err) {
                        logger.error(`Database error while updating current instruction index for task ${taskId}:`, err);
                    } else {
                        logger.info(`Task ${taskId} progress saved at index ${adjustedIndex}.`);
                    }
                });

                return; // Stop queueing instructions
            }

            const instruction = instructions[index];


            // Step 3: Insert the instruction into `ins_send`
            const insertInstructionSQL = `
                INSERT INTO ins_send (robot_id, instruction, status, ctime) 
                VALUES (?, ?, 'order', NOW())
            `;

            db.query(insertInstructionSQL, [robotId, instruction], (err, result) => {
                if (err) {
                    logger.error(`Failed to send instruction: ${instruction} for task ${taskId}:`, err);
                    return;
                }

                const insId = result.insertId;
                logger.info(`Instruction ${instruction} (ID: ${insId}) sent to robot ${robotId}.`);

                // Step 4: Update progress in the database
                const updateTaskSQL = `UPDATE task SET current_instruction_index = ? WHERE task_id = ?`;

                db.query(updateTaskSQL, [index, taskId], (err) => {
                    if (err) {
                        logger.error(`Database error while updating instruction index for task ${taskId}:`, err);
                    }
                });

                // Step 5: Wait for the instruction to complete before moving to the next one
                waitForInstructionCompletion(insId, taskId, robotId, () => {
                    processNextInstruction(taskId, robotId, instructions, index + 1);
                });
            });
        });
    };

    processNextInstruction(taskId, robotId, instructions, index);
};

const waitForInstructionCompletion = (insId, taskId, robotId, callback) => {
    const checkStatusSQL = `SELECT status FROM ins_send WHERE ins_id = ?`;
    const checkTaskStateSQL = `SELECT state FROM task WHERE task_id = ?`;

    const interval = setInterval(() => {
        db.query(checkStatusSQL, [insId], (err, results) => {
            if (err) {
                logger.error(`Error checking status for instruction ID ${insId}:`, err);
                clearInterval(interval);
                return;
            }

            if (results.length === 0) {
                logger.warn(`Instruction ID ${insId} no longer exists.`);
                clearInterval(interval);
                return;
            }

            const { status } = results[0];

            // Step 1: Check if task has been stopped
            db.query(checkTaskStateSQL, [taskId], (err, taskResults) => {
                if (err) {
                    logger.error(`Error checking state for task ${taskId}:`, err);
                    clearInterval(interval);
                    return;
                }

                if (taskResults.length === 0) {
                    logger.warn(`Task ID ${taskId} no longer exists.`);
                    clearInterval(interval);
                    return;
                }

                const { state } = taskResults[0];

                if (state === 3) {
                    logger.warn(`Task ${taskId} has been stopped. Halting instruction queue.`);
                    clearInterval(interval);
                    return;
                }

                // Step 2: If instruction is complete, proceed to next one
                if (status === 'complete') {
                    logger.info(`Instruction ID ${insId} completed successfully.`);
                    clearInterval(interval);
                    callback(); // Move to the next instruction
                } else if (status === 'emg' || status === 'aborted') {
                    logger.warn(`Instruction ID ${insId} was stopped or failed (Status: ${status}).`);
                    clearInterval(interval);
                }
            });
        });
    }, 5000); // Check every 5 seconds
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

const stopTask = (req, res) => {
    const { taskId } = req.params;

    if (!taskId) {
        logger.warn('Task ID is required to stop a task.');
        return res.status(400).json({ message: 'Task ID is required.' });
    }

    // Step 1: Fetch the task details
    const getTaskSQL = `SELECT robot_id, state, current_instruction_index FROM task WHERE task_id = ?`;

    db.query(getTaskSQL, [taskId], (err, results) => {
        if (err) {
            logger.error(`Database error while fetching task ${taskId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            logger.warn(`Task ${taskId} not found.`);
            return res.status(404).json({ message: 'Task not found.' });
        }

        const { robot_id, state, current_instruction_index } = results[0];

        // If the task is not running, return an error
        if (state !== 1) {
            logger.warn(`Task ${taskId} is not currently running.`);
            return res.status(400).json({ message: 'Task is not running and cannot be stopped.' });
        }

        // Step 2: Send an emergency stop instruction to the robot
        const stopInstruction = `navigation:stopNavigation`;

        const insertStopInstructionSQL = `
            INSERT INTO ins_send (robot_id, instruction, status, ctime) 
            VALUES (?, ?, 'emg', NOW())
        `;

        db.query(insertStopInstructionSQL, [robot_id, stopInstruction], (err) => {
            if (err) {
                logger.error(`Database error while sending stop command for robot ${robot_id}:`, err);
                return res.status(500).json({ message: 'Failed to send stop command.' });
            }

            logger.info(`Emergency stop instruction sent for robot ${robot_id}.`);

            // Step 3: Mark the task as stopped (`state = 3`)
            const updateTaskSQL = `UPDATE task SET state = 3 WHERE task_id = ?`;

            db.query(updateTaskSQL, [taskId], (err) => {
                if (err) {
                    logger.error(`Database error while updating task ${taskId} to stopped:`, err);
                    return res.status(500).json({ message: 'Failed to update task state to stopped.' });
                }

                logger.info(`Task ${taskId} marked as stopped at index ${current_instruction_index}.`);
                res.json({ message: `Task ${taskId} has been stopped. Progress saved at instruction index ${current_instruction_index}.` });
            });
        });
    });
};

// Delete a robot by ID
const deleteRobot = (req, res) => {
    const { robotId } = req.params;

    if (!robotId) {
        logger.warn('Robot ID is required for deletion.');
        return res.status(400).json({ message: 'Robot ID is required' });
    }

    // Step 1: Check if the robot has any tasks that are Not Started (0) or Running (1)
    const checkTasksSQL = `
        SELECT COUNT(*) AS count FROM task
        WHERE robot_id = ? AND state IN (0, 1)
    `;

    db.query(checkTasksSQL, [robotId], (err, results) => {
        if (err) {
            logger.error(`Database error while checking tasks for robot ID ${robotId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        // Prevent deletion if the robot has tasks that are not started or running
        if (results[0].count > 0) {
            logger.warn(`Cannot delete robot ID ${robotId} - it has pending or active tasks.`);
            return res.status(403).json({
                message: `Cannot delete robot ID ${robotId}. It has tasks that are either not started or still running. Complete or delete those tasks first.`
            });
        }

        // Step 2: If no such tasks exist, proceed with deletion
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
const addInstructionToTask = (req, res) => {
    const { task_id, instruction } = req.body;

    if (!task_id || !instruction) {
        logger.warn('Missing required fields: Task ID or instruction.');
        return res.status(400).json({ message: 'Task ID and instruction are required' });
    }

    logger.info(`Adding instruction to task ${task_id}: ${instruction}`);

    // Step 1: Fetch the current instruction list for the given task
    const getTaskSQL = `SELECT instruction_list FROM task WHERE task_id = ? AND state = 0 LIMIT 1`;

    db.query(getTaskSQL, [task_id], (err, results) => {
        if (err) {
            logger.error(`Database error while fetching task ID ${task_id}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            logger.warn(`Task ID ${task_id} not found or is already started.`);
            return res.status(400).json({ message: 'Task not found or has already started.' });
        }

        let instructionList = [];

        // Step 2: Parse existing instructions if any
        if (results[0].instruction_list) {
            try {
                instructionList = JSON.parse(results[0].instruction_list);
            } catch (parseErr) {
                logger.error(`Error parsing instruction list for task ${task_id}:`, parseErr);
                return res.status(500).json({ message: 'Error parsing instruction list.' });
            }
        }

        // Step 3: Append the new instruction
        instructionList.push(instruction);
        const updatedInstructionList = JSON.stringify(instructionList);

        // Step 4: Update the task table with the new instruction list
        const updateTaskSQL = `UPDATE task SET instruction_list = ? WHERE task_id = ?`;

        db.query(updateTaskSQL, [updatedInstructionList, task_id], (err) => {
            if (err) {
                logger.error(`Database error while updating instruction list for task ID ${task_id}:`, err);
                return res.status(500).json({ message: 'Database error' });
            }

            logger.info(`Instruction added successfully to task ${task_id}.`);
            res.json({ message: `Instruction added to task ${task_id}.` });
        });
    });
};

const resumeTask = (req, res) => {
    const { taskId } = req.params;

    if (!taskId) {
        logger.warn('Task ID is required.');
        return res.status(400).json({ message: 'Task ID is required.' });
    }

    // Step 1: Fetch task details (robot ID, instruction list, current index)
    const getTaskSQL = `SELECT robot_id, instruction_list, current_instruction_index FROM task WHERE task_id = ? AND state = 3`;

    db.query(getTaskSQL, [taskId], (err, results) => {
        if (err) {
            logger.error(`Database error while fetching task ${taskId}:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            logger.warn(`Task ${taskId} not found or is not in a stopped state.`);
            return res.status(400).json({ message: 'Task not found or is not in a stopped state.' });
        }

        const { robot_id, instruction_list, current_instruction_index } = results[0];

        if (!instruction_list) {
            logger.warn(`Task ${taskId} has no instructions.`);
            return res.status(400).json({ message: 'Task has no instructions.' });
        }

        let instructions;
        try {
            instructions = JSON.parse(instruction_list);
        } catch (parseError) {
            logger.error(`Failed to parse instruction list for task ${taskId}:`, parseError);
            return res.status(500).json({ message: 'Invalid instruction list format.' });
        }

        if (!Array.isArray(instructions) || instructions.length === 0) {
            logger.warn(`Task ${taskId} has an empty instruction list.`);
            return res.status(400).json({ message: 'Task has no valid instructions.' });
        }

        // Step 2: Check if another task for the same robot is already running
        const checkActiveTaskSQL = `SELECT task_id FROM task WHERE robot_id = ? AND state = 1`;

        db.query(checkActiveTaskSQL, [robot_id], (err, activeResults) => {
            if (err) {
                logger.error(`Database error while checking active task for robot ${robot_id}:`, err);
                return res.status(500).json({ message: 'Database error' });
            }

            if (activeResults.length > 0) {
                const activeTaskId = activeResults[0].task_id;
                logger.warn(`Robot ${robot_id} already has an active task (ID: ${activeTaskId}). Cannot resume a stopped task.`);
                return res.status(400).json({
                    message: `Robot ${robot_id} is already running task ID ${activeTaskId}. Please complete that task before resuming.`
                });
            }

            // Step 3: No active task exists, update task state to running
            const updateTaskSQL = `UPDATE task SET state = 1 WHERE task_id = ?`;

            db.query(updateTaskSQL, [taskId], (err) => {
                if (err) {
                    logger.error(`Database error while updating task ${taskId} to running:`, err);
                    return res.status(500).json({ message: 'Failed to update task state' });
                }

                logger.info(`Task ${taskId} resumed from instruction index ${current_instruction_index}. Continuing queue.`);

                // Step 4: Start queueing the instructions from the saved index
                queueTaskInstructions(taskId, robot_id, instructions, current_instruction_index);

                return res.json({ message: `Task ${taskId} resumed successfully from instruction index ${current_instruction_index}.` });
            });
        });
    });
};

const saveCurrentRobotPosition = (req, res) => {
    const { robotId, locationName } = req.body;

    if (!robotId || !locationName) {
        return res.status(400).json({ message: 'Robot ID and location name are required' });
    }

    // Fetch the latest position of the robot
    const fetchPositionSQL = `
        SELECT x, y, z, theta 
        FROM robot_location 
        WHERE robot_id = ? 
        ORDER BY r_loc_id DESC 
        LIMIT 1;
    `;

    db.query(fetchPositionSQL, [robotId], (err, results) => {
        if (err) {
            logger.error(`Error fetching robot ${robotId} position:`, err);
            return res.status(500).json({ message: 'Database error' });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: 'No recorded position for this robot' });
        }

        const { x, y, z, theta } = results[0];

        // Insert the latest position into the location table
        const insertLocationSQL = `
            INSERT INTO location (robot_id, x, y, z, theta, name) 
            VALUES (?, ?, ?, ?, ?, ?);
        `;

        db.query(insertLocationSQL, [robotId, x, y, z, theta, locationName], (err) => {
            if (err) {
                logger.error(`Error saving location ${locationName} for robot ${robotId}:`, err);
                return res.status(500).json({ message: 'Database error' });
            }

            res.status(201).json({ message: `Location '${locationName}' saved successfully.` });
        });
    });
};

module.exports = { getLocationsByRobotId, saveCurrentRobotPosition, resumeTask, stopTask, startTask, getAllLocations, getCoordinatesByLocation, removeRobotLocationById, getRobotPosition, deleteTask, getRobotTasks, getAllRobots, getRobotLocation, getRobotCallbacks, addInstructionToTask, createRobot, deleteRobot, createTask };
