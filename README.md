# Hospital Management System

A Java console application for managing hospital patients, treatment queues, and doctor assignments. Patient and doctor records are stored in MySQL.

## Features

- Add, view, search, update, sort, and discharge patients
- Prioritize patients in the treatment queue by severity
- Undo the most recent discharge
- Assign doctors by department using round-robin rotation and view doctor workloads
- Create the database and tables automatically when the application starts

## Requirements

- Java Development Kit (JDK)
- MySQL Server running locally on port `3306`
- MySQL Connector/J
- Visual Studio Code with the Extension Pack for Java (optional)

## Setup

1. Clone or download this repository.
2. Place the MySQL Connector/J `.jar` file in the project's `lib` directory. The VS Code project settings load JAR files from this directory.
3. Set the database credentials in your environment. The application reads `HOSPITAL_DB_USER` and `HOSPITAL_DB_PASSWORD`; it does not contain a database password.

   In PowerShell, for the current terminal:

   ```powershell
   $env:HOSPITAL_DB_USER = "your-mysql-username"
   $env:HOSPITAL_DB_PASSWORD = "your-mysql-password"
   ```

4. Open the project folder in VS Code and run `Main.java`.

On startup, the application creates the `hospital_management` database and the required tables if they do not exist. The MySQL account must have permission to create databases and tables.

## Project layout

```text
src/        Java source files
lib/        MySQL Connector/J dependency (add locally)
bin/        Compiled output (generated; not committed)
```
