import React, { useState, useCallback } from "react";
import axios from "axios";
import "./css/IstifSearch.css";

const IstifSearch = () => {
  const [searchQuery, setSearchQuery] = useState(null);
  const [searchResults, setSearchResults] = useState([]);
  const [searchDate, setSearchDate] = useState({ type: null, value: null });
  const [secondSearchDate, setSecondSearchDate] = useState({
    type: null,
    value: null,
  });

  const handleSearch = useCallback(async () => {
    try {
      let searchedDate = null;
      let searchedEndDate = null;

      switch (searchDate.type) {
        case "absolute-date":
          searchedDate = searchDate.value;
          searchedEndDate = null;
          break;
        case "interval-date":
          searchedDate = searchDate.value?.searchedDate;
          searchedEndDate = secondSearchDate.value?.searchedDate || null;
          break;
        case "absolute-year":
          searchedDate = `${searchDate.value}-01-01`;
          searchedEndDate = `${searchDate.value}-12-31`;
          break;
        case "interval-year":
          searchedDate = `${searchDate.value.start}-01-01`;
          searchedEndDate = `${secondSearchDate.value.end}-01-01` || null;
          break;
        default:
          break;
      }

      const response = await axios.get(
        `${process.env.REACT_APP_BACKEND_URL}/api/istif/search`,
        {
          params: {
            query: searchQuery,
            startDate: searchedDate,
            endDate: searchedEndDate,
          },
          withCredentials: true,
        }
      );

      setSearchResults(response.data);
    } catch (error) {
      console.log(error);
    }
  }, [searchQuery, searchDate, secondSearchDate]);

  const handleDateTypeChange = (event) => {
    const type = event.target.value;
    setSearchDate({ type, value: null });
    setSecondSearchDate({ type, value: null });
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
              Start Date:
              <input
                type="date"
                value={searchDate.value?.searchedDate || ""}
                onChange={(e) =>
                  setSearchDate({
                    ...searchDate,
                    value: {
                      ...searchDate.value,
                      searchedDate: e.target.value,
                    },
                  })
                }
              />
            </label>
            <label>
              End Date:
              <input
                type="date"
                value={secondSearchDate.value?.searchedDate || ""}
                onChange={(e) =>
                  setSecondSearchDate({
                    ...secondSearchDate,
                    value: {
                      ...secondSearchDate.value,
                      searchedDate: e.target.value,
                    },
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
                value={searchDate.value?.start || ""}
                onChange={(e) =>
                  setSearchDate({
                    ...searchDate,
                    value: { ...searchDate.value, start: e.target.value },
                  })
                }
              />
            </label>
            <label>
              End Year:
              <input
                type="number"
                value={secondSearchDate.value?.end || ""}
                onChange={(e) =>
                  setSecondSearchDate({
                    ...secondSearchDate,
                    value: { ...secondSearchDate.value, end: e.target.value },
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