const jwt = require('jsonwebtoken');
const dotenv = require('dotenv');

dotenv.config();
const SECRET_KEY = process.env.JWT_SECRET || 'your_jwt_secret';

// Middleware to verify JWT and authenticate user
const authenticateUser = (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1]; // Extract token
    if (!token) return res.status(401).json({ error: 'Access denied. No token provided.' });

    jwt.verify(token, SECRET_KEY, (err, decoded) => {
        if (err) return res.status(403).json({ error: 'Invalid or expired token' });
        req.user = decoded; // Attach user data to request
        next();
    });
};

module.exports = { authenticateUser };