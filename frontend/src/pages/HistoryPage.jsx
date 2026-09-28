import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  BsClockHistory,
  BsFileEarmarkPdfFill,
  BsFileEarmarkWordFill,
  BsSearch,
  BsTrash,
  BsEyeFill,
  BsFilter,
  BsArrowRight
} from 'react-icons/bs';
import { mockHistoryData } from '../data/mockData';

const HistoryPage = () => {
  const [historyList, setHistoryList] = useState(mockHistoryData);
  const [searchTerm, setSearchTerm] = useState('');
  const [scoreFilter, setScoreFilter] = useState('all');

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to remove this analysis record?')) {
      setHistoryList((prev) => prev.filter((item) => item.id !== id));
    }
  };

  const filteredHistory = historyList.filter((item) => {
    const matchesSearch =
      item.resumeName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.jobRole.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.company.toLowerCase().includes(searchTerm.toLowerCase());

    if (scoreFilter === 'high') return matchesSearch && item.atsScore >= 80;
    if (scoreFilter === 'mid') return matchesSearch && item.atsScore >= 60 && item.atsScore < 80;
    if (scoreFilter === 'low') return matchesSearch && item.atsScore < 60;
    return matchesSearch;
  });

  return (
    <div className="py-4">
      <div className="container">
        {/* Header */}
        <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4 pb-2 border-bottom">
          <div>
            <h2 className="fw-bold mb-1 d-flex align-items-center gap-2">
              <BsClockHistory className="text-primary" />
              <span>Resume Analysis History</span>
            </h2>
            <p className="text-muted mb-0 small">
              View, inspect, and track previous ATS matching reports stored in your database.
            </p>
          </div>
          <Link to="/dashboard" className="btn btn-primary-custom d-flex align-items-center gap-2">
            <span>Analyze New Resume</span>
            <BsArrowRight />
          </Link>
        </div>

        {/* Filters & Search Card */}
        <div className="custom-card mb-4 p-3">
          <div className="row g-2 align-items-center">
            <div className="col-md-7">
              <div className="input-group">
                <span className="input-group-text bg-white border-end-0">
                  <BsSearch className="text-muted" />
                </span>
                <input
                  type="text"
                  className="form-control border-start-0 shadow-none"
                  placeholder="Search by resume name, job role, or company..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  style={{ fontSize: '0.9rem' }}
                />
              </div>
            </div>

            <div className="col-md-5 d-flex gap-2 justify-content-md-end">
              <div className="input-group" style={{ maxWidth: '220px' }}>
                <span className="input-group-text bg-white">
                  <BsFilter className="text-muted" />
                </span>
                <select
                  className="form-select shadow-none"
                  value={scoreFilter}
                  onChange={(e) => setScoreFilter(e.target.value)}
                  style={{ fontSize: '0.88rem' }}
                >
                  <option value="all">All Scores</option>
                  <option value="high">High Match (&ge; 80%)</option>
                  <option value="mid">Moderate (60% - 79%)</option>
                  <option value="low">Needs Work (&lt; 60%)</option>
                </select>
              </div>

              {historyList.length > 0 && (
                <button
                  onClick={() => {
                    if (window.confirm('Clear all mock history records?')) setHistoryList([]);
                  }}
                  className="btn btn-outline-danger btn-sm px-3"
                >
                  Clear All
                </button>
              )}
            </div>
          </div>
        </div>

        {/* History Table */}
        <div className="custom-card p-0 overflow-hidden">
          {filteredHistory.length === 0 ? (
            <div className="text-center py-5">
              <BsClockHistory size={48} className="text-muted mb-3 opacity-50" />
              <h5 className="fw-bold text-dark mb-1">No analysis records found</h5>
              <p className="text-muted small mb-3">No matching resume analysis fits your current search filter.</p>
              <Link to="/dashboard" className="btn btn-outline-primary btn-sm rounded-pill px-4">
                Run an Analysis
              </Link>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr style={{ fontSize: '0.82rem', color: '#64748b' }}>
                    <th className="ps-4">RESUME FILE</th>
                    <th>TARGET ROLE & COMPANY</th>
                    <th>DATE</th>
                    <th>MATCH RATIO</th>
                    <th>ATS SCORE</th>
                    <th>STATUS</th>
                    <th className="text-end pe-4">ACTIONS</th>
                  </tr>
                </thead>
                <tbody style={{ fontSize: '0.88rem' }}>
                  {filteredHistory.map((item) => (
                    <tr key={item.id}>
                      <td className="ps-4">
                        <div className="d-flex align-items-center gap-2">
                          {item.resumeName.endsWith('.docx') ? (
                            <BsFileEarmarkWordFill className="text-primary fs-5" />
                          ) : (
                            <BsFileEarmarkPdfFill className="text-danger fs-5" />
                          )}
                          <span className="fw-semibold text-dark text-truncate" style={{ maxWidth: '240px' }}>
                            {item.resumeName}
                          </span>
                        </div>
                      </td>
                      <td>
                        <div className="fw-semibold text-dark">{item.jobRole}</div>
                        <small className="text-muted">{item.company}</small>
                      </td>
                      <td className="text-muted">{item.date}</td>
                      <td>
                        <span className="text-success fw-semibold">{item.matchedCount}</span> /{' '}
                        <span className="text-danger fw-semibold">{item.missingCount}</span>
                      </td>
                      <td>
                        <div className="d-flex align-items-center gap-2">
                          <span className="fw-bold text-dark fs-6">{item.atsScore}%</span>
                        </div>
                      </td>
                      <td>
                        <span
                          className={`badge ${
                            item.atsScore >= 80
                              ? 'bg-success-subtle text-success border border-success-subtle'
                              : item.atsScore >= 60
                              ? 'bg-warning-subtle text-warning-emphasis border border-warning-subtle'
                              : 'bg-danger-subtle text-danger border border-danger-subtle'
                          }`}
                        >
                          {item.status}
                        </span>
                      </td>
                      <td className="text-end pe-4">
                        <div className="d-inline-flex gap-2">
                          <Link
                            to="/results"
                            className="btn btn-sm btn-outline-primary d-inline-flex align-items-center gap-1 py-1 px-2"
                            title="View Analysis"
                          >
                            <BsEyeFill size={13} />
                            <span>View</span>
                          </Link>
                          <button
                            onClick={() => handleDelete(item.id)}
                            className="btn btn-sm btn-outline-danger py-1 px-2"
                            title="Delete Record"
                          >
                            <BsTrash size={13} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default HistoryPage;
