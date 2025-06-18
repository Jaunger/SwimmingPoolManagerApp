# Swimming Pool Manager (Android)

An Android client for the Swimming Pool Manager system.  
Allows you to:

- 📋 **Manage students** (Create, Read, Update, Delete)  
- 📅 **View & generate weekly lesson schedules**  

This client communicates with a RESTful backend (see [swimming-pool-backend](https://github.com/yourorg/swimming-pool-backend)) over Retrofit.

---

## Table of Contents

1. [Features](#features)  
2. [Getting Started](#getting-started)  
   1. [Prerequisites](#prerequisites)  
   2. [Installation](#installation)  
   3. [Running the App](#running-the-app)  
3. [Architecture](#architecture)  
4. [Key Screenshots](#key-screenshots)  
5. [API Endpoints](#api-endpoints)  
6. [Contributing](#contributing)  
7. [License](#license)  

---

## Features

- **Main Dashboard**  
  - Navigate to *Manage Students* or *View Schedule*.  
- **Student Hub**  
  - List of all students with edit & delete actions (via `StudentHubActivity`).  
  - FloatingActionButton to add a new student.  
- **Add / Edit Student**  
  - Form with first/last name, swimming types (checkboxes), lesson preference (spinner), **Cancel** & **Submit**.  
  - In edit mode, loads existing data and performs `PUT /students/{id}`.  
- **Schedule Viewer**  
  - Dropdown for weekday selection.  
  - Gantt-style grid from 08:00–20:00.  
  - Dynamically calculated columns (only up to last lesson).  
  - **Generate** & **Clear** buttons with loading spinner (`ProgressBar`).  
  - Issues dialog combining unscheduled students + warnings.  

---

## Getting Started

### Prerequisites

- Android Studio 4.0+  
- Android SDK API 21+  
- A running instance of the [backend API](https://github.com/yourorg/swimming-pool-backend) on `http://10.0.2.2:3000/api/`

### Installation

1. **Clone the repo**  
   ```bash
   git clone https://github.com/yourorg/swimming-pool-android.git
   cd swimming-pool-android
   ```
2. **Open in Android Studio**  
   - Choose “Open an existing Android Studio project” and select this folder.

3. **Sync Gradle**  
   - Let Gradle download dependencies (Retrofit, Gson, Material components, etc.)

### Running the App

1. Launch the **backend** locally or on a device/emulator.
2. In Android Studio, press **Run** (Shift+F10) on your connected device or emulator.
3. On the main screen, use the buttons to navigate to student hub or schedule.

---

## Architecture

- **Activities**  
  - `MainActivity`  
  - `StudentHubActivity` ↔ `StudentAdapter`  
  - `AddStudentActivity`  
  - `ScheduleActivity`
- **Network**  
  - `ApiClient` (Retrofit builder)  
  - `PoolManagerService` (interface with `@GET`, `@POST`, etc.)  
- **Entities**  
  - `Student`, `Schedule` (Gson-annotated data classes)  
- **UI**  
  - XML layouts in `res/layout/`  
  - Material theming in `res/values/colors.xml`, `styles.xml`

---

## Key Screenshots
| Main Screen | Student Hub | Add/Edit Student | Schedule View |
|:----------:|:-----------:|:----------------:|:-------------:|
| <img src="https://github.com/user-attachments/assets/7daad287-e81e-4fe5-9446-bf096ff3ece1" width="200"/> | <img src="https://github.com/user-attachments/assets/e84a03ab-0fb1-4577-88a5-88007c2710b8" width="200"/> | <img src="https://github.com/user-attachments/assets/c86ebcab-ec64-4fca-9fa1-e6852f915684" width="200"/> | <img src="https://github.com/user-attachments/assets/88cf92c0-e09f-43db-a0b0-a58f98052e29" width="200"/> |
---

## API Endpoints

All calls go to `BASE_URL = http://10.0.2.2:3000/api/`:

| Method | Path                    | Description                         |
|--------|-------------------------|-------------------------------------|
| GET    | `/students/all`         | List all students                   |
| GET    | `/students/{id}`        | Get a student by ID                 |
| POST   | `/students`             | Create new student                  |
| PUT    | `/students/{id}`        | Update existing student             |
| DELETE | `/students/{id}`        | Delete student (min 1 must remain)  |
| GET    | `/schedule`             | Fetch latest saved schedule         |
| POST   | `/schedule/generate`    | Generate a new schedule run         |
| DELETE | `/schedule/clear`       | Clear schedule & records            |
| GET    | `/schedule/issues`      | Get last run’s unscheduled + issues |

See the backend README for full details.

---

## Contributing

1. Fork this repository  
2. Create a feature branch (`git checkout -b feature/my-feature`)  
3. Commit your changes (`git commit -m 'Add amazing feature'`)  
4. Push to the branch (`git push origin feature/my-feature`)  
5. Open a Pull Request  

---

## License

MIT © Daniel Raby
