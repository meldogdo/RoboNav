const express = require('express');
const {
  registerUser,
  confirmEmail,
  loginUser,
  requestPasswordReset,
  verifyReset,
  resetPassword,
  changePassword
} = require('../controllers/authController');
const { authenticateUser } = require('../middleware/authMiddleware');

const router = express.Router();

// Open Authentication Routes (No authentication required)
router.post('/open/users/register', registerUser);
router.get('/open/users/confirm-email', confirmEmail);
router.post('/open/users/login', loginUser);
router.post('/open/users/request-reset', requestPasswordReset);
router.post('/open/users/verify-reset', verifyReset);

// Protected Authentication Routes (Require authentication)
router.use('/protected/users', authenticateUser);
router.post('/protected/users/reset-password', resetPassword);
router.post('/protected/users/change-password', changePassword);

module.exports = router;
