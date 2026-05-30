# Global Class Offering Booking System

## Overview

Global Class Offering Booking System is a Spring Boot backend application designed for a live-learning platform where teachers can create courses, offerings, and sessions, while parents/students can browse and book available offerings.

The system supports:

* Course management
* Offering (Batch) management
* Session scheduling
* Parent booking management
* Timezone-aware scheduling
* Conflict detection
* Concurrent booking handling

---

## Tech Stack

* Java 21
* Spring Boot 3.2
* Spring Data JPA
* MySQL
* Maven
* Lombok
* Swagger / OpenAPI

---

## Core Concepts

### Course

Represents a subject being taught.

Examples:

* Python Coding
* Art Drawing
* Public Speaking

---

### Offering (Batch)

A schedulable version of a course.

Examples:

* Saturday Batch
* Evening Batch
* Summer Camp Batch

Each offering belongs to a course and contains multiple sessions.

---

### Session

An individual class meeting.

Example:

Python Coding - Saturday Batch

Sessions:

* June 7, 6 PM – 7 PM
* June 14, 6 PM – 7 PM
* June 21, 6 PM – 7 PM

---

## Features

### Teacher APIs

Teachers can:

* Create courses
* Create offerings
* Add sessions to offerings
* View their courses
* View their offerings
* View upcoming sessions

### Parent APIs

Parents can:

* View available offerings
* View offering details
* Book offerings
* View bookings
* Cancel bookings

---

## Timezone Handling

Timezone support is one of the core requirements.

### Storage

All session times are stored in UTC using:

```java
Instant startTime;
Instant endTime;
```

### Teacher Flow

Teacher submits:

```text
2026-06-07T18:00:00+05:30
```

Stored internally as:

```text
2026-06-07T12:30:00Z
```

### Parent Flow

Parent requests schedules using their timezone:

```text
Asia/Kolkata
Europe/London
America/New_York
```

Session times are automatically converted before being returned.

---

## Booking Rules

### Rule 1 - Offering Level Booking

Parents book the entire offering.

Example:

Booking:

Python Coding - Saturday Batch

Automatically books all sessions belonging to that offering.

---

### Rule 2 - Conflict Detection

A parent cannot book another offering if any session overlaps with an already booked session.

Overlap logic:

```java
start1.isBefore(end2)
&& end1.isAfter(start2)
```

Example:

Offering A

5:00 PM - 6:00 PM

Offering B

5:30 PM - 6:30 PM

Result:

Booking rejected due to overlap.

---

### Rule 3 - Concurrent Booking Handling

The system handles simultaneous booking requests safely using:

* Transactions
* Database locking
* Conflict validation

This prevents:

* Duplicate bookings
* Race conditions
* Invalid overlapping bookings

---

## Database Design

Relationship Diagram

Course
|
Offering
|
Session

User (Parent/Teacher)
|
Booking
|
Offering

---

## Main Entities

### User

Represents teachers and parents.

### Course

Created by teachers.

### Offering

Batch/section of a course.

### Session

Individual scheduled class.

### Booking

Parent enrollment into an offering.

---

## API Endpoints

### Teacher APIs

Create Course

POST

```http
/api/v1/teacher/courses
```

Create Offering

POST

```http
/api/v1/teacher/offerings
```

Add Session

POST

```http
/api/v1/teacher/offerings/{offeringId}/sessions
```

Get Teacher Courses

GET

```http
/api/v1/teacher/courses
```

Get Teacher Offerings

GET

```http
/api/v1/teacher/offerings
```

Get Upcoming Sessions

GET

```http
/api/v1/teacher/sessions/upcoming
```

---

### Parent APIs

Get Available Offerings

GET

```http
/api/v1/parent/offerings
```

Get Offering Details

GET

```http
/api/v1/parent/offerings/{offeringId}
```

Book Offering

POST

```http
/api/v1/parent/bookings/{offeringId}
```

Get Parent Bookings

GET

```http
/api/v1/parent/bookings
```

Cancel Booking

DELETE

```http
/api/v1/parent/bookings/{bookingId}
```

---

## Running the Application

### Step 1 - Create Database

```sql
CREATE DATABASE booking_system;
```

---

### Step 2 - Configure application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_system
spring.datasource.username=root
spring.datasource.password=
```

---

### Step 3 - Build Project

```bash
mvn clean install
```

---

### Step 4 - Run Application

```bash
mvn spring-boot:run
```

Application starts at:

```text
http://localhost:8080
```

---

## Swagger Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Use Swagger to test all APIs directly from the browser.

---

## Validation & Error Handling

The application includes:

* Request validation
* Global exception handling
* Resource not found handling
* Conflict detection handling
* Consistent API error responses

---

## Assumptions

* Teachers and parents already exist in the database.
* Authentication is intentionally omitted because it was not part of the assignment requirements.
* All scheduling data is stored in UTC.
* Booking occurs at the offering level, not the session level.

---

## Author

Ashutosh Tripathi

Backend Engineering Assignment

Spring Boot + MySQL
