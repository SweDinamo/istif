import axios from "axios";
import { useEffect, useState } from "react";
import parse from "html-react-parser";
import { useParams } from "react-router-dom";
import "react-quill/dist/quill.snow.css";
import "./css/AllIstifs.css";

function IstifDetails() {
  const { id } = useParams();
  const [istif, setIstif] = useState(null);
  const [commentText, setCommentText] = useState("");
  const [currentUserId, setCurrentUserId] = useState(null);

  // Fetch the istif details
  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/istif/${id}`, {
        withCredentials: true,
      })
      .then((response) => {
        setIstif(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [id]);

  // Fetch the current user's profile
  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_BACKEND_URL}/api/user/profile`, {
        withCredentials: true,
      })
      .then((response) => {
        setCurrentUserId(response.data.id);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  const handleCommentSubmit = async (event) => {
    event.preventDefault();
    const comment = {
      istifId: istif.id,
      commentText: commentText,
    };
    try {
      const response = await axios.post(
        `${process.env.REACT_APP_BACKEND_URL}/api/comment/add`,
        comment,
        {
          withCredentials: true,
        }
      );
      const updatedIstif = { ...istif };
      updatedIstif.comments.push(response.data);
      setIstif(updatedIstif);
      setCommentText("");
    } catch (error) {
      console.log(error);
    }
  };

  const handleLikeIstif = async () => {
    try {
      const response = await axios.post(
        `${process.env.REACT_APP_BACKEND_URL}/api/istif/like`,
        { likedEntityId: istif.id },
        {
          withCredentials: true,
        }
      );
      setIstif(response.data);
    } catch (error) {
      console.log(error);
    }
  };

  const handleLikeComment = async (commentId) => {
    try {
      const response = await axios.post(
        `${process.env.REACT_APP_BACKEND_URL}/api/comment/like`,
        { likedEntityId: commentId },
        {
          withCredentials: true,
        }
      );
      const updatedIstif = { ...istif };
      const updatedComments = updatedIstif.comments.map((comment) => {
        if (comment.id === commentId) {
          return response.data;
        }
        return comment;
      });
      updatedIstif.comments = updatedComments;
      setIstif(updatedIstif);
    } catch (error) {
      console.log(error);
    }
  };

  const handleDeleteComment = async (commentId) => {
    try {
      await axios.get(
        `${process.env.REACT_APP_BACKEND_URL}/api/comment/delete/${commentId}`,
        {
          withCredentials: true,
        }
      );
      const updatedIstif = {
        ...istif,
        comments: istif.comments.filter((comment) => comment.id !== commentId),
      };
      setIstif(updatedIstif);
    } catch (error) {
      console.log(error);
    }
  };

  if (!istif || currentUserId === null) {
    return <div>Loading...</div>;
  }

  return (
    <div className="all-istifs">
      <h1>Title: {istif.title}</h1>
      <h1>
        Link:{" "}
        <a href={istif.titleLink} target="_blank" rel="noopener noreferrer">
          {istif.titleLink}
        </a>
      </h1>
      <p>
        <b>Istif:</b> {parse(istif.text)}
      </p>
      <p>
        <b>Likes:</b> {istif.likeSize}
      </p>
      <button onClick={handleLikeIstif}>Like!</button>
      <p>
        <b>Labels:</b> {istif.labels.join(", ")}
      </p>
      <b>Written by:</b>{" "}
      <a href={"/user/" + istif.user.id}>{istif.user.username}</a>
      <p>
        <b>Published at:</b> {istif.createdAt}
      </p>
      <p>
        <b>Relevant Date:</b> {istif.istifDate}
      </p>
      <p>
        <b>Comments:</b>
      </p>
      <ul>
        {istif.comments.map((comment) => (
          <li key={comment.id}>
            <b>Comment:</b> {comment.text}
            <p>
              <b>Commented by:</b> {comment.user.username}
              {currentUserId === comment.user.id && (
                <button onClick={() => handleDeleteComment(comment.id)}>
                  Delete
                </button>
              )}
            </p>
            <p>
              <b>Likes:</b> {comment.likes ? comment.likes.length : 0}
              <button onClick={() => handleLikeComment(comment.id)}>
                Like
              </button>
            </p>
          </li>
        ))}
      </ul>
      <form onSubmit={handleCommentSubmit}>
        <label>
          Add Comment:
          <textarea
            value={commentText}
            onChange={(e) => setCommentText(e.target.value)}
          ></textarea>
        </label>
        <button type="submit">Submit</button>
      </form>
    </div>
  );
}

export default IstifDetails;
