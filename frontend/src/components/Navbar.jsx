import { Link, useNavigate } from "react-router-dom";

function Navbar() {
  const navigate = useNavigate();
  const roles = JSON.parse(localStorage.getItem("roles") || "[]");
  const isAdmin = roles.includes("ROLE_ADMIN");

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("roles");
    navigate("/login");
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark px-4">
      <Link className="navbar-brand" to="/dashboard">
        SecureAuth
      </Link>

      <div className="ms-auto d-flex gap-3 align-items-center">
        <Link className="nav-link text-white" to="/dashboard">
          Dashboard
        </Link>

        {isAdmin && (
          <Link className="nav-link text-white" to="/admin/users">
            Admin Users
          </Link>
        )}

        <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;