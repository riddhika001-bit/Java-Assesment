# Hospital Management System

A Java hospital management project with a console application and a browser-based Spring Boot web app. Both use MySQL patient and doctor records.

## Web app features

- Register, search, update, sort, and discharge patients
- Queue patients for treatment by severity, with critical cases first
- Assign the next patient to a doctor and track doctor workloads
- Undo the last patient discharge
- Responsive dashboard for desktop and mobile
- Docker image for deployment

## Run the website locally

Requirements: JDK 21, Maven 3.9+, and MySQL 8 running locally.

1. Create the database once in MySQL:

   ```sql
   CREATE DATABASE hospital_management;
   ```

2. Set the MySQL login in PowerShell:

   ```powershell
   $env:HOSPITAL_DB_USER = "your-mysql-username"
   $env:HOSPITAL_DB_PASSWORD = "your-mysql-password"
   $env:HOSPITAL_WEB_USER = "admin"
   $env:HOSPITAL_WEB_PASSWORD = "a-strong-password-at-least-12-characters"
   ```

3. Start the web app:

   ```powershell
   cd webapp
   mvn spring-boot:run
   ```

4. Open [http://localhost:8080](http://localhost:8080).

The web app creates the patient, doctor, treatment queue, and discharge history tables and adds the default doctors at startup. Sign-in is required; configure `HOSPITAL_WEB_USER` and a strong `HOSPITAL_WEB_PASSWORD` of at least 12 characters. The web login is kept in application memory. You can configure a hosted MySQL database with `SPRING_DATASOURCE_URL`, `HOSPITAL_DB_USER`, and `HOSPITAL_DB_PASSWORD`. The server port can be set with `PORT`.

## Deploy with Docker

The Dockerfile is in `webapp/`. Build from the repository root:

```powershell
docker build -t hospital-management-web ./webapp
```

Run the image with a reachable MySQL database and configure:

- `SPRING_DATASOURCE_URL`: JDBC URL for the database, for example `jdbc:mysql://db-host:3306/hospital_management?useSSL=true&serverTimezone=UTC`
- `HOSPITAL_DB_USER`: database username
- `HOSPITAL_DB_PASSWORD`: database password
- `HOSPITAL_WEB_USER`: sign-in username
- `HOSPITAL_WEB_PASSWORD`: sign-in password (at least 12 characters)
- `PORT`: optional HTTP port (defaults to `8080`)

Set these as secret environment variables in your hosting provider, use HTTPS, and never put credentials in source code or commit them to GitHub. The database must exist before the web app starts. This project is an educational example; do not use it to store real patient or other regulated health data without a professional security and privacy review.

## Console app

The original Java console app remains in `src/`. Open the repository in VS Code and run `Main.java`; configure `HOSPITAL_DB_USER` and `HOSPITAL_DB_PASSWORD` first. Add MySQL Connector/J to `lib/` if you want to run the console app.

## Repository layout

```text
src/       Original console app sources
webapp/    Spring Boot website, MySQL schema, and Dockerfile
lib/       Optional local Connector/J dependency for the console app
```
