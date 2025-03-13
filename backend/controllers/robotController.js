const db = require('../config/db');
const logger = require('../utils/logger');

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

// Get robot callbacks
const getRobotCallbacks = (req, res) => {
  const { ins_id } = req.query;

  if (!ins_id) {
      logger.warn('Missing instruction ID (ins_id) in request.');
      return res.status(400).json({ message: 'Instruction ID (ins_id) is required' });
  }

  logger.info(`Fetching callbacks for instruction ID: ${ins_id}...`);

  const query = `
      SELECT cr.*, r.type, r.ip_add, r.port, r.battery, r.is_charging
      FROM callback_rec cr
      JOIN robot r ON cr.robot_id = r.robot_id
      WHERE cr.ins_id = ?
      ORDER BY cr.ctime ASC
  `;

  db.query(query, [ins_id], (err, results) => {
      if (err) {
          logger.error(`Database error while fetching callbacks for instruction ID ${ins_id}:`, err);
          return res.status(500).json({ message: 'Database error', error: err });
      }

      if (results.length === 0) {
          logger.warn(`No callbacks found for instruction ID ${ins_id}.`);
          return res.status(404).json({ message: 'No callbacks found for this instruction ID' });
      }

      // Format results with robot details
      const callbackMessages = results.map(callback =>
          `#${callback.cb_id} - Robot ${callback.robot_id} (${callback.type}) [${callback.ip_add}:${callback.port}] - ${callback.callback} [${callback.ctime}] | Battery: ${callback.battery}% | Charging: ${callback.is_charging ? 'Yes' : 'No'}`
      );

      logger.info(`Successfully retrieved ${callbackMessages.length} callbacks for instruction ID ${ins_id}.`);
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
      INSERT INTO ins_send (robot_id, instruction, status)
      VALUES (?, ?, 'order')
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

module.exports = { getRobotTasks, getAllRobots, getRobotLocation, getRobotCallbacks, sendRobotInstruction };
