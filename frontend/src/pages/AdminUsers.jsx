import { useEffect, useState } from "react";
import api from "../api/axiosConfig";
import Navbar from "../components/Navbar";

function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState("");

  const fetchUsers = async () => {
    try {
      const response = await api.get("/admin/users");
      setUsers(response.data);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load users");
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const updateUserStatus = async (userId, enabled) => {
    try {
        await api.patch(`/admin/users/${userId}/status?enabled=${enabled}`);
        fetchUsers();
    } catch (err) {
        setError(err.response?.data?.message || "Failed to update user status");
    }
    };

    const updateUserRole = async (userId, roleName, action) => {
    try {
        await api.patch(`/admin/users/${userId}/roles/${action}?roleName=${roleName}`);
        fetchUsers();
    } catch (err) {
        setError(err.response?.data?.message || "Failed to update user role");
    }
    };

  return (
    <>
    <Navbar />
    <div className="container mt-5">
      <h2>Admin - Users</h2>

      {error && <div className="alert alert-danger">{error}</div>}

      <table className="table table-bordered table-striped mt-4">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Enabled</th>
            <th>Roles</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.firstName} {user.lastName}</td>
              <td>{user.email}</td>
              <td>{user.enabled ? "Yes" : "No"}</td>
              <td>{user.roles?.join(", ")}</td>
              <td>
                {user.enabled ? (
                    <button
                    className="btn btn-sm btn-warning"
                    onClick={() => updateUserStatus(user.id, false)}
                    >
                    Disable
                    </button>
                ) : (
                    <button
                    className="btn btn-sm btn-success"
                    onClick={() => updateUserStatus(user.id, true)}
                    >
                    Enable
                    </button>
                )}
                <div className="mt-2">
                {user.roles?.includes("ROLE_ADMIN") ? (
                    <button
                    className="btn btn-sm btn-outline-danger"
                    onClick={() => updateUserRole(user.id, "ROLE_ADMIN", "remove")}
                    >
                    Remove Admin
                    </button>
                ) : (
                    <button
                    className="btn btn-sm btn-outline-primary"
                    onClick={() => updateUserRole(user.id, "ROLE_ADMIN", "add")}
                    >
                    Make Admin
                    </button>
                )}
                </div>
                </td>
                <td>
                {user.roles?.map((role) => (
                    <span key={role} className="badge bg-secondary badge-role">
                    {role}
                    </span>
                ))}
                </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
    </>
  );
}

export default AdminUsers;