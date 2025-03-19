const mysql = require('mysql2');
const dotenv = require('dotenv');
const fs = require('fs');
const path = require('path');
const logger = require('../utils/logger');

dotenv.config();

// Configure MySQL connection
const db = mysql.createConnection({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_NAME || 'your_database_name',
  port: process.env.DB_PORT || 3306,
  multipleStatements: true, // Allow multiple SQL statements
});

// Establish connection
db.connect((err) => {
  if (err) {
    logger.error('Database connection failed:', err.stack);
    return;
  }
  logger.info('Database connected successfully');

  // Initialize database on server restart
  initializeDatabase();
});

// Load and execute SQL dump
function initializeDatabase() {
  const sqlDumpPath = path.join(__dirname, '../database/init.sql');

  try {
    const sqlDump = fs.readFileSync(sqlDumpPath, 'utf8');

    db.query(sqlDump, (err) => {
      if (err) {
        logger.error('Error executing SQL dump:', err);
      } else {
        logger.info('Database initialized successfully');
      }
    });
  } catch (fileErr) {
    logger.error('Error reading SQL file:', fileErr);
  }
}

module.exports = db;