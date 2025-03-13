const express = require('express');
const {
  getRobotTasks,
  getAllRobots,
  getRobotLocation,
  getRobotCallbacks,
  sendRobotInstruction
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

module.exports = router;