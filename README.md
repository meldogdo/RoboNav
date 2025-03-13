# RoboNav Project

## Overview
This repository contains the complete RoboNav system, including both the frontend and backend components.

## Technologies Used
- **Backend:** Node.js, Express.js, MySQL
- **Frontend:** [To be added later]
- **Authentication:** JWT (JSON Web Token), Google OAuth2, bcrypt
- **Email Services:** Nodemailer
- **Logging:** Winston
- **Deployment & Monitoring:** Nodemon, Kill-Port

## Project Structure
```
│── /backend
│   │── /config
│   │   │── auth.js         # OAuth2 and JWT setup
│   │   │── db.js           # Database connection setup
│   │
│   │── /controllers
│   │   │── authController.js   # Authentication logic (register, login, reset password)
│   │   │── robotController.js  # Handles robot-related operations
│   │
│   │── /database
│   │   │── backup.sql         # Backup database provided by professor
│   │   │── init.sql           # Updated database schema
│   │
│   │── /logs
│   │   │── app.log            # Server logs (generated automatically)
│   │
│   │── /middleware
│   │   │── authMiddleware.js   # JWT authentication middleware
│   │
│   │── /routes
│   │   │── authRoutes.js       # Routes for authentication
│   │   │── robotRoutes.js      # Routes for robot operations
│   │
│   │── /utils
│   │   │── logger.js       # Winston logger setup
│   │
│   │── .env                # Environment variables
│   │── .gitignore          # Files to ignore in version control
│   │── app.js              # Express application setup
│   │── index.js            # Main server entry point
│   │── nodemon.json        # Nodemon configuration (auto-restart and kill port)
│   │── package.json        # Dependencies and scripts
```

---

# Backend API

## Installation & Setup
### **1. Clone the Repository**
```sh
git clone https://github.com/meldogdo/RoboNav.git
cd RoboNav/backend
```

### **2. Install Dependencies**
```sh
npm install
```

Or use:
```sh
npm run prestart
```
Which will automatically install dependencies before starting the server.

### **3. Set Up Environment Variables**
Create a `.env` file in the `/backend` directory and configure:
```env
# Database Configuration
DB_HOST=
DB_PORT=
DB_USER=
DB_PASSWORD=
DB_NAME=

# Server Configuration
SERVER_HOST=
SERVER_PORT=

# Authentication Configuration
JWT_SECRET=

# Email Configuration
EMAIL_USER=
CLIENT_SECRET=
REFRESH_TOKEN=
```

### **4. Start the Backend Server**
#### **Development Mode (Auto-restart with Nodemon)**
```sh
npm run dev
```
#### **Production Mode**
```sh
npm start
```

## API Endpoints
### **Authentication Routes**
- `POST /api/open/users/register` → Register a new user
- `GET /api/open/users/confirm-email` → Confirm user email
- `POST /api/open/users/login` → User login
- `POST /api/open/users/request-reset` → Request password reset
- `POST /api/open/users/verify-reset` → Verify password reset
- `POST /api/protected/users/reset-password` → Reset password (JWT protected)
- `POST /api/protected/users/change-password` → Change password (JWT protected)

### **Robot Routes (Protected)**
- `GET /api/protected/robot/tasks` → Retrieve robot tasks
- `GET /api/protected/robot/robots` → List all robots
- `GET /api/protected/robot/:robotId/location` → Get robot location
- `GET /api/protected/robot/callbacks` → Get robot callbacks
- `POST /api/protected/robot/instruction` → Send robot instructions

## Logging
The project uses **Winston** for structured logging.
- Logs are stored in `/backend/logs/app.log`.
- Console and file logging are enabled.

---

# Frontend

(To be added later)

---

For further details, refer to the API documentation or contact the development team.