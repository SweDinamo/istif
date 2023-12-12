import React, { useState } from "react";
import axios from "axios";
import "./css/Register.css";
import { useNavigate } from "react-router-dom";
import { Space,message } from "antd";

const RegisterComponent = () => {
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [retypePassword, setRetypePassword] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [messageApi, contextHolder] = message.useMessage();
  const Navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (password !== retypePassword) {
      setPasswordError("Passwords do not match");
      messageApi.open({ type: "error", content: "Passwords do not match" });
      return;
    }

    const data = {
      email,
      username,
      password,
    };

    try {
      const response = await axios.post(
        `${process.env.REACT_APP_BACKEND_URL}/api/user/register`,
        data
      );
      console.log(response.data);
      await messageApi.open({
        type: "success",
        content: "Registered successfully!",
      });
      Navigate("/login");
    } catch (error) {
      console.error(error);
      messageApi.open({
        type: "error",
        content: "Error occurred during registration!",
      });
    }
  };

  const handleRetypePasswordChange = (e) => {
    setRetypePassword(e.target.value);
    setPasswordError("");
  };

  return (
    <Space
      direction="vertical"
      style={{
        width: "100%",
      }}
    >
      {contextHolder}
      <div>
        <form className="register-form" onSubmit={handleSubmit}>
          <h2 className="register-heading">Register</h2>
          <div className="register-input-group">
            <label className="register-label">Email:</label>
            <input
              type="email"
              className="register-input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="register-input-group">
            <label className="register-label">Username:</label>
            <input
              type="text"
              className="register-input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div className="register-input-group">
            <label className="register-label">Password:</label>
            <input
              type="password"
              className="register-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <div className="register-input-group">
            <label className="register-label">Retype Password:</label>
            <input
              type="password"
              className="register-input"
              value={retypePassword}
              onChange={handleRetypePasswordChange}
              required
            />
          </div>
          {passwordError && (
            <div className="register-error">{passwordError}</div>
          )}
          <button type="submit" className="register-button">
            Register
          </button>
        </form>
      </div>
    </Space>
  );
};

export default RegisterComponent;
