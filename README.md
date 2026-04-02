# SafeTrack — Bus Booking & Tracking System

A Java desktop application for bus booking and tracking, with separate dashboards for admins and passengers. Built with JavaFX and MySQL.

---

## Features

### Admin Dashboard
- Add, edit, and delete buses and routes
- View and manage all bookings
- Monitor passenger records
- Send and handle emergency alerts
- View dashboard statistics

### Passenger Dashboard
- Register and log in securely
- Search available buses by route
- Book and cancel tickets
- View personal booking history
- Receive emergency alerts

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| UI | JavaFX |
| Database | MySQL 8.0+ |
| IDE | IntelliJ IDEA |
| Architecture | MVC (Model-View-Controller) |

---

## Project Structure

```
SafeTrack-Project/
├── src/com/safetrack/
│   ├── controller/     → Business logic (handles user actions)
│   ├── dao/            → Database access (CRUD operations)
│   ├── model/          → Data classes (Bus, Booking, User, etc.)
│   ├── service/        → Service layer (rules & coordination)
│   ├── view/           → JavaFX screens (FXML/UI)
│   └── util/           → DB connection & session utilities
├── resources/          → FXML layouts, icons, stylesheets
├── schema.sql          → Database setup script
└── README.md
```

---

## Setup Instructions

### Prerequisites

Make sure you have the following installed:
- [Java JDK 17+](https://adoptium.net/)
- [JavaFX SDK](https://openjfx.io/)
- [MySQL 8.0+](https://dev.mysql.com/downloads/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)

---

### Step 1 — Clone the repository

```bash
git clone https://github.com/snehathakur1907-dev/SafeTrack-Project.git
cd SafeTrack-Project
```

### Step 2 — Set up the database

1. Open **MySQL Workbench** or your MySQL client
2. Run the schema file to create all tables:

```bash
mysql -u root -p < schema.sql
```

Or paste the contents of `schema.sql` directly into MySQL Workbench and execute.

### Step 3 — Configure the database connection

Open `src/com/safetrack/util/DBConnection.java` and update your credentials:

```java
private static final String URL = "jdbc:mysql://localhost:3306/safetrack_db";
private static final String USER = "root";         // your MySQL username
private static final String PASSWORD = "yourpassword"; // your MySQL password
```

### Step 4 — Add JavaFX to IntelliJ

1. Open the project in IntelliJ IDEA
2. Go to **File → Project Structure → Libraries**
3. Click **+** and add your JavaFX SDK `lib` folder
4. Go to **Run → Edit Configurations**
5. Add the following VM options:

```
--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
```

### Step 5 — Run the app

Run `Main.java` — the login screen will open.

**Default admin credentials:**
```
Username: admin
Password: admin123
```

---

## Database Schema

See `schema.sql` for the full script. Here's an overview of the tables:

| Table | Description |
|---|---|
| `users` | Stores passenger accounts |
| `admins` | Stores admin accounts |
| `buses` | Bus details (number, capacity, type) |
| `routes` | Route info (source, destination, distance) |
| `schedules` | Bus + route + departure time combinations |
| `bookings` | Passenger ticket bookings |
| `emergency_alerts` | Alerts raised by admin or passengers |

---

## Screenshots
> <img width="1662" height="990" alt="image" src="https://github.com/user-attachments/assets/1b2de6ea-6174-4a3a-b161-52c4a8a646ab" />
<img width="1577" height="992" alt="image" src="https://github.com/user-attachments/assets/323eb66b-f1d0-454d-876c-b363f3283402" />

> To add: drag an image into the GitHub editor, or upload to the `resources/` folder and reference it like:
> `![Admin Dashboard](resources/screenshots/admin-dashboard.png)`

---

## Developer

**Sneha Thakur**
- GitHub: [@snehathakur1907-dev](https://github.com/snehathakur1907-dev)

---

## License

This project is open source and available under the [MIT License](LICENSE).

