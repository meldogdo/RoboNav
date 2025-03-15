const express = require('express');
const {
  getRobotTasks,
  getAllRobots,
  getRobotLocation,
  getRobotCallbacks,
  sendRobotInstruction,
  createRobot,
  deleteRobot,
  createTask,
  deleteTask
} = require('../controllers/robotController');
const { authenticateUser } = require('../middleware/authMiddleware');

const router = express.Router();

// Robot Routes (All routes require authentication)
router.use('/protected/robot', authenticateUser); 

router.get('/protected/robot/tasks', getRobotTasks);
router.get('/protected/robot/robots', getAllRobots);
router.get('/protected/robot/:robotId/location', getRobotLocation);
router.get('/protected/robot/callbacks', getRobotCallbacks);
router.post('/protected/robot/instruction', sendRobotInstruction);
router.post('/protected/robot/create', createRobot);
router.post('/protected/robot/task/create', createTask); 
router.delete('/protected/robot/task/:taskId/delete', deleteTask); 
router.delete('/protected/robot/:robotId/delete', deleteRobot);

module.exports = router;
