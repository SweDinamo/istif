import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import "./App.css";
import IstifImage from "./istiffinal_1.gif";
import IstifLogo from "./istif_logo.png";
import User from "./components/User";
import Profile from "./components/Profile";
import Login from "./components/Login";
import Register from "./components/Register";
import AddIstifForm from "./components/AddIstif";
import MyIstifs from "./components/MyIstifs";
import IstifDetails from "./components/IstifDetails";
import AllIstifs from "./components/AllIstifs";
import FollowedUserIstifs from "./components/FollowedUserIstifs";
import IstifSearch from "./components/IstifSearch";
import axios from "axios";

function App() {
  const [loggedIn, setLoggedIn] = useState(false);

  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/user/profile`, {
        withCredentials: true,
      })
      .then((response) => {
        setLoggedIn(true);
      })
      .catch((error) => {
        setLoggedIn(false);
      });
  }, []);

  const handleLogout = () => {
    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/user/logout`, null, {
        withCredentials: true,
      })
      .then((response) => {
        document.cookie =
          "Bearer=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/api;";
        setLoggedIn(false);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleLogin = () => {
    setLoggedIn(true);
  };

  return (
    <Router>
      <nav className="nav">
        <div className="logo-container">
          <Link to="/">
            <img src={IstifLogo} alt="Logo" className="logo" />
          </Link>
        </div>
        <Link to="/" className="nav-link">
          Home
        </Link>
        {loggedIn ? (
          <>
            <Link to="/" onClick={handleLogout} className="nav-link">
              Logout
            </Link>
            <Link to="/user/my-profile" className="nav-link">
              My Profile
            </Link>
            <Link to="/istif/add-istif" className="nav-link">
              Add Istif
            </Link>
            <Link to="/istif/followings" className="nav-link">
              Istif Feed
            </Link>
            <Link to="/istif/my-istifs" className="nav-link">
              My Istifs
            </Link>
            <Link to="/istif/all-istifs" className="nav-link">
              All Istifs
            </Link>
            <Link to="/istif/search" className="nav-link">
              Search
            </Link>
          </>
        ) : (
          <>
            <Link to="/login" className="nav-link">
              Login
            </Link>
            <Link to="/register" className="nav-link">
              Register
            </Link>
          </>
        )}
      </nav>
      <Routes>
        <Route
          path="/"
          element={
            <div className="istif-container">
              <img src={IstifImage} alt="Istif" className="istif-image" />
              <h2>
                Welcome to Istif, your stack application that you can keep
                and share their personal preference of content to everyone!
              </h2>
            </div>
          }
        />
        <Route path="/user/my-profile" element={<User />} />
        <Route path="/login" element={<Login onLogin={handleLogin} />} />
        <Route path="/register" element={<Register />} />
        <Route path="/istif/add-istif" element={<AddIstifForm />} />
        <Route path="/istif/my-istifs" element={<MyIstifs />} />
        <Route path="/istif/followings" element={<FollowedUserIstifs />} />
        <Route path="/istif/all-istifs" element={<AllIstifs />} />
        <Route path="/istif/:id" element={<IstifDetails />} />
        <Route path="/user/:id" element={<Profile />} />
        <Route path="/istif/search" element={<IstifSearch />} />
      </Routes>
    </Router>
  );
}

export default App;