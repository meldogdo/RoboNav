const db = require('../config/db');
const logger = require('../utils/logger');

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

module.exports = { getRobotTasks, getAllRobots, getRobotLocation, getRobotCallbacks, sendRobotInstruction, createRobot };
