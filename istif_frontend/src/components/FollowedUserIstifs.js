import axios from "axios";
import { useState, useEffect } from "react";
import parse from "html-react-parser";
import "./css/AllIstifs.css";

function FollowedUserIstifs() {
  const [followedUserIstifs, setFollowedUserIstifs] = useState([]);

  function formatDate(dateString) {
    const date = new Date(dateString);
    const day = date.getDate();
    const month = date.getMonth() + 1;
    const year = date.getFullYear();
    const hours = date.getHours();
    const minutes = date.getMinutes().toString().padStart(2, "0");

    const formattedDate = `${day}/${month}/${year} ${hours}:${minutes}`;

    return formattedDate;
  }

  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/istif/following`, {
        withCredentials: true,
      })
      .then((response) => {
        setFollowedUserIstifs(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  return (
    <div className="all-istifs">
      <h1>Istif Feed</h1>
      {followedUserIstifs.map((istif) => (
        <div key={istif.id} className="istif">
          <h2 className="istif-title">
            <b>Title: </b>
            <a href={"/istif/" + istif.id}>{istif.titleLink}</a>
          </h2>
          <p className="istif-title">
            <b>Text:</b> {parse(istif.text)}
          </p>
          <p className="istif-details">
            <b>Likes:</b> {istif.likes ? istif.likes.length : 0}
          </p>
          <p className="istif-details">
            <b>Labels:</b> {istif.labels.join(", ")}
          </p>
          <p className="istif-details">
            <b>Written by:</b>{" "}
            <a href={"/user/" + istif.user.id}>{istif.user.username}</a>
          </p>
          <p className="istif-details">
            <b>Published at:</b> {formatDate(istif.createdAt)}
          </p>
        </div>
      ))}
    </div>
  );
}

export default FollowedUserIstifs;
