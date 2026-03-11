# 📁 Digital Asset Management System (DAM)

> **Project No. 07** — Web Application Project 2024/2025  
> Stack: **Spring Boot 3** · **Hibernate 6** · **MySQL 8** · **Thymeleaf** · **Chart.js**

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Technology Stack](#technology-stack)
4. [Architecture](#architecture)
5. [Class Diagram](#class-diagram)
6. [User Roles & Permissions](#user-roles--permissions)
7. [Project Structure](#project-structure)
8. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Database Setup](#database-setup)
   - [Configuration](#configuration)
   - [Running the Application](#running-the-application)
9. [REST API](#rest-api)
10. [UI Modules](#ui-modules)
11. [Analytics Dashboard](#analytics-dashboard)
12. [Security](#security)
13. [Authors](#authors)

---

## Overview

The **Digital Asset Management System (DAM)** is a full-stack web application that serves as a centralised platform for storing, cataloguing, searching, sharing and archiving digital assets — images, videos, PDFs, documents and more.

It solves common organisational problems such as scattered file repositories, missing metadata, lack of access control and no audit trail. The system supports **six user roles** with granular RBAC permissions, a **rich metadata model**, **full-text search**, **version history** per asset and an **interactive analytics dashboard**.

---

## Features

### Asset Management
- Upload single or multiple files with drag-and-drop support
- Automatic thumbnail generation for images and PDF (first page)
- Full version history per asset with author and timestamp
- Metadata editor: title, description, tags, category, licence, expiry date
- Asset statuses: **Draft → Published → Archived**
- Download tracking and related-asset suggestions

### Search & Discovery
- Full-text search across title, description, tags and EXIF metadata
- Advanced filter panel: file type, category, tags, date range, owner, status
- Sortable results: date, name, size, download count
- Grid and list view modes with pagination or infinite scroll

### User & Access Management
- Six roles with hierarchical, additive permissions (RBAC via Spring Security)
- Account lockout after 5 failed login attempts
- Per-user storage quota management
- User activity overview for administrators

### Analytics Dashboard
- Upload trends over time (line chart)
- Format distribution (doughnut chart)
- Top categories and tags (bar charts)
- Activity heatmap (day × hour)
- KPI cards: storage used, active users, monthly downloads, metadata completeness

### API & Integration
- RESTful API with JSON responses
- JWT-based authentication for external integrators
- OpenAPI 3 documentation via Swagger UI at `/swagger-ui.html`
- Server-side response caching (Spring Cache + Caffeine, TTL = 5 min)

---

## Technology Stack

| Layer | Technology |
|---|---|
| **Database** | MySQL 8.x |
| **ORM** | Hibernate 6.x (via Spring Data JPA) |
| **Application Server** | Spring Boot 3.x |
| **Web Layer** | Spring MVC |
| **Templating** | Thymeleaf 3.x |
| **Security** | Spring Security 6 — RBAC, BCrypt, JWT, CSRF |
| **Caching** | Spring Cache + Caffeine |
| **Frontend** | HTML5, CSS3, Bootstrap 5, vanilla JavaScript (Fetch API) |
| **Charts** | Chart.js 4.x |
| **API Docs** | SpringDoc OpenAPI 3 (Swagger UI) |
| **Build Tool** | Maven 3.x |
| **Java Version** | Java 21 |

---

## Architecture

The application follows a classic **layered MVC architecture**:

```
┌──────────────────────────────────────────────┐
│              Browser / API Client             │
└────────────────────┬─────────────────────────┘
                     │ HTTP / HTTPS
┌────────────────────▼─────────────────────────┐
│           Presentation Layer                  │
│   Spring MVC Controllers + Thymeleaf Views    │
│   REST Controllers (@RestController)          │
└────────────────────┬─────────────────────────┘
                     │
┌────────────────────▼─────────────────────────┐
│             Service Layer                     │
│   Business logic, validation, caching         │
│   Spring @Service beans                       │
└────────────────────┬─────────────────────────┘
                     │
┌────────────────────▼─────────────────────────┐
│           Repository Layer                    │
│   Spring Data JPA Repositories                │
│   Hibernate ORM                               │
└────────────────────┬─────────────────────────┘
                     │
┌────────────────────▼─────────────────────────┐
│              MySQL 8 Database                 │
└──────────────────────────────────────────────┘
```

**Key architectural decisions:**
- **Server-side rendering** with Thymeleaf for standard pages; asynchronous Fetch API calls for analytics data and dynamic interactions
- **Multipart file upload** handled by Spring's `MultipartFile`, stored on the local filesystem (configurable path)
- **Thumbnail generation** performed synchronously on upload using Java's `ImageIO` / Apache PDFBox for PDF previews
- **JWT tokens** issued for API Integrator role; session-based auth for browser users
- **Audit log** written to the database after every significant operation via a Spring AOP aspect

---

## Class Diagram

```
User ────────────────────── owns ──────────────────── Asset
 │                                                      │
 ├── has ──── ApiToken                                  ├── versions ──── AssetVersion
 │                                                      │
 └── has ──── StorageQuota                              ├── described by ── Metadata
                                                        │
                                                        ├── belongs to ──── Category ◄─┐
                                                        │                               │ (tree)
                                                        └── tagged with ─── Tag         └────────┘

AuditLog ── performed by ──► User
AuditLog ── targets ────────► Asset

Enumerations: Role · AssetStatus · AssetType · LicenceType · AuditAction
```

Full Mermaid class diagram available in [`DAM_ClassDiagram.mermaid`](DAM_ClassDiagram.mermaid).

---

## User Roles & Permissions

The system implements **Role-Based Access Control (RBAC)** with six roles. Permissions are additive — each higher business role inherits those below it.

```
Administrator  ⊃  Content Manager  ⊃  Editor  ⊃  Viewer
```

Technical roles (System Administrator, API Integrator) are independent of the business hierarchy.

| Functionality | Admin | Content Mgr | Editor | Viewer | API Int. |
|---|:---:|:---:|:---:|:---:|:---:|
| Browse catalogue | ✓ | ✓ | ✓ | ✓ | ✓ |
| Advanced search | ✓ | ✓ | ✓ | ✓ | ✓ |
| Download assets | ✓ | ✓ | ✓ | ✓ | ✓ |
| Upload assets | ✓ | ✓ | ✓ | — | ✓ |
| Edit own assets | ✓ | ✓ | ✓ | — | — |
| Edit others' assets | ✓ | ✓ | — | — | — |
| Delete assets | ✓ | ✓ | — | — | — |
| Manage versions | ✓ | ✓ | ✓ | — | — |
| Manage categories | ✓ | ✓ | — | — | — |
| Manage tags | ✓ | ✓ | — | — | — |
| Manage users | ✓ | — | — | — | — |
| Analytics dashboard | ✓ | ✓ | — | — | — |
| Data export (CSV/JSON) | ✓ | ✓ | — | — | ✓ |
| REST API access | ✓ | — | — | — | ✓ |
| System configuration | ✓ | — | — | — | — |
| View system logs | ✓ | — | — | — | — |

---

## Project Structure

```
dam-system/
├── src/
│   ├── main/
│   │   ├── java/com/dam/
│   │   │   ├── DamApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java        # Spring Security + JWT filter chain
│   │   │   │   ├── CacheConfig.java           # Caffeine cache configuration
│   │   │   │   └── WebMvcConfig.java          # MVC, static resources, file upload limits
│   │   │   ├── controller/
│   │   │   │   ├── AssetController.java       # Web views for asset CRUD
│   │   │   │   ├── UserController.java        # User management views
│   │   │   │   ├── DashboardController.java   # Main dashboard view
│   │   │   │   └── api/
│   │   │   │       ├── AssetApiController.java
│   │   │   │       ├── AnalyticsApiController.java
│   │   │   │       └── AuthApiController.java
│   │   │   ├── service/
│   │   │   │   ├── AssetService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── CategoryService.java
│   │   │   │   ├── TagService.java
│   │   │   │   ├── ThumbnailService.java      # ImageIO + PDFBox thumbnail generation
│   │   │   │   ├── AnalyticsService.java
│   │   │   │   ├── StorageService.java        # Filesystem read/write
│   │   │   │   └── JwtService.java
│   │   │   ├── repository/
│   │   │   │   ├── AssetRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── TagRepository.java
│   │   │   │   └── AuditLogRepository.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Asset.java
│   │   │   │   ├── AssetVersion.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── Tag.java
│   │   │   │   ├── Metadata.java
│   │   │   │   ├── AuditLog.java
│   │   │   │   ├── ApiToken.java
│   │   │   │   ├── StorageQuota.java
│   │   │   │   └── enums/
│   │   │   │       ├── Role.java
│   │   │   │       ├── AssetStatus.java
│   │   │   │       ├── AssetType.java
│   │   │   │       ├── LicenceType.java
│   │   │   │       └── AuditAction.java
│   │   │   ├── dto/
│   │   │   │   ├── AssetUploadDto.java
│   │   │   │   ├── AssetResponseDto.java
│   │   │   │   ├── UserDto.java
│   │   │   │   └── AnalyticsDto.java
│   │   │   └── aspect/
│   │   │       └── AuditAspect.java           # AOP-based audit logging
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── main.css
│   │       │   └── js/
│   │       │       ├── upload.js              # Drag-and-drop + progress bars
│   │       │       ├── asset-browser.js       # Grid/list toggle, filter panel
│   │       │       └── analytics.js           # Chart.js chart initialisation
│   │       └── templates/
│   │           ├── layout/
│   │           │   ├── base.html              # Base Thymeleaf layout
│   │           │   ├── navbar.html
│   │           │   └── sidebar.html
│   │           ├── auth/
│   │           │   ├── login.html
│   │           │   └── register.html
│   │           ├── dashboard.html
│   │           ├── assets/
│   │           │   ├── browser.html
│   │           │   ├── detail.html
│   │           │   └── upload.html
│   │           ├── admin/
│   │           │   ├── users.html
│   │           │   └── categories.html
│   │           └── profile.html
│   └── test/
│       └── java/com/dam/
│           ├── service/
│           │   ├── AssetServiceTest.java
│           │   └── UserServiceTest.java
│           └── controller/
│               └── AssetApiControllerTest.java
├── uploads/                                   # Runtime file storage (gitignored)
├── pom.xml
└── README.md
```

---

## Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.9+**
- **MySQL 8.0+**
- *(Optional)* Apache PDFBox on the classpath for PDF thumbnail support (included via `pom.xml`)

### Database Setup

```sql
CREATE DATABASE dam_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'dam_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON dam_db.* TO 'dam_user'@'localhost';
FLUSH PRIVILEGES;
```

Hibernate will auto-create the schema on first run (`ddl-auto=update`).  
To seed an initial Administrator account, run the provided SQL script:

```bash
mysql -u dam_user -p dam_db < src/main/resources/db/seed.sql
```

### Configuration

Copy `application.properties` and adjust the values for your environment:

```properties
# ── Database ──────────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/dam_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=dam_user
spring.datasource.password=your_password

# ── Hibernate ─────────────────────────────────────────────
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# ── File Storage ──────────────────────────────────────────
dam.storage.upload-dir=./uploads
dam.storage.max-file-size=52428800        # 50 MB in bytes
dam.storage.allowed-types=image/jpeg,image/png,image/gif,image/webp,application/pdf,video/mp4

# ── JWT ───────────────────────────────────────────────────
dam.jwt.secret=your_jwt_secret_key_min_32_chars
dam.jwt.expiration-ms=86400000            # 24 hours

# ── Cache ─────────────────────────────────────────────────
dam.cache.analytics-ttl-seconds=300      # 5 minutes

# ── Spring multipart ──────────────────────────────────────
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=200MB
```

### Running the Application

```bash
# Clone the repository
git clone https://github.com/your-org/dam-system.git
cd dam-system

# Build and run
mvn clean package
mvn spring-boot:run

# Or run the JAR directly
java -jar target/dam-system-1.0.0.jar
```

The application starts on **http://localhost:8080** by default.  
Swagger UI is available at **http://localhost:8080/swagger-ui.html**.

**Default admin credentials (from seed.sql):**

| Field | Value |
|---|---|
| Email | `admin@dam.local` |
| Password | `Admin123!` *(change on first login)* |

---

## REST API

All API endpoints are under `/api/`. Authentication uses `Authorization: Bearer <JWT>` header.  
Full interactive documentation: **http://localhost:8080/swagger-ui.html**

### Authentication

```http
POST /api/auth/token
Content-Type: application/json

{
  "email": "integrator@dam.local",
  "password": "your_password"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
  "expiresAt": "2025-03-12T10:00:00Z"
}
```

### Assets

| Method | Endpoint | Description | Roles |
|---|---|---|---|
| `GET` | `/api/assets` | List assets (paginated, filterable) | All |
| `GET` | `/api/assets/{id}` | Get asset details | All |
| `POST` | `/api/assets` | Upload new asset (`multipart/form-data`) | Editor+ |
| `PUT` | `/api/assets/{id}` | Update asset metadata | Editor+ |
| `DELETE` | `/api/assets/{id}` | Delete asset | Content Mgr+ |
| `GET` | `/api/assets/{id}/download` | Download original file | All |
| `GET` | `/api/assets/{id}/thumbnail` | Get thumbnail | All |
| `POST` | `/api/assets/{id}/versions` | Upload new version | Editor+ |

### Categories & Tags

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/categories` | Full category tree |
| `POST` | `/api/categories` | Create category |
| `PUT` | `/api/categories/{id}` | Update category |
| `DELETE` | `/api/categories/{id}` | Delete category |
| `GET` | `/api/tags` | List all tags |
| `POST` | `/api/tags/merge` | Merge duplicate tags |

### Analytics

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/analytics/uploads` | Upload counts over time |
| `GET` | `/api/analytics/formats` | Asset distribution by format |
| `GET` | `/api/analytics/categories` | Top categories |
| `GET` | `/api/analytics/users` | Upload ranking by user |
| `GET` | `/api/analytics/activity` | Heatmap data |
| `GET` | `/api/analytics/kpi` | KPI summary card data |

---

## UI Modules

| Module | URL | Available To |
|---|---|---|
| Login / Register | `/login`, `/register` | Public |
| Dashboard | `/dashboard` | All authenticated |
| Asset Browser | `/assets` | All authenticated |
| Asset Detail | `/assets/{id}` | All authenticated |
| Upload | `/assets/upload` | Editor+ |
| Categories | `/admin/categories` | Content Mgr+ |
| User Management | `/admin/users` | Administrator |
| User Profile | `/profile` | All authenticated |
| Swagger UI | `/swagger-ui.html` | All authenticated |

---

## Analytics Dashboard

The dashboard module fetches all chart data asynchronously via the REST API and renders it client-side using **Chart.js 4**. Results are cached server-side for **5 minutes** to reduce database load.

| Chart | Type | Data Source |
|---|---|---|
| Uploads over time | Line | `/api/analytics/uploads?range=30d` |
| Format distribution | Doughnut | `/api/analytics/formats` |
| Top 10 categories | Bar | `/api/analytics/categories?limit=10` |
| User upload ranking | Horizontal bar | `/api/analytics/users?limit=10` |
| Activity heatmap | Matrix (custom) | `/api/analytics/activity` |
| KPI cards | Numeric | `/api/analytics/kpi` |

---

## Security

- **Authentication:** Session-based for browser users; JWT Bearer token for API Integrator role
- **Password hashing:** BCrypt with strength factor 12
- **CSRF protection:** Enabled for all state-changing web requests; disabled for `/api/**` (stateless JWT)
- **Account lockout:** Automatic lock after 5 failed login attempts; manual unlock by Administrator
- **Method-level security:** `@PreAuthorize` annotations on service methods enforce role checks beyond URL-level filters
- **Audit logging:** AOP aspect (`@Around`) intercepts upload, edit, delete and download operations and writes to `audit_log` table
- **HTTPS:** Recommended in production via reverse proxy (nginx / Apache); application itself serves HTTP

---

## Authors

| Name | Role |
|---|---|
| Filip Jankowski | Developer |
| Krzysztof Niemir | Developer |

---

*Web Application Project · 2026*
