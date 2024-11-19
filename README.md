# RoboNav Mobile App

RoboNav is an Android mobile application designed to control and monitor OrionStar autonomous robots remotely. This app integrates with the OrionStar SDK to provide real-time robot navigation, control functionalities, and status monitoring through an intuitive mobile interface.

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Technologies Used](#technologies-used)
4. [Installation](#installation)
5. [Usage](#usage)
6. [Project Structure](#project-structure)
7. [Contributors](#contributors)
8. [License](#license)

---

### Overview

The RoboNav Mobile App is developed as part of SE4450 Software Engineering Design Project by Team 7. It enables users to interact with OrionStar autonomous robots through the OrionStar SDK, supporting real-time status updates, navigation controls, and remote monitoring. This mobile interface offers a user-friendly platform for controlling robotic operations effectively.

### Features

- **Real-Time Navigation Controls**: Send commands to control robot movement to specific points or through preset routes.
- **Status Monitoring**: View real-time updates on robot status, location, and battery levels.
- **Robot Management**: Seamlessly connect and disconnect robots from the mobile device.
- **Error Alerts**: Receive notifications for any errors or issues in robot operation.

### Technologies Used

- **Android SDK**: For building a native Android application.
- **OrionStar SDK**: Integrates robot control and monitoring functionalities.
- **Java/Kotlin**: Primary programming languages for the Android app.
- **Firebase (Optional)**: Used for logging and analytics.

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/meldogdo/RoboNav.git
   cd RoboNav
   
2. **Set Up the OrionStar SDK**:
Follow the OrionStar SDK setup guide to integrate the SDK. Place your SDK files in the specified directory in the project.

3. **Build the Project**: Open the project in Android Studio. Sync Gradle and build the app.

4. **Disconnect**: Safely disconnect from the robot through the app’s menu when monitoring or control is complete.

### Project Structure

- `src/main/java/com/robogroup/robonav`: Contains main app source code files.
- `src/main/res`: UI layouts and resources.
- `libs/orionstar-sdk`: OrionStar SDK files.
- `build.gradle`: Project dependencies and SDK integration.


### Contributors

- Bryson Crook (bcrook4@uwo.ca)
- Christopher Higgins (chiggi24@uwo.ca)
- Mohamed El Dogdog (meldogdo@uwo.ca)
- Seth Langendoen (slangend@uwo.ca)

### License

This project is for academic and educational use under the SE4450 course. For licensing details, please refer to [LICENSE](https://github.com/yourusername/yourrepo/blob/main/LICENSE).

