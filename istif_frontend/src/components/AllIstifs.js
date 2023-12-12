import axios from "axios";
import { useState, useEffect } from "react";
import parse from "html-react-parser";
import "./css/AllIstifs.css";
import { formatTimeAgo } from "../App";

function AllIstifs() {
  const [allIstifs, setAllIstifs] = useState([]);

  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/istif/all`, {
        withCredentials: true,
      })
      .then((response) => {
        setAllIstifs(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  return (
    <div className="all-istifs">
      <h1>All Istifs</h1>
      {allIstifs.map((istif) => (
        <div key={istif.id} className="istif">
          <h2 className="istif-title">
            <b>Title: </b>
            <a href={"/istif/" + istif.id}>{istif.title}</a>
          </h2>
          <p className="istif-details">
            <b>Text:</b> {parse(istif.text)}
          </p>
          <p className="istif-details">
            <b>Likes:</b> {istif.likeSize}
          </p>
          <p className="istif-details">
            <b>Labels:</b> {istif.labels.join(", ")}
          </p>
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
        </div>
      ))}
    </div>
  );
}

export default AllIstifs;
