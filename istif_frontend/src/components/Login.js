import React, { useState } from "react";
import axios from "axios";
import "./css/Login.css";
import { useNavigate } from "react-router-dom";
import {message } from "antd";

function LoginComponent({ onLogin }) {
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const [messageApi] = message.useMessage();

  const handleLogin = (event) => {
    event.preventDefault();

    axios
      .post(
        `${process.env.REACT_APP_BACKEND_URL}/api/user/login`,
        { identifier, password },
        { withCredentials: true }
      )
      .then((response) => {
        const cookieValue = response.headers["bearer"];
        localStorage.setItem("authToken", cookieValue);
        onLogin();
        navigate("/istif/all-istifs");
        messageApi.open({
          type: "success",
          content: "You logged in successfully!",
        });
      })
      .catch((error) => {
        if (error.response && error.response.status === 401) {
          messageApi.open({
            type: "error",
            content: "Invalid username or password.",
          });
        } else {
          messageApi.open({
            type: "error",
            content: "An error occurred while logging in.",
          });
        }
      });
  };

  return (
    <div>
      <form className="login-form" onSubmit={handleLogin}>
        <h2 className="login-heading">Log In</h2>
        <div className="login-input-group">
          <label htmlFor="identifier" className="login-label">
            Username or Email:
          </label>
          <input
            type="text"
            id="identifier"
            className="login-input"
            value={identifier}
            onChange={(event) => setIdentifier(event.target.value)}
          />
        </div>
        <div className="login-input-group">
          <label htmlFor="password" className="login-label">
            Password:
          </label>
          <input
            type="password"
            id="password"
            className="login-input"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </div>
        <button type="submit" className="login-button">
          Log in
        </button>
      </form>
    </div>
  );

}

export default LoginComponent;
