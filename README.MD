# Store Management API

This project is a simple backend service that acts as a **store management tool**.  
It exposes a REST API for managing products, with basic authentication and role-based access control.

---

## Tech Stack

- **Java 21**
- **Spring Boot 4**
- **Spring Web MVC**
- **Spring Data JPA**
- **Spring Security (Basic Auth + roles)**
- **H2 in-memory database**
- **Jakarta Validation**
- **Lombok**
- **JUnit 5 + Mockito**
- **Spring Boot integration testing with RestTestClient**

---

## Domain Model

### Product

A product represents an item in the store.

Fields:
- `id` – unique identifier
- `name` – product name (required)
- `description` – optional description
- `price` – product price (must be ≥ 0)
- `quantity` – available stock (must be ≥ 0)
- `createdAt` – automatically set on creation
- `lastUpdatedAt` – automatically updated on changes

The model is intentionally simple and avoids unnecessary complexity.

---

## API Overview

Base path: `/products`

---

### Create product (ADMIN only)

Creates a new product in the store.
This endpoint is restricted to users with the ADMIN role.

Endpoint:
POST /products

Request body:
{
"name": "Milk",
"description": "Fresh milk",
"price": 5.5,
"quantity": 10
}

Validation rules:
- name must not be blank
- price must be greater than or equal to 0
- quantity must be greater than or equal to 0

Response:
- 201 Created
- returns the created product

Example response:
{
"id": 1,
"name": "Milk",
"description": "Fresh milk",
"price": 5.5,
"quantity": 10
}

Authorization:
- Requires HTTP Basic Authentication
- Accessible only by users with the ADMIN role

### Get all products (USER, ADMIN)

Fetches all products from the store.

Endpoint:
GET /products

Response:
- 200 OK
- returns a list of products

Example response:
[
{
"id": 1,
"name": "Milk",
"description": "Fresh milk",
"price": 5.5,
"quantity": 10
},
{
"id": 2,
"name": "Bread",
"description": "Whole grain bread",
"price": 3.2,
"quantity": 20
}
]

Authorization:
- Requires HTTP Basic Authentication
- Accessible by users with USER or ADMIN role

### Get product by ID (USER, ADMIN)

Fetches a single product by its identifier.

Endpoint:
GET /products/{id}

Path parameters:
- id: product identifier

Response:
- 200 OK if the product exists
- 404 Not Found if the product does not exist

Example response:
{
"id": 1,
"name": "Milk",
"description": "Fresh milk",
"price": 5.5,
"quantity": 10
}

Authorization:
- Requires HTTP Basic Authentication
- Accessible by users with USER or ADMIN role

### Get product by name (USER, ADMIN)

Fetches a single product by its name.

Endpoint:
GET /products/by-name/{name}

Path parameters:
- name: product name

Response:
- 200 OK if the product exists
- 404 Not Found if the product does not exist

Example response:
{
"id": 1,
"name": "Milk",
"description": "Fresh milk",
"price": 5.5,
"quantity": 10
}

Authorization:
- Requires HTTP Basic Authentication
- Accessible by users with USER or ADMIN role

### Update product price (ADMIN only)

Updates the price of an existing product.

Endpoint:
PATCH /products/{id}/price

Path parameters:
- id: product identifier

Request body:
{
"price": 12.5
}

Validation rules:
- price must be greater than or equal to 0

Response:
- 200 OK
- returns the updated product

Example response:
{
"id": 1,
"name": "Milk",
"description": "Fresh milk",
"price": 12.5,
"quantity": 10
}

Authorization:
- Requires HTTP Basic Authentication
- Accessible only by users with the ADMIN role

### Update product quantity (ADMIN only)

Updates the available quantity of an existing product.

Endpoint:
PATCH /products/{id}/quantity

Path parameters:
- id: product identifier

Request body:
{
"quantity": 25
}

Validation rules:
- quantity must be greater than or equal to 0

Response:
- 200 OK
- returns the updated product

Example response:
{
"id": 1,
"name": "Milk",
"description": "Fresh milk",
"price": 5.5,
"quantity": 25
}

Authorization:
- Requires HTTP Basic Authentication
- Accessible only by users with the ADMIN role

### Delete product by ID (ADMIN only)

Deletes a product by its identifier.

Endpoint:
DELETE /products/{id}

Path parameters:
- id: product identifier

Response:
- 204 No Content

Notes:
- If the product does not exist, the operation is idempotent
- No error is returned for missing products

Authorization:
- Requires HTTP Basic Authentication
- Accessible only by users with the ADMIN role

### Delete product by name (ADMIN only)

Deletes a product by its name.

Endpoint:
DELETE /products/by-name/{name}

Path parameters:
- name: product name

Response:
- 204 No Content

Notes:
- If the product does not exist, the operation is idempotent
- No error is returned for missing products

Authorization:
- Requires HTTP Basic Authentication
- Accessible only by users with the ADMIN role

### Delete all products (ADMIN only)

Deletes all products from the store.

Endpoint:
DELETE /products

Response:
- 204 No Content

Notes:
- This operation removes all existing products
- Intended for administrative or cleanup purposes

Authorization:
- Requires HTTP Basic Authentication
- Accessible only by users with the ADMIN role