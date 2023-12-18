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
import EditIstifForm from "./components/EditIstif"
import MyIstifs from "./components/MyIstifs";
import IstifDetails from "./components/IstifDetails";
import AllIstifs from "./components/AllIstifs";
import FollowedUserIstifs from "./components/FollowedUserIstifs";
import IstifSearch from "./components/IstifSearch";
import axios from "axios";

export function formatTimeAgo(dateStr) {
  const dateParts = dateStr.split(/[/ :]/);
  const year = parseInt(dateParts[2], 10);
  const month = parseInt(dateParts[1], 10) - 1;
  const day = parseInt(dateParts[0], 10);
  const hour = parseInt(dateParts[3], 10);
  const minute = parseInt(dateParts[4], 10);
  const date = new Date(year, month, day, hour, minute);

  const now = new Date();
  const timeDifference = now - date;
  const secondsDifference = Math.floor(timeDifference / 1000);
  const minutesDifference = Math.floor(secondsDifference / 60);
  const hoursDifference = Math.floor(minutesDifference / 60);
  const daysDifference = Math.floor(hoursDifference / 24);
  const monthsDifference = Math.floor(daysDifference / 30);


  const timeUnits = [
    { unit: "month", value: monthsDifference },
    { unit: "day", value: daysDifference },
    { unit: "hour", value: hoursDifference },
    { unit: "minute", value: minutesDifference },
  ];
  const mostSignificantUnit = timeUnits.find((unit) => unit.value > 0);

  if (mostSignificantUnit) {
    const unitString =
      mostSignificantUnit.unit + (mostSignificantUnit.value > 1 ? "s" : "");
    return `${mostSignificantUnit.value} ${unitString} ago (${dateStr})`;
  } else {
    return `just now (${dateStr})`;
  }
}

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
      <header className="app-header">
        <Link to="/" className="logo-link">
          <img src={IstifLogo} alt="Logo" className="logo" />
        </Link>
        <nav className="nav-bar">
          {loggedIn ? (
            <>
              <Link to="/istif/all-istifs" className="nav-link">
                Home
              </Link>
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
              <Link to="/" className="nav-link">
                Home
              </Link>
              <Link to="/login" className="nav-link">
                Login
              </Link>
              <Link to="/register" className="nav-link">
                Register
              </Link>
            </>
          )}
        </nav>
      </header>
      <Routes>
        <Route
          path="/"
          element={
            <div className="istif-container">
              <h2>
                Welcome to Istif, your stack application that you can keep and
                share their personal preference of content to everyone!
              </h2>
              <img src={IstifImage} alt="Istif" className="istif-image" />
            </div>
          }
        />
        <Route path="/user/my-profile" element={<User />} />
        <Route path="/login" element={<Login onLogin={handleLogin} />} />
        <Route path="/register" element={<Register />} />
        <Route path="/istif/add-istif" element={<AddIstifForm />} />
        <Route path="/istif/edit/:id" element={<EditIstifForm />} />
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