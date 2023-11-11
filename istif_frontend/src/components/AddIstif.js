import React, { useState } from "react";
import axios from "axios";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import "quill-emoji/dist/quill-emoji.css";
import "react-datetime-picker/dist/DateTimePicker.css";
import "./css/AddIstif.css";

const AddIstifForm = () => {
  const [title, setTitle] = useState("");
  const [labels, setLabels] = useState("");
  const [text, setText] = useState("");
  const [shareFlag, setShareFlag] = useState(0); // 0 for private, 1 for public

  const handleEditorChange = (value) => {
    setText(value);
  };

  const handleToggleChange = () => {
    setShareFlag((prevFlag) => (prevFlag === 0 ? 1 : 0)); // Toggle between 0 and 1
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const istif = {
      title,
      labels: labels.split(","),
      text,
      shareFlag,
    };

    try {
      const response = await axios.post(
        `${process.env.REACT_APP_BACKEND_URL}/api/istif/add`,
        istif,
        {
          withCredentials: true,
        }
      );
      console.log(response);
      window.location.href = `${process.env.REACT_APP_FRONTEND_URL}/user/my-profile`;
    } catch (error) {
      console.log(error);
    }
  };

  const modules = {
    toolbar: [
      [{ header: [1, 2, false] }],
      ["bold", "italic", "underline", "strike", "blockquote"],
      [{ list: "ordered" }, { list: "bullet" }],
      ["link", "image"],
    ],
  };

  const formats = [
    "header",
    "bold",
    "italic",
    "underline",
    "strike",
    "blockquote",
    "list",
    "bullet",
    "indent",
    "link",
    "image",
  ];

  return (
    <form className="add-istif-form" onSubmit={handleSubmit}>
      <label className="add-istif-label">
        Link:
        <input
          type="text"
          className="add-istif-input"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
      </label>
      <br />
      <label className="add-istif-label">
        Labels:(comma separated)
        <input
          type="text"
          className="add-istif-input"
          value={labels}
          onChange={(e) => setLabels(e.target.value)}
        />
      </label>
      <br />
      <label className="add-istif-label">
        Kept Notes:
        <ReactQuill
          value={text}
          onChange={handleEditorChange}
          modules={modules}
          formats={formats}
          className="add-istif-editor"
        />
      </label>
      <br />
      <br />
      <br />
      <div className="slider-container">
        <label className={`switch ${shareFlag === 1 ? "public" : "private"}`}>
          <input type="checkbox" onChange={handleToggleChange} />
          <span className="slider round"></span>
        </label>
        <p className="toggle-label">{shareFlag === 1 ? "Public!" : "Private!"}</p>
      </div>
      <br />
      <button type="submit" className="add-istif-button">
        Add Istif
      </button>
    </form>
  );
};

export default AddIstifForm;