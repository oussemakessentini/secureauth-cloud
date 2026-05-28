import { Navigate } from "react-router-dom";

function AdminRoute({ children }) {
  const roles = JSON.parse(localStorage.getItem("roles") || "[]");

  if (!roles.includes("ROLE_ADMIN")) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}

export default AdminRoute;