# RoboNav Project

## Overview
This repository contains the complete **RoboNav** system, including both the **frontend** (Android app) and **backend** (Express API and MySQL database). The system facilitates robot navigation, task assignment, and map management through a mobile interface.

---

## Technologies Used

### **Backend:**
- **Server:** Node.js, Express.js
- **Database:** MySQL
- **Authentication:** JWT (JSON Web Token), bcrypt
- **Email Services:** Nodemailer
- **Logging:** Winston
- **Deployment & Monitoring:** Nodemon, Kill-Port

### **Frontend:**
- **Framework:** Android (Java, XML)
- **Networking:** Volley (for API calls)
- **UI Components:** EditText, TextView, RecyclerView, Toasts
- **Authentication:** JWT (retrieved from backend)
- **Error Handling:** Custom error messages parsed from API responses

---

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
│
│── /frontend
│   │── /app/src/main/java/com/robonav/app
│   │   ├── activities/       # Main app screens (Login, Signup, Dashboard)
│   │   ├── adapters/         # Custom adapters for RecyclerViews & lists
│   │   ├── fragments/        # Modular UI components for better UX
│   │   ├── interfaces/       # API callbacks & event handling
│   │   ├── models/           # Data models representing API responses
│   │   ├── utilities/        # Helper functions (Validation, API Requests, etc.)
│   │
│   │── /app/src/main/res/layout    # XML UI layouts for activities & fragments
│   │
│   │── /app/src/main/res/drawable  # Icon assets
│
│   │── AndroidManifest.xml         # Android app permissions & settings
│   │── build.gradle                # Gradle configuration & dependencies
│   │── settings.gradle             # Project-level Gradle settings
│
│── README.md  # This documentation
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

# Frontend (Android App)

## Installation & Setup

### **1. Clone the Repository**
```sh
git clone https://github.com/meldogdo/RoboNav.git
cd RoboNav/frontend
```

### **2. Open in Android Studio**
1. Open **Android Studio**.
2. Select **Open an Existing Project**.
3. Navigate to `RoboNav/frontend` and open it.

### **3. Build & Run**
1. Ensure **USB debugging** is enabled on your Android device or use the Android Emulator.
2. Click **Run (▶️)** in Android Studio.

### **4. Configure API URL**
By default, the app fetches the backend URL from a configuration file:

#### **Edit `/app/src/main/res/raw/config.properties`**
```properties
# Backend Configuration
BACKEND_IP=10.0.2.2
BACKEND_PORT=8080
USE_HTTPS=false
```
- Modify `BACKEND_IP` to match your backend host.
- Change `BACKEND_PORT` if your backend runs on a different port.
- Set `USE_HTTPS=true` if using HTTPS instead of HTTP.

If the backend is running on **HTTP** locally, use the default `10.0.2.2` as the hostname.

## Features
- **User Authentication** (Register, Login, JWT-based sessions)
- **Robot Management** (View tasks, locations, and callbacks)
- **Instruction Handling** (Send instructions to robots)
- **Map Upload & Retrieval** (Upload and retrieve robot map files)

---

## Contributors
- Christopher Higgins
- Bryson Crook
- Mohamed El Dogdog
