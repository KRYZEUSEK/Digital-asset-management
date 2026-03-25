# 📦 Digital Asset Management System

> **Web Application Project — No. 07** | Academic Year 2025/2026  
> Spring Boot · Hibernate · MySQL · Thymeleaf · Chart.js

---

## Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Database Setup](#database-setup)
   - [Running the Application](#running-the-application)
5. [Configuration](#configuration)
6. [Domain Model](#domain-model)
7. [User Roles & Permissions](#user-roles--permissions)
8. [REST API](#rest-api)
9. [Features](#features)
10. [Authors](#authors)

---

## Overview

The **Digital Asset Management (DAM) System** is a web application for centralised storage, cataloguing, searching, and sharing of digital assets — images, videos, documents, audio files, and more.

Key capabilities:

- Upload and store digital assets with automatic metadata extraction
- Organise assets using a hierarchical category tree and tags
- Search and filter the asset catalogue by type, status, category, tag, or keyword
- Manage asset versions — full history of every file change
- Role-based access control with six distinct roles
- Analytics dashboard with live charts (Chart.js)
- REST API with OpenAPI / Swagger UI documentation

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2 (MVC, Security, Data JPA, Cache) |
| ORM | Hibernate 6 |
| Database | MySQL 8 |
| Templates | Thymeleaf 3 |
| Frontend | HTML5, CSS3, Bootstrap 5, vanilla JavaScript |
| Charts | Chart.js 4 |
| Security | Spring Security (BCrypt, session-based auth, RBAC) |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven 3 |

---

## Project Structure

```
src/
└── main/
    ├── java/com/uep/wap/
    │   ├── WapApplication.java          ← entry point
    │   │
    │   ├── model/                       ← JPA entities & enums
    │   │   ├── User.java
    │   │   ├── Asset.java
    │   │   ├── AssetVersion.java
    │   │   ├── Category.java
    │   │   ├── Tag.java
    │   │   ├── Metadata.java
    │   │   ├── AuditLog.java
    │   │   ├── ApiToken.java
    │   │   ├── StorageQuota.java
    │   │   ├── AnalyticsSnapshot.java
    │   │   ├── SystemConfig.java
    │   │   ├── Role.java                ← enum
    │   │   ├── AssetStatus.java         ← enum
    │   │   ├── AssetType.java           ← enum
    │   │   ├── LicenceType.java         ← enum
    │   │   └── AuditAction.java         ← enum
    │   │
    │   ├── repository/                  ← Spring Data JPA repositories
    │   │   ├── UserRepository.java
    │   │   ├── AssetRepository.java
    │   │   ├── AssetVersionRepository.java
    │   │   ├── CategoryRepository.java
    │   │   ├── TagRepository.java
    │   │   ├── AuditLogRepository.java
    │   │   ├── ApiTokenRepository.java
    │   │   ├── StorageQuotaRepository.java
    │   │   └── AnalyticsSnapshotRepository.java
    │   │
    │   ├── dto/                         ← Data Transfer Objects
    │   │   ├── UserDTO.java
    │   │   ├── CreateUserDTO.java
    │   │   ├── UpdateUserDTO.java
    │   │   ├── AssetDTO.java
    │   │   ├── CreateAssetDTO.java
    │   │   ├── UpdateAssetDTO.java
    │   │   ├── AssetVersionDTO.java
    │   │   ├── CategoryDTO.java
    │   │   ├── TagDTO.java
    │   │   └── AnalyticsDashboardDTO.java
    │   │
    │   ├── service/                     ← Business logic
    │   │   ├── UserService.java
    │   │   ├── AssetService.java
    │   │   ├── CategoryService.java
    │   │   ├── TagService.java
    │   │   └── AnalyticsService.java
    │   │
    │   ├── controller/                  ← REST controllers
    │   │   ├── UserController.java
    │   │   ├── AssetController.java
    │   │   ├── CategoryController.java
    │   │   ├── TagController.java
    │   │   └── AnalyticsController.java
    │   │
    │   ├── config/                      ← Spring Security configuration
    │   │   ├── SecurityConfig.java
    │   │   ├── DamUserDetails.java
    │   │   └── DamUserDetailsService.java
    │   │
    │   └── exception/                   ← Global error handling
    │       └── GlobalExceptionHandler.java
    │
    └── resources/
        ├── application.properties
        └── templates/                   ← Thymeleaf HTML templates (to be added)
```

---

## Getting Started

### Prerequisites

- **Java 21** — [Download](https://adoptium.net/)
- **Maven 3.9+** — bundled via `mvnw` wrapper
- **MySQL 8** — running locally or via Docker

> Verify your setup:
> ```bash
> java -version    # openjdk 21 or higher
> mvn -version     # Apache Maven 3.9+
> mysql --version  # Ver 8.x
> ```

---

### Database Setup

1. Start MySQL and log in:
   ```bash
   mysql -u root -p
   ```

2. Create the database:
   ```sql
   CREATE DATABASE dam_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. Update credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/dam_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

> Hibernate will create all tables automatically on first run (`ddl-auto=update`).

---

### Running the Application

**Using the Maven wrapper (recommended):**

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

**Or build and run the JAR:**

```bash
./mvnw clean package -DskipTests
java -jar target/wap-0.0.1-SNAPSHOT.jar
```

Once started, open your browser at:

| URL | Description |
|---|---|
| `http://localhost:8080` | Application home / login page |
| `http://localhost:8080/dashboard` | Main dashboard (authenticated) |
| `http://localhost:8080/swagger-ui.html` | Swagger UI — interactive API docs |
| `http://localhost:8080/v3/api-docs` | Raw OpenAPI JSON |

---

## Configuration

All configuration lives in `src/main/resources/application.properties`.

```properties
# Server
server.port=8080

# DataSource
spring.datasource.url=jdbc:mysql://localhost:3306/dam_db?...
spring.datasource.username=root
spring.datasource.password=password

# Hibernate
spring.jpa.hibernate.ddl-auto=update      # use 'validate' in production
spring.jpa.show-sql=true

# File upload limits
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=55MB

# Thymeleaf
spring.thymeleaf.cache=false              # set to true in production
```

---

## Domain Model

The core entities and their relationships:

```
User ──────────── owns ──────────────── Asset
 │                                        │
 ├── StorageQuota (1:1)                   ├── AssetVersion (1:N, ordered)
 └── ApiToken (1:N)                       ├── Metadata (1:1, EXIF + custom)
                                          ├── Category (N:1, tree structure)
                                          └── Tag (N:M)

AuditLog ──── performed by ──── User
         ──── targets       ──── Asset
```

### Enumerations

| Enum | Values |
|---|---|
| `Role` | `ADMINISTRATOR`, `CONTENT_MANAGER`, `EDITOR`, `VIEWER`, `SYSTEM_ADMINISTRATOR`, `API_INTEGRATOR` |
| `AssetStatus` | `DRAFT`, `PUBLISHED`, `ARCHIVED` |
| `AssetType` | `IMAGE`, `VIDEO`, `DOCUMENT`, `AUDIO`, `OTHER` |
| `LicenceType` | `ALL_RIGHTS_RESERVED`, `CC_BY`, `CC_BY_SA`, `CC_BY_NC`, `PUBLIC_DOMAIN`, `CUSTOM` |
| `AuditAction` | `UPLOAD`, `DOWNLOAD`, `EDIT_METADATA`, `STATUS_CHANGE`, `DELETE`, `VERSION_UPLOAD` |

---

## User Roles & Permissions

```
Administrator  ⊃  Content Manager  ⊃  Editor  ⊃  Viewer
```

| Functionality | Admin | Content Mgr | Editor | Viewer | API Int. |
|---|:---:|:---:|:---:|:---:|:---:|
| Browse & search assets | ✓ | ✓ | ✓ | ✓ | ✓ |
| Download assets | ✓ | ✓ | ✓ | ✓ | ✓ |
| Upload new assets | ✓ | ✓ | ✓ | — | ✓ |
| Edit own assets | ✓ | ✓ | ✓ | — | — |
| Edit other users' assets | ✓ | ✓ | — | — | — |
| Delete assets | ✓ | ✓ | — | — | — |
| Manage asset versions | ✓ | ✓ | ✓ | — | — |
| Manage categories & tags | ✓ | ✓ | — | — | — |
| Manage users | ✓ | — | — | — | — |
| Analytics dashboard | ✓ | ✓ | — | — | — |
| Export data (CSV/JSON) | ✓ | ✓ | — | — | ✓ |
| REST API access | ✓ | — | — | — | ✓ |
| System configuration | ✓ | — | — | — | — |
| View system logs | ✓ | — | — | — | — |

---

## REST API

All endpoints are under `/api/`. Full interactive documentation is available at **`/swagger-ui.html`** after starting the application.

### Assets — `/api/assets`

| Method | Path | Description | Min. Role |
|---|---|---|---|
| `GET` | `/api/assets` | Paginated asset list | Any |
| `GET` | `/api/assets/{id}` | Asset detail | Any |
| `GET` | `/api/assets/search?q=` | Full-text search | Any |
| `GET` | `/api/assets?status=PUBLISHED` | Filter by status | Any |
| `POST` | `/api/assets` | Upload new asset (multipart) | EDITOR |
| `PUT` | `/api/assets/{id}` | Update metadata | EDITOR |
| `POST` | `/api/assets/{id}/versions` | Upload new file version | EDITOR |
| `GET` | `/api/assets/{id}/download` | Download original file | Any |
| `DELETE` | `/api/assets/{id}` | Delete asset | CONTENT_MANAGER |

### Users — `/api/users`

| Method | Path | Description | Min. Role |
|---|---|---|---|
| `GET` | `/api/users` | List all users | ADMINISTRATOR |
| `GET` | `/api/users/{id}` | User detail | ADMINISTRATOR |
| `POST` | `/api/users` | Create user | ADMINISTRATOR |
| `PUT` | `/api/users/{id}` | Update profile | ADMINISTRATOR / self |
| `PATCH` | `/api/users/{id}/role` | Change role | ADMINISTRATOR |
| `PATCH` | `/api/users/{id}/lock` | Lock account | ADMINISTRATOR |
| `PATCH` | `/api/users/{id}/unlock` | Unlock account | ADMINISTRATOR |
| `DELETE` | `/api/users/{id}` | Delete user | ADMINISTRATOR |

### Categories — `/api/categories`

| Method | Path | Description | Min. Role |
|---|---|---|---|
| `GET` | `/api/categories` | All categories | Any |
| `GET` | `/api/categories/roots` | Root categories only | Any |
| `GET` | `/api/categories/{id}` | Category detail | Any |
| `POST` | `/api/categories` | Create category | CONTENT_MANAGER |
| `PUT` | `/api/categories/{id}` | Update category | CONTENT_MANAGER |
| `DELETE` | `/api/categories/{id}` | Delete category | CONTENT_MANAGER |

### Tags — `/api/tags`

| Method | Path | Description | Min. Role |
|---|---|---|---|
| `GET` | `/api/tags` | All tags (by usage) | Any |
| `POST` | `/api/tags` | Create tag | CONTENT_MANAGER |
| `POST` | `/api/tags/merge?sourceId=&targetId=` | Merge two tags | CONTENT_MANAGER |
| `DELETE` | `/api/tags/unused` | Delete unused tags | CONTENT_MANAGER |
| `DELETE` | `/api/tags/{id}` | Delete tag | ADMINISTRATOR |

### Analytics — `/api/analytics`

| Method | Path | Description | Min. Role |
|---|---|---|---|
| `GET` | `/api/analytics/dashboard` | Full dashboard payload | CONTENT_MANAGER |

---

## Features

### Asset Management
- Upload single or multiple files simultaneously with drag-and-drop
- Automatic MIME type detection and asset type classification
- Thumbnail generation for images and PDF first pages
- Full version history — every file replacement is stored and browsable

### Metadata & Organisation
- Rich metadata per asset: title, description, tags, category, licence, expiry date
- Hierarchical category tree (parent-child) with colour coding
- Many-to-many tagging with autocomplete; merge duplicate tags

### Search & Browse
- Full-text search across title, description and tags
- Filter panel: type, status, category, tags, owner, date range
- Grid and list views with sortable columns
- Pagination (20 per page, configurable)

### Analytics Dashboard
- KPI cards: total assets, storage used, active users (30 days), monthly downloads
- Line chart: uploads over time (configurable range)
- Doughnut chart: asset type distribution
- Bar chart: top 10 categories and tags
- Horizontal bar: top uploaders ranking
- All chart data served from `/api/analytics/dashboard` with 5-minute cache (Caffeine)

### Security
- Form-based authentication with BCrypt password hashing
- Account lockout after 5 consecutive failed login attempts
- Full audit trail: every upload, download, edit and delete is logged with user, timestamp and IP
- Per-user storage quota enforcement

---

## Authors

| Name | Role |
|---|---|
| Filip Jankowski | Developer |
| Krzysztof Niemir | Developer |


---

*Web Application Project — Poznań University of Economics and Business — 2026*
