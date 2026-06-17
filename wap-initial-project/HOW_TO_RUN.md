# HOW TO RUN

Checklist
- [ ] Install required tools (Java, Maven) and verify versions
- [ ] Configure database and update `src/main/resources/application.properties`
- [ ] Build backend and start application
- [ ] Open the application in a browser and log in with default credentials

This document explains how to run the application included in this repository (backend + static frontend files). It is written for a Windows development machine (PowerShell examples). Adjust commands for other OSes.

---

Required tools and versions
- Java 17 (project property `<java.version>17</java.version>`). Verify with:

```powershell
java -version
```

- Maven (recommended: use included Maven Wrapper). If you use system Maven, Maven 3.6+ or 3.8+ is fine. Verify with:

```powershell
mvn -v
# or use the wrapper shipped with the project
.\mvnw.cmd -v
```

---

Database setup
1. Engine: MySQL (compatible). The application reads JDBC URL, username and password from `src/main/resources/application.properties`.
2. Create schema (replace `your_schema` with desired name) and a user for the application. Example MySQL commands (run in MySQL shell or via a client):

```sql
CREATE DATABASE your_schema CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'app_user'@'%' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON your_schema.* TO 'app_user'@'%';
FLUSH PRIVILEGES;
```

3. Update `src/main/resources/application.properties` (or pass values as runtime arguments / environment variables) so the application can connect to MySQL. The relevant properties are:

```properties
spring.datasource.url=jdbc:mysql://<DB_HOST>:3306/<DB_NAME>
spring.datasource.username=<DB_USER>
spring.datasource.password=<DB_PASSWORD>
server.port=8765
```

Replace `<DB_HOST>`, `<DB_NAME>`, `<DB_USER>`, `<DB_PASSWORD>` with the values: jdbc:mysql://150.254.36.243:3306/ait91868, ait91868, ait91868, KN91868

4. Initial data: the project includes a `DataInitializer` bean (`com.uep.wap.config.DataInitializer`) which inserts example data (including an admin user) when the database is empty. You do not need to manually import seed data. If you prefer manual import, export the SQL from a running instance or create SQL inserts for users, categories and tags.

---

Environment variables and `application.properties` values
- You can set the same properties via environment variables (PowerShell):

```powershell
$env:SPRING_DATASOURCE_USERNAME = "app_user"
$env:SPRING_DATASOURCE_PASSWORD = "strong_password"
# then run the app in the same PowerShell session
```

- Or pass them as command-line arguments when running the jar, for example:

```powershell
java -jar .\target\wap-initial-project-0.0.1-SNAPSHOT.jar --spring.datasource.username=app_user --spring.datasource.password=strong_password --spring.datasource.url=jdbc:mysql://dbhost:3306/your_schema
```

Be careful not to commit real credentials to version control. Use environment variables or an external configuration for production.

---

Build and start backend (exact commands)
1. From project root `wap-initial-project` use the Maven wrapper (Windows PowerShell):

```powershell
# run directly (useful for development)
.\mvnw.cmd spring-boot:run

# or build a JAR and run it
.\mvnw.cmd clean package
java -jar .\target\wap-initial-project-0.0.1-SNAPSHOT.jar
```

2. Passing DB credentials at runtime (example):

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:mysql://150.254.36.243:3306/your_schema --spring.datasource.username=app_user --spring.datasource.password=strong_password"

# or when running the generated JAR
java -jar .\target\wap-initial-project-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:mysql://150.254.36.243:3306/your_schema --spring.datasource.username=app_user --spring.datasource.password=strong_password
```

Notes:
- The project already contains `WapApplication` annotated with `@SpringBootApplication` and will start all controllers, repositories and beans when run.
- Default port configured in `src/main/resources/application.properties` is `8765`. Change `server.port` if needed.

---

Build and start frontend (exact commands)
- This repository contains a small static frontend inside `src/main/resources/static/frontend` (files: `index.html`, `css/styles.css`, `js/app.js`). There is no separate frontend build step — the static files are served by Spring Boot from the backend.

- To access the frontend, start the backend as shown above and open the application URL (see below). If you need to serve the `index.html` file directly from filesystem for quick debugging, you can open it in the browser, but then API calls will not work unless backend is running and CORS/URLs match.

---

URLs after startup
- Backend base URL: http://localhost:8765/ (default)
- API endpoints are served under the same host and port (check `src/main/java/com/uep/wap/controller` for exact routes).

---

Default login credentials (if authentication required)
- An initial admin user is created automatically by `DataInitializer` when the database is empty:
  - email: `admin@dam.local`
  - password: `admin123`

Use this account to log in and create additional users. If the `DataInitializer` did not run (for example because the DB already contains users), ask the maintainer for credentials or reset the database to allow seeding.

---

Troubleshooting
- Error: `Access denied for user '...'` — means DB credentials or privileges are incorrect. Ensure the MySQL user exists and has privileges to connect from your host. In MySQL the host part matters (`'app_user'@'%'` allows connections from any host).
- Error: `Port 8765 already in use` — change `server.port` in `application.properties` or stop the conflicting service.
- If you want to run without MySQL for fast development, you can add an in-memory H2 profile (not included by default). Ask for instructions to add a `dev` profile using H2 and I will add it.

---

Contact / notes
- Do not commit actual credentials to git. Use a secure secret mechanism for production.
- If you want, I can add a `application-dev.properties` and a runtime `dev` profile using H2 for quick local runs — tell me and I'll add the files and the dependency.

