# RoboNav Project

## Overview
This repository contains the complete **RoboNav** system, including both the **frontend** (Android app) and **backend** (Express API and MySQL database). The system facilitates robot navigation, task assignment, and map management through a mobile interface.

---

## Technologies Used

### **Backend:**
- **Server:** `Node.js` with `Express.js` for handling API requests.  
- **Database:** `MySQL` for data storage, managed with `mysql2`.  
- **Authentication:** `JWT (JSON Web Token)` for secure user authentication, with password hashing via `bcrypt`.  
- **Email Services:** `Nodemailer` for sending registration confirmation and password reset emails.  
- **Logging:** `Winston` for structured logging and error tracking.  
- **Development Tools:**  
  - `Nodemon` for live server reloading during development.  
  - `Kill-Port` for freeing up ports before restarting the server.  

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
│   │   │── auth.js         # OAuth2 and JWT configuration  
│   │   │── db.js           # MySQL database connection setup  
│   │
│   │── /controllers
│   │   │── authController.js   # Handles user authentication (register, login, password reset)  
│   │   │── robotController.js  # Manages robot tasks, locations, and instructions  
│   │
│   │── /database
│   │   │── backup.sql         # Backup database provided by professor  
│   │   │── init.sql           # Initial database schema and setup script  
│   │
│   │── /logs
│   │   │── app.log            # Application logs (generated automatically)  
│   │
│   │── /middleware
│   │   │── authMiddleware.js   # Middleware for JWT authentication  
│   │
│   │── /routes
│   │   │── authRoutes.js       # Defines routes for authentication endpoints  
│   │   │── robotRoutes.js      # Defines routes for robot management and tasks  
│   │
│   │── /utils
│   │   │── logger.js       # Winston logger configuration for structured logging  
│   │
│   │── .env                # Environment variables for sensitive configurations  
│   │── .gitignore          # Specifies files to exclude from version control  
│   │── app.js              # Express application setup and middleware configuration  
│   │── index.js            # Main server entry point  
│   │── nodemon.json        # Nodemon settings for auto-restart and port cleanup  
│   │── package.json        # Project dependencies and scripts  
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
This will automatically install dependencies before starting the server.

### **3. Set Up Environment Variables**
Create a `.env` file in the `/backend` directory and configure:
```env
# Database Configuration
DB_HOST=             # Database hostname or IP (default: localhost)
DB_PORT=             # Database port (default: 3306 for MySQL)
DB_USER=             # Database username
DB_PASSWORD=         # Database password
DB_NAME=             # Name of the database

# Server Configuration
SERVER_HOST=         # Server host (default: 127.0.0.1 for local)
SERVER_PORT=         # Port for the backend server (default: 8080)

# Authentication Configuration
JWT_SECRET=         # Secret key for signing JWT tokens

# Email Configuration
EMAIL_USER=         # Email used for sending registration & password reset emails
CLIENT_ID=          # OAuth2 Client ID from Google Cloud Console
CLIENT_SECRET=      # OAuth2 Client Secret from Google Cloud Console
REFRESH_TOKEN=      # OAuth2 Refresh Token from Google OAuth Playground (for Gmail API)
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

## **API Endpoints**

### **Authentication Routes**
- `POST /api/open/users/register` → Register a new user  
- `GET /api/open/users/confirm-email` → Confirm user email via token  
- `POST /api/open/users/login` → Authenticate user and return JWT  
- `POST /api/open/users/request-reset` → Request a password reset email  
- `POST /api/open/users/verify-reset` → Verify reset code before setting a new password  
- `POST /api/protected/users/reset-password` → Reset password (JWT required)  
- `POST /api/protected/users/change-password` → Change password (JWT required)  

### **Robot Routes (Protected)**
- `GET /api/protected/robot/tasks` → Retrieve all robot tasks  
- `POST /api/protected/robot/task/create` → Create a new robot task  
- `DELETE /api/protected/robot/task/:taskId/delete` → Delete a robot task  
- `POST /api/protected/robot/task/:taskId/start` → Start a robot task  
- `POST /api/protected/robot/task/:taskId/stop` → Stop a running robot task  
- `POST /api/protected/robot/task/:taskId/resume` → Resume a stopped robot task  
- `POST /api/protected/robot/task/instruction` → Add an instruction to a task  

### **Robot Management Routes (Protected)**
- `GET /api/protected/robot/robots` → List all registered robots  
- `POST /api/protected/robot/create` → Register a new robot  
- `DELETE /api/protected/robot/:robotId/delete` → Remove a robot from the system  
- `GET /api/protected/robot/:robotId/location` → Retrieve the latest location of a robot  

### **Location & Position Routes (Protected)**
- `GET /api/protected/robot/:robotId/position` → Retrieve the last recorded position of a robot  
- `POST /api/protected/robot/save-current-position` → Save the robot’s current position  
- `DELETE /api/protected/location/:locId` → Remove a saved location by ID  
- `GET /api/protected/robot/location/:locId` → Get coordinates by location ID  
- `GET /api/protected/robot/:robotId/locations` → List all locations assigned to a robot  
- `GET /api/protected/robot/locations` → Retrieve all saved locations  

### **Robot Callback Routes (Protected)**
- `GET /api/protected/robot/callbacks` → Retrieve callback events from robots  

## **Logging & Error Handling**
The project uses **Winston** for structured logging to track application events and errors.  
- Logs are stored in `/backend/logs/app.log`.  
- Both **console logging** and **file logging** are enabled.  
- Errors and important events are recorded for debugging and monitoring.  

## **Error Handling**
The backend includes structured error handling to ensure stability and meaningful API responses:  
- **Consistent Error Responses:** API errors follow a standardized format with HTTP status codes.  
- **Database & Query Errors:** Logged and handled gracefully to avoid system crashes.  
- **Unhandled Exceptions:** Caught by a global error handler to prevent app failures.  
- **Authentication Errors:** Unauthorized requests return proper `401` or `403` responses.  


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
