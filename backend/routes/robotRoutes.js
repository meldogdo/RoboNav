const express = require('express');
const {
  // Task-related functions
  getRobotTasks,
  createTask,
  deleteTask,

  // Robot-related functions
  getAllRobots,
  createRobot,
  deleteRobot,
  getRobotLocation,
  sendRobotInstruction,

  // Callback functions
  getRobotCallbacks,

  // Utility functions
  getRobotPosition,
  saveRobotLocation,
  removeAllRobotLocations,
  getCoordinatesByLocation,
  getAllLocations
} = require('../controllers/robotController');
const { authenticateUser } = require('../middleware/authMiddleware');

const router = express.Router();

// Protect all routes under /protected/robot
router.use('/protected/robot', authenticateUser);

// Routes related to creating, deleting, and managing robots.
router.get('/robots', getAllRobots);
router.post('/create', createRobot);
router.delete('/:robotId/delete', deleteRobot);
router.get('/:robotId/location', getRobotLocation);

// Routes for handling robot tasks.
router.get('/tasks', getRobotTasks);
router.post('/task/create', createTask);
router.delete('/task/:taskId/delete', deleteTask);

// Routes for handling robot instructions and callbacks.
router.get('/callbacks', getRobotCallbacks);
router.post('/instruction', sendRobotInstruction);

// Routes for getting and managing robot locations.
router.get('/:robotId/position', getRobotPosition);
router.post('/save-location', saveRobotLocation);
router.delete('/:robotId/remove-locations', removeAllRobotLocations);
router.get('/location/:locationName', getCoordinatesByLocation);
router.get('/locations', getAllLocations);

module.exports = router;
