import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchAllProducts } from "../api/productApi";

// ─── ProductListPage ──────────────────────────────────────────────────────────
//  Demonstrates:
//    useState  — stores the products array and loading/error state
//    useNavigate — programmatic navigation to the Create page
//
//  Flow on "Load Products" click:
//    button click → fetchAllProducts() → GET /api/products → render table

export default function ProductListPage() {
  const [products, setProducts] = useState([]);   // data from backend
  const [loading, setLoading]   = useState(false); // spinner flag
  const [error, setError]       = useState(null);  // error message
  const [loaded, setLoaded]     = useState(false); // whether fetch ran at least once

  const navigate = useNavigate();

  async function handleLoadProducts() {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchAllProducts(); // calls GET /api/products
      setProducts(data);
      setLoaded(true);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={styles.page}>
      <h1 style={styles.title}>Product Store</h1>

      <div style={styles.buttonRow}>
        <button style={styles.primaryBtn} onClick={handleLoadProducts} disabled={loading}>
          {loading ? "Loading…" : "Show All Products"}
        </button>

        <button style={styles.secondaryBtn} onClick={() => navigate("/create")}>
          + Create New Product
        </button>
      </div>

      {error && <p style={styles.error}>Error: {error}</p>}

      {loaded && products.length === 0 && !loading && (
        <p style={styles.empty}>No products found. Create one!</p>
      )}

      {products.length > 0 && (
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>ID</th>
              <th style={styles.th}>Name</th>
              <th style={styles.th}>Description</th>
              <th style={styles.th}>Price ($)</th>
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id} style={styles.tr}>
                <td style={styles.td}>{p.id}</td>
                <td style={styles.td}>{p.name}</td>
                <td style={styles.td}>{p.description}</td>
                <td style={styles.td}>{p.price.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

const styles = {
  page:         { maxWidth: 800, margin: "40px auto", fontFamily: "sans-serif", padding: "0 16px" },
  title:        { fontSize: 28, marginBottom: 24, color: "#1a1a2e" },
  buttonRow:    { display: "flex", gap: 12, marginBottom: 24 },
  primaryBtn:   { padding: "10px 20px", background: "#4361ee", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 15 },
  secondaryBtn: { padding: "10px 20px", background: "#fff", color: "#4361ee", border: "2px solid #4361ee", borderRadius: 6, cursor: "pointer", fontSize: 15 },
  error:        { color: "#e63946", fontWeight: "bold" },
  empty:        { color: "#666" },
  table:        { width: "100%", borderCollapse: "collapse", marginTop: 8 },
  th:           { background: "#4361ee", color: "#fff", padding: "10px 14px", textAlign: "left" },
  tr:           { borderBottom: "1px solid #e0e0e0" },
  td:           { padding: "10px 14px", color: "#333" },
};
