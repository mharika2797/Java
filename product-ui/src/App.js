import { BrowserRouter, Routes, Route } from "react-router-dom";
import ProductListPage   from "./pages/ProductListPage";
import CreateProductPage from "./pages/CreateProductPage";

// BrowserRouter wraps the whole app — gives all children access to routing
// Routes picks the first <Route> whose path matches the current URL
export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/"       element={<ProductListPage />} />
        <Route path="/create" element={<CreateProductPage />} />
      </Routes>
    </BrowserRouter>
  );
}
