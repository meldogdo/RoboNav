# **RoboNav: Intelligent Robot Navigation & Task Automation**

## **Overview**  
RoboNav is an **intelligent task execution and navigation system** designed to enhance the efficiency of **autonomous service robots** used in **hospitality, retail, and logistics**. The project builds upon an **existing infrastructure** where **commercial robots** (e.g., **OrionStar and other service robots**) feature **built-in Android-based control systems** that communicate with an **existing Flask-based web application** hosted on an external server with a **database backend**.  

To **extend and optimize this ecosystem**, RoboNav introduces:  
- A **mobile application** for assigning tasks, queuing navigation requests, and monitoring robot activity.  
- A **dedicated Node.js backend service** that acts as a bridge between the **mobile app and the Flask-based system**, ensuring **real-time synchronization, secure task execution, and seamless data flow**.  
- **Automated task execution and multi-robot coordination** for optimized navigation and workload distribution in high-traffic environments.  

By integrating with the **Flask-based server**, RoboNav ensures **efficient communication** between the **mobile app, robots, and web-based control interface**, improving overall **operational efficiency** across multiple locations.  

This project is developed under the supervision of **Professor Yili Tang**.  


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

### **Frontend**
- **Framework:** `Android (Java, XML)` – Native Android development using Java with XML for UI layouts.  
- **Networking:** `Volley` – Handles API requests and responses efficiently.  
- **UI Components:** `EditText`, `TextView`, `RecyclerView`, `Toasts` – Standard Android UI elements for user interaction.  
- **Authentication:** `JWT (JSON Web Token)` – Token-based authentication retrieved from the backend and securely stored using `EncryptedSharedPreferences`.  
- **Error Handling:** Custom error messages parsed from API responses for better UX.  
- **Dependency Management:** `Gradle` – Handles third-party libraries and project configurations.  
- **JSON Parsing:** `org.json` – Parses API responses into structured data.  

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
│   │   ├── activities/       # App screens for navigation & user interaction
│   │   │   ├── ChangePasswordActivity    # Allows users to change their password
│   │   │   ├── CreateRobotActivity       # Screen to register a new robot
│   │   │   ├── CreateTaskActivity        # Screen to create tasks for robots
│   │   │   ├── ForgotPasswordActivity    # Handles password reset requests
│   │   │   ├── HomeActivity              # Initial login screen
│   │   │   ├── MainActivity              # Main dashboard containing navigation
│   │   │   ├── ResetPasswordActivity     # Handles resetting password after verification
│   │   │   ├── SignUpActivity            # User registration screen
│   │   │
│   │   ├── adapters/         # Custom adapters for mapping backend data to UI components
│   │   │   ├── RobotAdapter  # Handles displaying robots in lists/RecyclerView
│   │   │   ├── TaskAdapter   # Handles displaying tasks in lists/RecyclerView
│   │   │
│   │   ├── fragments/        # Modular UI components for navigation & content
│   │   │   ├── HomeFragment          # Displays main home dashboard functionality
│   │   │   ├── NavigationFragment    # Manages navigation controls & UI
│   │   │   ├── UtilitiesFragment     # Contains additional user tools & features
│   │   │
│   │   ├── interfaces/       # Event listeners for UI updates
│   │   │   ├── OnUpdateListener  # Handles updating data on specific events
│   │   │
│   │   ├── models/           # Data models representing API responses
│   │   │   ├── Robot    # Defines robot structure as a Java object
│   │   │   ├── Task     # Defines task structure as a Java object
│   │   │
│   │   ├── utilities/        # Helper functions for various app features
│   │   │   ├── ConfigManager        # Reads `config.properties` for backend URL & settings
│   │   │   ├── FragmentUtils        # Manages fragment transitions & UI functions
│   │   │   ├── JsonUtils            # Converts JSON API responses into Java models
│   │   │   ├── VolleySingleton      # Ensures single API request instance when needed
│   │
│   │── /app/src/main/res/layout/       # XML UI layouts for activities & fragments
│   │── /app/src/main/res/drawable/     # Image assets (icons, backgrounds, etc.)
│   │── /app/src/main/assets/config.properties  # Backend server IP, port, and protocol (HTTP/HTTPS)
│
│   │── AndroidManifest.xml         # Defines app permissions, activities, and services
│   │── build.gradle                # Gradle dependencies and project configurations
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

