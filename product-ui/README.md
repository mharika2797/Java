# Product UI — React + Spring Boot

A React frontend that connects to the Spring Boot Product API.

## Running locally

```bash
# Terminal 1 — start the Spring Boot backend (port 8080)
cd ../SpringDemo
mvn spring-boot:run

# Terminal 2 — start the React app (port 3000)
cd product-ui
npm start
```

Open http://localhost:3000 in a browser.

---

## Project structure

```
src/
  api/
    productApi.js          # all fetch() calls to the backend
  pages/
    ProductListPage.jsx    # home page — shows the product table
    CreateProductPage.jsx  # /create — form to add a new product
  App.js                   # router setup
```

---

# React Core Concepts

## useState

`useState` stores a value that, when changed, causes the component to re-render.

```jsx
const [count, setCount] = useState(0);
// count    → current value
// setCount → function to update it (triggers re-render)
```

Rules:
- Never mutate state directly (`state.push(x)` won't re-render). Always call the setter.
- For objects/arrays, spread to create a new reference: `setState(prev => ({ ...prev, key: val }))`.
- useState is initialized once — changing the initial value after mount has no effect.

```jsx
// Example — controlled input
const [name, setName] = useState("");
<input value={name} onChange={e => setName(e.target.value)} />
```

---

## useEffect

`useEffect` runs side effects (fetch, subscriptions, timers) after the component renders.

```jsx
useEffect(() => {
  // runs after every render
});

useEffect(() => {
  // runs once, on mount only
}, []);

useEffect(() => {
  // runs when `id` changes
}, [id]);

useEffect(() => {
  const timer = setInterval(() => {}, 1000);
  return () => clearInterval(timer); // cleanup runs before next effect or on unmount
}, []);
```

Common pattern — fetch on mount:

```jsx
const [products, setProducts] = useState([]);

useEffect(() => {
  async function load() {
    const data = await fetchAllProducts();
    setProducts(data);
  }
  load();
}, []); // empty array = run once when the component mounts
```

---

## useNavigate

Programmatic navigation (from react-router-dom v6).

```jsx
const navigate = useNavigate();
navigate("/create");                  // go to /create
navigate(-1);                         // go back (like browser back button)
navigate("/", { replace: true });     // replace current history entry
```

---

## Props vs State

| | Props | State |
|---|---|---|
| Owned by | Parent | The component itself |
| Mutable? | No (read-only in child) | Yes, via setter |
| Purpose | Pass data down | Track changing data |

```jsx
// Parent passes name as a prop
function Parent() {
  return <Greeting name="Alice" />;
}

// Child receives it as props (read-only)
function Greeting({ name }) {
  return <h1>Hello, {name}</h1>;
}
```

---

## Controlled vs Uncontrolled Components

**Controlled** — React state is the single source of truth for the input value.

```jsx
const [email, setEmail] = useState("");
<input value={email} onChange={e => setEmail(e.target.value)} />
```

**Uncontrolled** — the DOM manages the value; React reads it via a ref.

```jsx
const inputRef = useRef();
<input ref={inputRef} />
// read: inputRef.current.value
```

Prefer controlled components — they make validation and form submission predictable.

---

## useRef

`useRef` gives a mutable box that persists across renders without causing a re-render when changed.

```jsx
// 1 — access a DOM element directly
const inputRef = useRef(null);
<input ref={inputRef} />
inputRef.current.focus();

// 2 — store a value that should NOT trigger a re-render
const renderCount = useRef(0);
renderCount.current += 1;
```

---

## useContext

Avoids prop-drilling by sharing data across the component tree without passing props at every level.

```jsx
// 1. Create the context
const ThemeContext = createContext("light");

// 2. Provide it high in the tree
<ThemeContext.Provider value="dark">
  <App />
</ThemeContext.Provider>

// 3. Consume anywhere below
function Button() {
  const theme = useContext(ThemeContext);
  return <button className={theme}>Click</button>;
}
```

---

## Component lifecycle (with hooks)

| Lifecycle phase | Hook equivalent |
|---|---|
| Mount (first render) | `useEffect(() => { ... }, [])` |
| Update (state/prop change) | `useEffect(() => { ... }, [dep])` |
| Unmount (cleanup) | Return a function from `useEffect` |

---

## Key interview questions

**Q: What is the virtual DOM?**
React keeps a lightweight copy of the real DOM in memory. When state changes, React re-renders to a new virtual DOM, diffs it against the previous one, and applies only the minimal real DOM changes. This is called reconciliation.

**Q: Why do we need keys in lists?**
Keys help React identify which items changed, were added, or were removed. Without stable keys, React re-renders the entire list on every change. Keys must be unique among siblings and stable (don't use array index if the list can be reordered).

```jsx
{products.map(p => <tr key={p.id}>…</tr>)}
```

**Q: What is the difference between `useEffect` and `useLayoutEffect`?**
`useEffect` runs asynchronously after the browser has painted. `useLayoutEffect` runs synchronously after DOM mutations but before the browser paints — use it only when you need to measure the DOM to avoid flicker.

**Q: What causes a re-render?**
1. `setState` / setter from `useState` is called
2. A parent re-renders and passes new props
3. `useContext` value changes

**Q: What is prop drilling and how do you avoid it?**
Prop drilling is passing props through many intermediate components that don't use them. Solutions: useContext (built-in), a state manager like Redux/Zustand, or component composition (passing JSX as children).

**Q: What is a custom hook?**
A function whose name starts with `use` and that calls other hooks. It lets you extract and share stateful logic without changing component hierarchy.

```jsx
function useFetch(url) {
  const [data, setData]   = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(url)
      .then(r => r.json())
      .then(setData)
      .catch(setError);
  }, [url]);

  return { data, error };
}
```

**Q: What is React.memo?**
Wraps a component so it skips re-rendering when its props haven't changed (shallow comparison). Use it for expensive components that receive the same props often.

```jsx
const ProductRow = React.memo(function ProductRow({ product }) {
  return <tr><td>{product.name}</td></tr>;
});
```

**Q: What is the difference between `useMemo` and `useCallback`?**

| | `useMemo` | `useCallback` |
|---|---|---|
| Returns | A memoized **value** | A memoized **function** |
| Use when | Expensive calculation | Stable function reference for child props |

```jsx
const total = useMemo(() => prices.reduce((a, b) => a + b, 0), [prices]);
const handleClick = useCallback(() => doSomething(id), [id]);
```

**Q: Controlled vs uncontrolled components — when to use which?**
Prefer controlled for forms that need validation, conditional fields, or submission logic. Use uncontrolled (useRef) when integrating with a non-React library that needs direct DOM access, or for file inputs (which can't be controlled).

**Q: What is the StrictMode double-render?**
`<React.StrictMode>` intentionally renders components twice in development to surface side effects in the render phase. It only applies in development — production builds render once.

**Q: Explain the Rules of Hooks.**
1. Only call hooks at the top level — never inside loops, conditions, or nested functions.
2. Only call hooks from React functions (components or custom hooks) — not plain JS functions.

These rules ensure hooks are called in the same order on every render, which is how React tracks state between renders.
