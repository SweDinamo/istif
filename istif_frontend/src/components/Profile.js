import axios from "axios";
import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import photoPreview from "../profile_pic.png";
import "./css/Profile.css";

function Profile() {
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");
  const [isFollowing, setIsFollowing] = useState();
  const [followButtonName, setFollowButtonName] = useState();
  const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;


  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/user/profile`, {
        withCredentials: true,
      })
      .then((response) => {
        const loggedInUser = response.data;
        if (loggedInUser.following && loggedInUser.following.includes(id)) {
          setFollowButtonName("Unfollow");
          setIsFollowing(true);
        } else {
          setFollowButtonName("Follow");
          setIsFollowing(false);
        }
      })
      .catch((error) => {
        setError(error);
      });
  }, [id]);

  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/user/${id}`, {
        withCredentials: true,
      })
      .then((response) => {
        setUser(response.data);
      })
      .catch((error) => {
        setError(error);
      });
  }, [id, BACKEND_URL]);

  const handleFollowClick = () => {
    if (isFollowing) {
      axios
        .post(
          `${process.env.REACT_APP_BACKEND_URL}/api/user/follow/`,
          { userId: id },
          { withCredentials: true }
        )
        .then((response) => {
          setFollowButtonName("Follow");
          setIsFollowing(false);
        })
        .catch((error) => {
          setError(error);
        });
    } else {
      axios
        .post(
          `${process.env.REACT_APP_BACKEND_URL}/api/user/follow`,
          { userId: id },
          { withCredentials: true }
        )
        .then((response) => {
          setFollowButtonName("Unfollow");
          setIsFollowing(true);
        })
        .catch((error) => {
          setError(error);
        });
    }
  };

  if (error) {
    return <div>Error: {error.message}</div>;
  } else if (!user) {
    return <div>Loading...</div>;
  } else {
    return (
      <div className="profile-container">
        <div className="profile-photo-container">
          <label htmlFor="photo" className="profile-photo-label">
            Photo:
          </label>
          <p>
            {
              <img
                className="profile-photo"
                src={user.profilePhoto || photoPreview}
                alt={user.username}
              />
            }
          </p>
        </div>
        <h1 className="profile-username">Username: {user.username}</h1>
        <p className="profile-biography">Biography: {user.biography}</p>
        <button className="follow-button" onClick={handleFollowClick}>
          {followButtonName}
        </button>
      </div>
    );
  }
}

export default Profile;
