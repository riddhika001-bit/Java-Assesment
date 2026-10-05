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

Requirements: JDK 21 and Maven 3.9+. The local launcher uses an embedded file-based database, so no MySQL installation or database password is required.

In PowerShell, from the project folder, run:

```powershell
.\webapp\run-local.ps1
```

Open [http://localhost:8080](http://localhost:8080). The launcher creates a random login the first time, prints it in PowerShell, and saves it in an ignored local-only file so the login stays the same when restarted. Patient and doctor records are stored under `webapp/data/` and persist across restarts. Leave the PowerShell window open while using the site; press `Ctrl+C` there to stop it.

The local launcher binds to this PC only. The web app creates the patient, doctor, treatment queue, and discharge history tables and adds the default doctors at startup. Sign-in is required. You can connect to hosted MySQL instead by setting `SPRING_DATASOURCE_URL`, `HOSPITAL_DB_USER`, and `HOSPITAL_DB_PASSWORD`. The server port can be set with `PORT`.

## Deploy with Docker

The Dockerfile is in `webapp/`. Build from the repository root:

```powershell
docker build -t hospital-management-web ./webapp
```

For online hosting, use a reachable hosted MySQL database and configure:

- `SPRING_DATASOURCE_URL`: JDBC URL for the database, for example `jdbc:mysql://db-host:3306/hospital_management?useSSL=true&serverTimezone=UTC`
- `HOSPITAL_DB_USER`: database username
- `HOSPITAL_DB_PASSWORD`: database password
- `HOSPITAL_WEB_USER`: sign-in username
- `HOSPITAL_WEB_PASSWORD`: sign-in password (at least 12 characters)
- `PORT`: optional HTTP port (defaults to `8080`)

Set these as secret environment variables in your hosting provider, use HTTPS, and never put credentials in source code or commit them to GitHub. The database must exist before the web app starts. This project is an educational example; do not use it to store real patient or other regulated health data without a professional security and privacy review.

## Deploy on Render

The repository-root `render.yaml` defines a Docker web service with a persistent 1 GB disk for the embedded database. In Render, create a Blueprint from this repository and select the `Riddhika_mini_project` branch. Render will create the service and generate a private web-login password. The Blueprint uses Render's paid Starter web-service plan because a persistent disk is required; check Render's current pricing before confirming. After deployment, open the service's **Environment** settings to retrieve the generated `HOSPITAL_WEB_PASSWORD`. Sign in with username `admin`. Keep the disk attached when redeploying so patient records are retained.

## Deploy the shared app on Netlify

Netlify serves the static frontend in `netlify-app`; the Java/Spring Boot server cannot run on Netlify. In Netlify, connect this repository and set the production branch to `Riddhika_mini_project`. The root [`netlify.toml`](./netlify.toml) configures the build automatically. Create a Supabase project, open its SQL Editor, and run [`netlify-app/supabase/schema.sql`](./netlify-app/supabase/schema.sql). In Supabase Authentication settings, disable public sign-ups and create/invite accounts only for authorized hospital staff. Then add these environment variables in Netlify site settings and redeploy:

- `VITE_SUPABASE_URL`: the project's Supabase URL
- `VITE_SUPABASE_ANON_KEY`: the project's publishable/anon key (never use a service-role key in the browser)

Connect the GitHub repository `riddhika001-bit/Java-Assesment` and set the production branch to `Riddhika_mini_project`. The root [`netlify.toml`](./netlify.toml) configures the `netlify-app` build. Use HTTPS and do not store real patient or regulated health records in this educational project without a professional security and privacy review.

## Console app

The original Java console app remains in `src/`. Open the repository in VS Code and run `Main.java`; configure `HOSPITAL_DB_USER` and `HOSPITAL_DB_PASSWORD` first. Add MySQL Connector/J to `lib/` if you want to run the console app.

## Repository layout

```text
src/       Original console app sources
webapp/    Spring Boot website, MySQL schema, and Dockerfile
lib/       Optional local Connector/J dependency for the console app
```
