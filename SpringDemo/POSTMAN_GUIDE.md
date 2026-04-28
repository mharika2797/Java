# How to Run & Test

## 1. Start the app
```bash
cd SpringDemo
./mvnw spring-boot:run
# or: mvn spring-boot:run
```
App starts at http://localhost:8080

---

## Controller 1 — CRUD (Local H2 Database)
Base URL: `http://localhost:8080/api/products`

### GET all products
```
GET http://localhost:8080/api/products
```

### GET one product
```
GET http://localhost:8080/api/products/1
```

### POST — create a product
```
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Headphones",
  "description": "Noise-cancelling over-ear",
  "price": 199.99
}
```

### PUT — update a product
```
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
  "name": "Laptop Pro",
  "description": "Updated 16-inch model",
  "price": 1499.99
}
```

### DELETE — delete a product
```
DELETE http://localhost:8080/api/products/1
```
Returns 204 No Content on success.

### H2 Console (browser)
```
http://localhost:8080/h2-console
JDBC URL:  jdbc:h2:mem:demodb
User:      sa
Password:  (leave blank)
```

---

## Controller 2 — External API (JSONPlaceholder)
Base URL: `http://localhost:8080/api/posts`

### GET all posts  (fetches from jsonplaceholder.typicode.com)
```
GET http://localhost:8080/api/posts
```

### GET one post  (id: 1–100)
```
GET http://localhost:8080/api/posts/1
```

---

## Controller 3 — GraphQL
Single endpoint: `POST http://localhost:8080/graphql`

**Postman setup:**
- Method: `POST`
- URL: `http://localhost:8080/graphql`
- Body → raw → JSON

### Query: get all books
```json
{
  "query": "{ books { id title author year genre } }"
}
```

### Query: get one book (choose which fields you want — that's GraphQL's power)
```json
{
  "query": "{ book(id: \"1\") { title author } }"
}
```

### Query: get books with only title and year  (no over-fetching!)
```json
{
  "query": "{ books { title year } }"
}
```

### Mutation: add a book
```json
{
  "query": "mutation { addBook(title: \"Refactoring\", author: \"Martin Fowler\", year: 1999, genre: \"Programming\") { id title } }"
}
```

### Mutation: delete a book
```json
{
  "query": "mutation { deleteBook(id: \"2\") }"
}
```

### GraphiQL (browser IDE — easier for exploring)
```
http://localhost:8080/graphiql
```
Paste any query above (without the outer quotes) and run it interactively.

---

## GraphQL vs REST — quick explanation
| | REST | GraphQL |
|---|---|---|
| Endpoints | Many (`/books`, `/books/1`, …) | One (`/graphql`) |
| Shape of response | Fixed by server | **Client decides** which fields it wants |
| Over-fetching | Common (you get all fields even if you need 2) | Never — you ask for exactly what you need |
| Under-fetching | N+1 calls to get related data | One query can fetch nested data |
| Mutations | POST / PUT / DELETE | `mutation { }` block |
| Schema | Implicit (OpenAPI optional) | **Required** — the schema IS the contract |
