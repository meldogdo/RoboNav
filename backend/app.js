const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const authRoutes = require('./routes/authRoutes');
const robotRoutes = require('./routes/robotRoutes');
const logger = require('./utils/logger');
const db = require('./config/db');

dotenv.config();

// Initializes Express, sets up middleware, routes, logging, and automated cleanup tasks.
const app = express();

// Middleware
app.use(cors({ origin: '*', methods: ['GET', 'POST', 'PUT', 'DELETE'], allowedHeaders: ['Content-Type', 'Authorization'] }));
app.use(express.json());

// Request logging
app.use((req, res, next) => {
    logger.info(`${req.method} ${req.url}`);
    next();
});

// Routes
app.use('/api', authRoutes);
app.use('/api', robotRoutes);

// Health check endpoint
app.get('/health', (req, res) => res.json({ status: 'Server is running' }));

// Cleanup expired OTPs and unconfirmed users hourly
setInterval(() => {
    db.query(
        `DELETE FROM users WHERE id IN (SELECT user_id FROM email_confirmations WHERE expires_at < NOW())`,
        (err, result) => {
            if (err) logger.info('Error deleting unconfirmed users:', err);
            else logger.info(`${result.affectedRows} unconfirmed users deleted`);
        }
    );

    db.query('DELETE FROM password_reset_tokens WHERE expires_at < NOW()', (err) => {
        if (err) logger.info('Error clearing expired tokens:', err);
        else logger.info('Expired reset tokens cleared');
    });

}, 3600000); // Runs every hour

module.exports = app;