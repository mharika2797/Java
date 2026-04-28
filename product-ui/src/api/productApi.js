// All HTTP calls to the Spring Boot backend live here.
// Backend runs on http://localhost:8080 — start it with: mvn spring-boot:run

const BASE_URL = "http://localhost:8080/api/products";

export async function fetchAllProducts() {
  const res = await fetch(BASE_URL);
  if (!res.ok) throw new Error("Failed to fetch products");
  return res.json();
}

export async function createProduct(product) {
  const res = await fetch(BASE_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(product),
  });
  if (!res.ok) {
    const err = await res.json();
    throw new Error(err.message || "Failed to create product");
  }
  return res.json();
}
