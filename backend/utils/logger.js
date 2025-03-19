const winston = require('winston');

// Logger with timestamped messages, console, and file output
const logger = winston.createLogger({
  level: 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.printf(({ timestamp, level, message }) => `${timestamp} [${level.toUpperCase()}]: ${message}`)
  ),
  transports: [
    new winston.transports.Console(), // Output to console
    new winston.transports.File({ filename: 'logs/app.log' }) // Log file storage
  ]
});

module.exports = logger;