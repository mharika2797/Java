import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createProduct } from "../api/productApi";

// ─── CreateProductPage ────────────────────────────────────────────────────────
//  Demonstrates:
//    useState  — controlled form inputs (every keystroke updates state)
//    async/await — calls POST /api/products, waits for 201 response
//    useNavigate — goes back to the product list after a successful create
//
//  Flow on "Save" click:
//    form submit → createProduct(form) → POST /api/products → navigate("/")

const EMPTY_FORM = { name: "", description: "", price: "" };

export default function CreateProductPage() {
  const [form, setForm]       = useState(EMPTY_FORM); // controlled form state
  const [saving, setSaving]   = useState(false);       // disables button while saving
  const [error, setError]     = useState(null);        // backend error message
  const [success, setSuccess] = useState(false);       // show success banner

  const navigate = useNavigate();

  // Controlled input handler — one function handles all fields
  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault(); // prevent default browser form submission
    setSaving(true);
    setError(null);
    try {
      await createProduct({
        name: form.name,
        description: form.description,
        price: parseFloat(form.price), // backend expects a number
      });
      setSuccess(true);
      setForm(EMPTY_FORM);
      // Navigate back to the list after a short delay so the user sees the success message
      setTimeout(() => navigate("/"), 1200);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div style={styles.page}>
      <button style={styles.backBtn} onClick={() => navigate("/")}>
        ← Back to Products
      </button>

      <h1 style={styles.title}>Create Product</h1>

      {success && <p style={styles.success}>Product created! Redirecting…</p>}
      {error   && <p style={styles.error}>Error: {error}</p>}

      <form onSubmit={handleSubmit} style={styles.form}>
        <label style={styles.label}>
          Name *
          <input
            style={styles.input}
            name="name"
            value={form.name}
            onChange={handleChange}
            required
            placeholder="e.g. Laptop"
          />
        </label>

        <label style={styles.label}>
          Description
          <input
            style={styles.input}
            name="description"
            value={form.description}
            onChange={handleChange}
            placeholder="e.g. High performance laptop"
          />
        </label>

        <label style={styles.label}>
          Price ($) *
          <input
            style={styles.input}
            name="price"
            type="number"
            step="0.01"
            min="0.01"
            value={form.price}
            onChange={handleChange}
            required
            placeholder="e.g. 999.99"
          />
        </label>

        <button style={styles.submitBtn} type="submit" disabled={saving}>
          {saving ? "Saving…" : "Save Product"}
        </button>
      </form>
    </div>
  );
}

const styles = {
  page:      { maxWidth: 480, margin: "40px auto", fontFamily: "sans-serif", padding: "0 16px" },
  backBtn:   { background: "none", border: "none", color: "#4361ee", cursor: "pointer", fontSize: 15, marginBottom: 16, padding: 0 },
  title:     { fontSize: 26, marginBottom: 24, color: "#1a1a2e" },
  success:   { color: "#2d6a4f", background: "#d8f3dc", padding: "10px 14px", borderRadius: 6, marginBottom: 16 },
  error:     { color: "#e63946", background: "#ffe8e8", padding: "10px 14px", borderRadius: 6, marginBottom: 16 },
  form:      { display: "flex", flexDirection: "column", gap: 18 },
  label:     { display: "flex", flexDirection: "column", gap: 6, fontSize: 14, fontWeight: 600, color: "#333" },
  input:     { padding: "10px 12px", border: "1px solid #ccc", borderRadius: 6, fontSize: 15, outline: "none" },
  submitBtn: { padding: "12px", background: "#4361ee", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer", fontSize: 16, fontWeight: 600 },
};