### **3. Configure API URL**
By default, the app fetches the backend URL from a configuration file:

#### **Edit `/app/src/main/res/raw/config.properties`**
```properties
# Backend Configuration
BACKEND_IP=10.0.2.2
BACKEND_PORT=8080
USE_HTTPS=false
```
- **`BACKEND_IP`** → Change this to match your backend server's IP.  
- **`BACKEND_PORT`** → Adjust this if your backend is running on a different port.  
- **`USE_HTTPS`** → Set to `true` if your backend uses HTTPS.  

**For Local Backend on Emulator**  
If using an **Android Emulator**, keep `BACKEND_IP=10.0.2.2` (this routes to `localhost`).  

**For Local Backend on a Real Device**  
If testing on a **physical device**, update `BACKEND_IP` to your **computer's local network IP** (e.g., `192.168.x.x`).  

### **4. Build & Run**
1. Ensure **USB debugging** is enabled on your Android device or use the Android Emulator.
2. Click **Run (▶️)** in Android Studio.


## **App Overview & Features**

### **1. Authentication & User Management**
- **Login:**  
  - The app starts with `MainActivity`, which immediately opens `HomeActivity` (Login Screen).  
  - Users can log in using **admin/password** as default credentials or create a new account.  
- **Registration:**  
  - Users register by providing an **email, username, and password**.  
  - A **confirmation link** is sent via email, and clicking it enables login.  
- **Forgot Password:**  
  - Users can request a **one-time password (OTP)** via email if they forget their password.  
  - Entering the OTP logs the user in and allows them to set a new password.  
- **Post-Login Actions:**  
  - Upon logging in, users are directed to `HomeFragment`, where they can **log out** or **change their password**.

---

### **2. Home Fragment (Main Dashboard)**
- **Robot & Task Management:**
  - The **HomeFragment** displays two sliders: **one for robots** and **one for tasks**.
  - Users can create **robots** or **tasks** using respective buttons.  
  - Clicking a **robot** or **task** opens a **popup** with actions:  
    - **For Robots:** View details and delete.  
    - **For Tasks:** Start, stop, resume, delete (depending on its current state).  
    - **Task Details:** Displays **currently queued instructions**, providing insight into what the robot will execute next.  
- **Automatic Data Refresh:**
  - When switching between fragments or performing **CRUD operations** (create, delete, update), the UI automatically refreshes.

---

### **3. Navigation Fragment (Task Execution)**
- **Task Selection & Navigation:**
  - Users select a **task from a dropdown** and **assign locations** to it.  
  - Tasks created in `HomeFragment` contain only a **name and robot ID** (a shell task).  
  - After selecting a **task and location**, users press the **"Queue Navigation"** button to add the location to the task’s execution queue.  
- **Executing a Task:**
  - Returning to `HomeFragment`, the user can **start, stop, resume, or delete a task**.  
  - **Resume** continues from the last recorded execution step.  
  - Tasks can only be deleted when **completed, stopped, or not started**.  

---

### **4. Utilities Fragment (Location Management)**
- **Location-Based Utilities:**
  - `UtilitiesFragment` provides **three location management tools**:
    1. **Save Current Robot Location:** Saves the robot’s current coordinates.  
    2. **Delete a Robot’s Locations:** Remove specific stored locations.  
    3. **Get Coordinates of a Location:** Retrieve GPS data for a saved location.  
- **System Callbacks:**
  - An **output box** displays real-time **system callbacks** from all robots.  
  - Users can **filter callbacks** to view logs for a **specific robot**.

---

## Contributors
- Christopher Higgins
- Bryson Crook
- Mohamed El Dogdog
