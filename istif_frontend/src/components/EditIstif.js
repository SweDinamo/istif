import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import ReactQuill from "react-quill";
import DatePicker from "react-datepicker";
import TextField from "@mui/material/TextField";
import Chip from "@mui/material/Chip";
import { format } from "date-fns";
import { useParams, useNavigate } from "react-router-dom";
import { message } from "antd";
import "react-quill/dist/quill.snow.css";
import "react-datepicker/dist/react-datepicker.css";
import "./css/AddIstif.css";

const EditIstifForm = () => {
  const { id } = useParams();
  const [messageApi] = message.useMessage();
  const [title, setTitle] = useState("");
  const [titleLink, setTitleLink] = useState("");
  const [labels, setLabels] = useState([]);
  const [currentLabel, setCurrentLabel] = useState("");
  const [text, setText] = useState("");
  const [source, setSource] = useState("");
  const [istifDate, setIstifDate] = useState(null);
  const [formerRelevantDate, setFormerRelevantDate] = useState(null);
  const [dateFormat, setDateFormat] = useState(null);
  const [shareFlag, setShareFlag] = useState(0);
  const navigate = useNavigate();

  const fetchIstifData = useCallback(async () => {
    try {
      const response = await axios.get(
        `${process.env.REACT_APP_BACKEND_URL}/api/istif/${id}`,
        {
          withCredentials: true,
        }
      );
      const existingIstif = response.data;

      setTitle(existingIstif.title);
      setLabels(existingIstif.labels);
      setTitleLink(existingIstif.titleLink);
      setText(existingIstif.text);
      setSource(existingIstif.source);
      setIstifDate(null);
      setFormerRelevantDate(existingIstif.istifDate);
      setShareFlag(existingIstif.shareFlag);
    } catch (error) {
      console.error("Error fetching Istif data:", error);
      messageApi.open({
        type: "error",
        content: "Error occurred while fetching the Istif data!",
      });
    }
  }, [id, messageApi]);

  useEffect(() => {
    fetchIstifData();
  }, [id, fetchIstifData]);

  const addLabel = () => {
    if (currentLabel.trim() !== "") {
      setLabels([...labels, currentLabel.trim()]);
      setCurrentLabel("");
    }
  };

  const removeLabel = (labelToRemove) => {
    setLabels(labels.filter((label) => label !== labelToRemove));
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

  const handleLabelKeyDown = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      addLabel();
    }
  };

  const handleEditorChange = (value) => {
    setText(value);
  };

  const handleTitleChange = (value) => {
    setTitle(value);
  };

  const handleSourceChange = (value) => {
    setSource(value);
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

  const handleIstifDateChange = (date) => {
    setIstifDate(date);
  };



  const handleSubmit = async (event) => {
    event.preventDefault();

    const formattedIstifDate = istifDate
      ? format(istifDate, dateFormat.replace(/d/g, "d").replace(/y/g, "y"))
      : null;

    const istifData = {
      id,
      title,
      titleLink,
      labels,
      text,
      source,
      istifDate: formattedIstifDate,
      shareFlag
    };

    try {
      await axios.post(
        `${process.env.REACT_APP_BACKEND_URL}/api/istif/edit/${id}`,
        istifData,
        { withCredentials: true }
      );
      navigate(`/istif/${id}`);
    } catch (error) {
      console.error("Error updating Istif:", error);
    }
  };

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
        Labels (optional) Click enter to add:
        <TextField
          fullWidth
          value={currentLabel}
          onChange={(e) => setCurrentLabel(e.target.value)}
          onKeyDown={handleLabelKeyDown}
          InputProps={{
            endAdornment: (
              <>
                {labels.map((label) => (
                  <Chip
                    key={label}
                    label={label}
                    onDelete={() => removeLabel(label)}
                    style={{ margin: "4px" }}
                  />
                ))}
              </>
            ),
          }}
        />
      </label>
      <label className="add-istif-label">
        <b>Source:</b> (optional)
        <input
          type="text"
          className="add-istif-input"
          value={source}
          onChange={(e) => handleSourceChange(e.target.value)}
        />
      </label>
      <br />
      <label className="add-istif-form">
        Description:
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
      {formerRelevantDate && (
        <p className="add-istif-select">
          <b>Former Relevant Date:</b> {formerRelevantDate}
        </p>
      )}
      <label className="add-istif-label">
        Date Format for Relevant Date:(optional)
        <select
          value={dateFormat}
          onChange={(e) => setDateFormat(e.target.value)}
          className="add-istif-select"
        >
          <option value="">Please Select</option>
          <option value="dd/MM/yyyy">dd/MM/yyyy</option>
          <option value="MM/yyyy">MM/yyyy</option>
          <option value="yyyy">yyyy</option>
        </select>
      </label>
      {dateFormat && (
        <label className="add-istif-label">
          Relevant Date for Istif:
          <DatePicker
            selected={istifDate}
            onChange={handleIstifDateChange}
            dateFormat={dateFormat}
            className="add-istif-datepicker"
            showMonthYearPicker={dateFormat === "MM/yyyy"}
            showYearPicker={dateFormat === "yyyy"}
            showFullMonthYearPicker={dateFormat === "MM/yyyy"}
          />
        </label>
      )}
      <br />
      <br />
      <div className="slider-container">
        <label className={`switch ${shareFlag === 1 ? "public" : "private"}`}>
          <input
            type="checkbox"
            checked={shareFlag === 1} // Checkbox reflects shareFlag value
            onChange={handleToggleChange}
          />
          <span className="slider round"></span>
        </label>
        <p className="toggle-label">
          {shareFlag === 1 ? "Istif will be Public!" : "Istif will be Private!"}
        </p>
      </div>
      <br />
      <button type="submit" className="add-istif-button">
        Update Istif
      </button>
    </form>
  );
};

export default EditIstifForm;
