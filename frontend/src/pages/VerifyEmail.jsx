import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import api from "../api/axiosConfig";

function VerifyEmail() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [message, setMessage] = useState("Verifying email...");
  const [error, setError] = useState("");

  useEffect(() => {
    const verifyEmail = async () => {
      try {
        const token = searchParams.get("token");

        await api.get(`/auth/verify-email/${token}`);

        setMessage("Email verified successfully!");

        setTimeout(() => {
          navigate("/login?verified=true");
        }, 2000);

      } catch (err) {
        setError(
          err.response?.data?.message ||
          "Email verification failed"
        );
      }
    };

    verifyEmail();
  }, [navigate, searchParams]);

  return (
    <div className="container mt-5">
      <div className="card shadow p-4 text-center">
        {error ? (
          <div className="alert alert-danger">{error}</div>
        ) : (
          <div className="alert alert-success">{message}</div>
        )}
      </div>
    </div>
  );
}

export default VerifyEmail;