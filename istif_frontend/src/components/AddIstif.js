import React, { useState } from "react";
import axios from "axios";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import "quill-emoji/dist/quill-emoji.css";
import DatePicker from "react-datepicker";
import { format } from "date-fns";
import { useNavigate } from "react-router-dom";
import "react-datepicker/dist/react-datepicker.css";
import "./css/AddIstif.css";

const AddIstifForm = () => {
  const [titleLink, setTitleLink] = useState("");
  const [labels, setLabels] = useState("");
  const [text, setText] = useState("");
  const [title, setTitle] = useState("");
  const [relevantDate, setRelevantDate] = useState(null);
  const [dateFormat, setDateFormat] = useState("dd/MM/yyyy"); // Added state for date format
  const [shareFlag, setShareFlag] = useState(0); // 0 for private, 1 for public
  const navigate = useNavigate();

  const handleEditorChange = (value) => {
    setText(value);
  };

  const handleTitleChange = (value) => {
    setTitle(value);
  };

  const handleTitleLinkChange = (value) => {
    if (
      value &&
      !value.startsWith("http://") &&
      !value.startsWith("https://")
    ) {
      value = "https://" + value;
    }

    if (value && !value.includes("www.")) {
      value = value.replace("https://", "https://www.");
    }

    setTitleLink(value);
  };

  const handleToggleChange = () => {
    setShareFlag((prevFlag) => (prevFlag === 0 ? 1 : 0));
  };

  const handleRelevantDateChange = (date) => {
    setRelevantDate(date);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formattedRelevantDate = relevantDate
      ? format(relevantDate, dateFormat.replace(/d/g, "d").replace(/y/g, "y"))
      : null;

    const istif = {
      titleLink,
      title,
      labels: labels.split(","),
      text,
      relevantDate: formattedRelevantDate,
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
      const newIstifId = response.data.id;
      console.log(response);
      console.log(`New Istif ID: ${newIstifId}`);
      navigate(`/istif/${newIstifId}`);
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
        <b>Title:</b>
        <input
          type="text"
          className="add-istif-input"
          value={title}
          onChange={(e) => handleTitleChange(e.target.value)}
        />
      </label>
      <label className="add-istif-label">
        <b>Link:</b>
        <input
          type="text"
          className="add-istif-input"
          value={titleLink}
          onChange={(e) => handleTitleLinkChange(e.target.value)}
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
      <label className="add-story-label">
        Relevant Date for Istif:
        <DatePicker
          selected={relevantDate}
          onChange={handleRelevantDateChange}
          dateFormat={dateFormat}
          className="add-istif-datepicker"
          showMonthYearPicker={dateFormat === "MM/yyyy"}
          showYearPicker={dateFormat === "yyyy"}
          showFullMonthYearPicker={dateFormat === "MM/yyyy"}
        />
      </label>
      <br />
      <label className="add-istif-label">
        Date Format:
        <select
          value={dateFormat}
          onChange={(e) => setDateFormat(e.target.value)}
          className="add-istif-select"
        >
          <option value="dd/MM/yyyy">dd/MM/yyyy</option>
          <option value="MM/yyyy">MM/yyyy</option>
          <option value="yyyy">yyyy</option>
        </select>
      </label>
      <br />
      <div className="slider-container">
        <label className={`switch ${shareFlag === 1 ? "public" : "private"}`}>
          <input type="checkbox" onChange={handleToggleChange} />
          <span className="slider round"></span>
        </label>
        <p className="toggle-label">
          {shareFlag === 1 ? "Public!" : "Private!"}
        </p>
      </div>
      <br />
      <button type="submit" className="add-istif-button">
        Add Istif
      </button>
    </form>
  );
};

export default AddIstifForm;
