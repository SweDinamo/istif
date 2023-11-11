import axios from "axios";
import { useState, useEffect } from "react";
import parse from "html-react-parser";
import "./css/AllIstifs.css";

function MyIstifs() {
  const [myIstifs, setMyIstifs] = useState([]);
  const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;

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
      .get(
        `${process.env.REACT_APP_BACKEND_URL}/api/istif/fromUser`,
        {
          withCredentials: true,
        }
      )
      .then((response) => {
        setMyIstifs(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [BACKEND_URL]);

  const handleDelete = (istifId) => {
    axios
      .get(
        `${process.env.REACT_APP_BACKEND_URL}/api/istif/delete/${istifId}`,
        {
          withCredentials: true,
        }
      )
      .then(() => {
        setMyIstifs((prevIstifs) =>
          prevIstifs.filter((istif) => istif.id !== istifId)
        );
      })
      .catch((error) => {
        console.log(error);
      });
  };

  return (
    <div className="all-istifs">
      <h1>My Istifs</h1>
      {myIstifs.map((istif) => (
        <div key={istif.id} className="istif">
          <h2 className="istif-title">
            <a href={"/istif/" + istif.id}>{istif.title}</a>
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
          <button
            className="delete-button"
            onClick={() => handleDelete(istif.id)}
          >
            Delete
          </button>
        </div>
      ))}
    </div>
  );
}

export default MyIstifs;
