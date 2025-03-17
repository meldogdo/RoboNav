const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const authRoutes = require('./routes/authRoutes');
const robotRoutes = require('./routes/robotRoutes');
const logger = require('./utils/logger');

dotenv.config();

const app = express();
const db = require('./config/db');
// Middleware
app.use(cors({
    origin: '*', 
    methods: ['GET', 'POST', 'PUT', 'DELETE'],
    allowedHeaders: ['Content-Type', 'Authorization']
}));
app.use(express.json());

// Logging Middleware
app.use((req, res, next) => {
    logger.info(`${req.method} ${req.url}`);
    next();
});

// Routes
app.use('/api', authRoutes);
app.use('/api', robotRoutes);

// Health Check Endpoint
app.get('/health', (req, res) => {
    res.json({ status: 'Server is running' });
});

// Auto-cleanup expired OTPs and unconfirmed users every hour
setInterval(() => {
    // Delete users who have not confirmed their email before expiration
    db.query(
        `DELETE FROM users 
        WHERE id IN (SELECT user_id FROM email_confirmations WHERE expires_at < NOW())`,
        (err, result) => {
            if (err) console.error('Error deleting unconfirmed users:', err);
            else console.log(`${result.affectedRows} unconfirmed users deleted`);
        }
    );

    // Delete expired password reset tokens separately
    db.query('DELETE FROM password_reset_tokens WHERE expires_at < NOW()', (err) => {
        if (err) console.error('Error clearing expired tokens:', err);
        else console.log('Expired reset tokens cleared');
    });

}, 3600000); // Runs every hour (3600000 ms)

module.exports = app;