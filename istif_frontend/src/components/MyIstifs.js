import axios from "axios";
import { useState, useEffect } from "react";
import parse from "html-react-parser";
import "./css/AllIstifs.css";
import { useNavigate } from "react-router-dom";
import { formatTimeAgo } from "../App";


function MyIstifs() {
  const [myIstifs, setMyIstifs] = useState([]);
  const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
  const navigate = useNavigate();

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

  const handleEdit = (istifId) => {
    navigate(`/istif/edit/${istifId}`);
  };

  return (
    <div className="all-istifs">
      <h1>My Istifs</h1>
      {myIstifs.map((istif) => (
        <div key={istif.id} className="istif">
          <h2 className="istif-details">
            <b>Title: </b>
            <a href={"/istif/" + istif.id}>{istif.title}</a>
          </h2>
          <p className="istif-details">
            <b>Text:</b> {parse(istif.text)}
          </p>
          {istif.likeSize > 0 && (
            <p className="istif-details">
              <b>Likes:</b> {istif.likeSize}
            </p>
          )}
          {istif.labels && (
            <p className="istif-details">
              <b>Labels:</b> {istif.labels.join(", ")}
            </p>
          )}
          <p className="istif-details">
            <b>Written by:</b>{" "}
            <a href={"/user/" + istif.user.id}>{istif.user.username}</a>
          </p>
          <p className="istif-details">
            <b>Created at:</b> {formatTimeAgo(istif.createdAt)}
          </p>
          {istif.istifDate && (
            <p className="istif-details">
              <b>Relevant Date:</b> {istif.istifDate}
            </p>
          )}
          {istif.editedAt && (
            <p className="istif-details">
              <b>Edited at:</b> {formatTimeAgo(istif.editedAt)}
            </p>
          )}
          <button
            className="delete-button"
            onClick={() => handleEdit(istif.id)}
          >
            Edit
          </button>
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
