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

// Auto-cleanup expired OTPs every hour
setInterval(() => {
    db.query('DELETE FROM password_reset_tokens WHERE expires_at < NOW()', (err) => {
        if (err) console.error('Error clearing expired tokens:', err);
        else console.log('Expired reset tokens cleared');
    });
    db.query('DELETE FROM email_confirmations WHERE expires_at < NOW()', (err) => {
        if (err) console.error('Error clearing expired email confirmations:', err);
        else console.log('Expired email confirmations cleared');
    });
}, 3600000); 

module.exports = app;