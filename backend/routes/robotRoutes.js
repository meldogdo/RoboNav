const express = require('express');
const { authenticateUser } = require('../middleware/authMiddleware');
const {
  // Task-related functions
  getRobotTasks,
  createTask,
  deleteTask,
  startTask,
  stopTask,
  resumeTask,
  addInstructionToTask,

  // Robot-related functions
  getAllRobots,
  createRobot,
  deleteRobot,
  getRobotLocation,

  // Location-related functions
  getRobotPosition,
  saveCurrentRobotPosition,
  removeRobotLocationById,
  getCoordinatesByLocation,
  getAllLocations,
  getLocationsByRobotId,

  // Callback functions
  getRobotCallbacks
} = require('../controllers/robotController');

const router = express.Router();

// Protect all routes under /protected/robot
router.use('/protected/robot', authenticateUser);

// Task Routes
router.get('/protected/robot/tasks', getRobotTasks);
router.post('/protected/robot/task/create', createTask);
router.delete('/protected/robot/task/:taskId/delete', deleteTask);
router.post('/protected/robot/task/:taskId/start', startTask);
router.post('/protected/robot/task/:taskId/stop', stopTask);
router.post('/protected/robot/task/:taskId/resume', resumeTask);
router.post('/protected/robot/task/instruction', addInstructionToTask);

// Robot Routes
router.get('/protected/robot/robots', getAllRobots);
router.post('/protected/robot/create', createRobot);
router.delete('/protected/robot/:robotId/delete', deleteRobot);
router.get('/protected/robot/:robotId/location', getRobotLocation);

// Location & Position Routes
router.get('/protected/robot/:robotId/position', getRobotPosition);
router.post('/protected/robot/save-current-position', saveCurrentRobotPosition);
router.delete('/protected/location/:locId', removeRobotLocationById);
router.get('/protected/robot/location/:locId', getCoordinatesByLocation);
router.get('/protected/robot/:robotId/locations', getLocationsByRobotId);
router.get('/protected/robot/locations', getAllLocations);

// Callback Routes
router.get('/protected/robot/callbacks', getRobotCallbacks);

module.exports = router;