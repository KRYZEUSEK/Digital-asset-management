# HOW TO RUN

Checklist
- [ ] Install required tools (Java, Maven) and verify versions
- [ ] Build backend and start application
- [ ] Open the application in a browser and log in with default credentials

This document explains how to run the application included in this repository (backend + static frontend files). It is written for a Windows development machine (PowerShell examples). Adjust commands for other OSes.

---

Required tools and versions
- Java 17

- Maven (recommended: use included Maven Wrapper). If you use system Maven, Maven 3.6+ or 3.8+ is fine. Verify with:


Database setup
1. Engine: MySQL

3. Update `src/main/resources/application.properties` (or pass values as runtime arguments / environment variables) so the application can connect to MySQL. The relevant properties are:

```properties
spring.datasource.url=jdbc:mysql://<DB_HOST>:3306/<DB_NAME>
spring.datasource.username=<DB_USER>
spring.datasource.password=<DB_PASSWORD>
server.port=8765
```

Replace `<DB_HOST>`, `<DB_NAME>`, `<DB_USER>`, `<DB_PASSWORD>` with the values: jdbc:mysql://150.254.36.243:3306/ait91868, ait91868, ait91868, KN91868

Build and start backend (exact commands)
1. From project root `wap-initial-project` use the Maven wrapper (Windows PowerShell):

.\mvnw.cmd clean package
java -jar .\target\wap-initial-project-0.0.1-SNAPSHOT.jar

Notes:
- Default port configured in `src/main/resources/application.properties` is `8765`. Change `server.port` if needed.

Build and start frontend (exact commands)
- This repository contains a small static frontend inside `src/main/resources/static/frontend` (files: `index.html`, `css/styles.css`, `js/app.js`). 
There is no separate frontend build step — the static files are served by Spring Boot from the backend.


URLs after startup
- Backend base URL: http://localhost:8765/ (default)
- API endpoints are served under the same host and port (check `src/main/java/com/uep/wap/controller` for exact routes).

Default login credentials (if authentication required)
- An initial admin user is created automatically by `DataInitializer` when the database is empty:
  - email: `admin@dam.local`
  - password: `admin123`

Troubleshooting
- Error: `Access denied for user '...'` — means DB credentials or privileges are incorrect. Ensure the MySQL user exists and has privileges to connect from your host. In MySQL the host part matters (`'app_user'@'%'` allows connections from any host).
- Error: `Port 8765 already in use` — change `server.port` in `application.properties` or stop the conflicting service.
- If you want to run without MySQL for fast development, you can add an in-memory H2 profile (not included by default). Ask for instructions to add a `dev` profile using H2 and I will add it.


