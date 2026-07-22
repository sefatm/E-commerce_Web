# Rural Mart Web

Rural Mart ecommerce web application with the shared Spring Boot backend and MySQL database.

## Repository contents

- `frontend/` - Angular web storefront, customer pages, seller pages and admin panel
- `backend/` - shared Spring Boot REST API
- `database/rural_mart.sql` - shared MySQL database export

## Backend setup

1. Create a MySQL database named `rural_mart`.
2. Import `database/rural_mart.sql`.
3. Update `backend/src/main/resources/application.properties` with your MySQL username and password.
4. Start the API:

```bash
cd backend
mvnw spring-boot:run
```

The backend runs on `http://localhost:8080`.

## Angular setup

```bash
cd frontend
npm install
npm start
```

Open `http://localhost:4200`.

## Notes

The Angular app, Android app and Flutter app are intended to use this same backend and database.
