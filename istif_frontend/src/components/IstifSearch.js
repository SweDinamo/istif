import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import "./css/IstifSearch.css";

const IstifSearch = () => {
  const [searchQuery, setSearchQuery] = useState(null);
  const [searchResults, setSearchResults] = useState([]);
  const [searchDate, setSearchDate] = useState({ type: null, value: null });

  const handleSearch = useCallback(async () => {

    try {
      let searchedDate = null;

      switch (searchDate.type) {
        case "absolute-date":
          searchedDate = searchDate.value;
          break;
        case "interval-date":
          searchedDate = searchDate.value.searchedDate;
          break;
        case "absolute-year":
          searchedDate = `${searchDate.value}-01-01`;
          break;
        case "interval-year":
          searchedDate = `${searchDate.value.searchedDate}`;
          break;
        default:
          break;
      }

      const response = await axios.get(
        `${process.env.REACT_APP_BACKEND_URL}/api/istif/search`,
        {
          params: {
            query: searchQuery,
            date: searchedDate,
          },
          withCredentials: true,
        }
      );

      setSearchResults(response.data);
    } catch (error) {
      console.log(error);
    }
  }, [
    searchQuery,
    searchDate
  ]);

  useEffect(() => {
    handleSearch();
  }, [handleSearch, searchDate]);

  const handleDateTypeChange = (event) => {
    const type = event.target.value;
    setSearchDate({ type, value: null });
  };

  return (
    <div className="istif-search">
      <h2>Istif Search</h2>
      <div className="search-form">
        <label>
          Search Query:
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </label>
        <label>
          Date Type:
          <select value={searchDate.type} onChange={handleDateTypeChange}>
            <option value="">Select a Date Type</option>
            <option value="absolute-date">Absolute Date</option>
            <option value="interval-date">Interval Date</option>
            <option value="absolute-year">Absolute Year</option>
            <option value="interval-year">Interval Year</option>
          </select>
        </label>
        {searchDate.type === "absolute-date" && (
          <label>
            Date:
            <input
              type="date"
              value={searchDate.value || ""}
              onChange={(e) =>
                setSearchDate({ ...searchDate, value: e.target.value })
              }
            />
          </label>
        )}
        {searchDate.type === "interval-date" && (
          <>
            <label>
              Date:
              <input
                type="date"
                value={searchDate.value?.searchedDate || ""}
                onChange={(e) =>
                  setSearchDate({
                    ...searchDate,
                    value: { ...searchDate.value, searchedDate: e.target.value },
                  })
                }
              />
            </label>
          </>
        )}
        {searchDate.type === "absolute-year" && (
          <label>
            Year:
            <input
              type="number"
              value={searchDate.value || ""}
              onChange={(e) =>
                setSearchDate({ ...searchDate, value: e.target.value })
              }
            />
          </label>
        )}
        {searchDate.type === "interval-year" && (
          <>
            <label>
              Start Year:
              <input
                type="number"
                value={searchDate.value?.searchedDate || ""}
                onChange={(e) =>
                  setSearchDate({
                    ...searchDate,
                    value: { ...searchDate.value, searchedDate: e.target.value },
                  })
                }
              />
            </label>
            <label>
              End Year:
              <input
                type="number"
                value={searchDate.value?.relevant || ""}
                onChange={(e) =>
                  setSearchDate({
                    ...searchDate,
                    value: { ...searchDate.value, relevant: e.target.value },
                  })
                }
              />
            </label>
          </>
        )}
        <button type="button" onClick={handleSearch}>
          Search
        </button>
      </div>
      <div className="search-results">
        {searchResults.length > 0 && (
          <div>
            <h3>Search Results:</h3>
            <ul>
              {searchResults.map((result) => (
                <li key={result.id}>
                  <h2>
                    <a href={`/istif/${result.id}`}>{result.title}</a>
                  </h2>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default IstifSearch;