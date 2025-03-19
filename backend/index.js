require('dotenv').config();
const app = require('./app');
const logger = require('./utils/logger');

const SERVER_PORT = process.env.SERVER_PORT || 8080;
const SERVER_HOST = process.env.SERVER_HOST || "127.0.0.1";

// Starts the server and logs status, exits on failure.
const startServer = async () => {
    try {
        logger.info('Starting server...');

        app.listen(SERVER_PORT, SERVER_HOST, () => {
            logger.info(`Server running on http://${SERVER_HOST}:${SERVER_PORT}`);
        });
    } catch (error) {
        logger.error('Failed to start server', error);
        process.exit(1);
    }
};

startServer();