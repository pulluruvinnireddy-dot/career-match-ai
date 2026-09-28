import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  BsCloudArrowUpFill,
  BsFileEarmarkPdfFill,
  BsFileEarmarkWordFill,
  BsTrash,
  BsCheckCircleFill,
  BsXCircleFill,
  BsArrowRight,
  BsStars,
  BsBriefcase,
  BsCheck2Circle,
  BsClockHistory,
  BsExclamationTriangleFill
} from 'react-icons/bs';
import { mockDefaultAnalysis, mockHistoryData } from '../data/mockData';

const DashboardPage = () => {
  const navigate = useNavigate();

  // File state
  const [selectedFile, setSelectedFile] = useState({
    name: 'Alex_Morgan_Senior_Software_Engineer.pdf',
    size: '184 KB',
    type: 'application/pdf'
  });
  const [dragActive, setDragActive] = useState(false);
  const [fileError, setFileError] = useState('');

  // Job description state
  const [jobDescription, setJobDescription] = useState(mockDefaultAnalysis.jobDescription);
  const [jobTitle, setJobTitle] = useState('Senior Full Stack & Java Engineer');
  const [company, setCompany] = useState('Apex Global Tech');

  // Loading state
  const [isAnalyzing, setIsAnalyzing] = useState(false);

  // File upload handlers
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    validateAndSetFile(file);
  };

  const validateAndSetFile = (file) => {
    setFileError('');
    if (!file) return;

    const allowedExtensions = ['pdf', 'docx'];
    const fileExtension = file.name.split('.').pop().toLowerCase();

    if (!allowedExtensions.includes(fileExtension)) {
      setFileError('Invalid file type! Please upload a PDF or DOCX file only.');
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      setFileError('File size exceeds 5MB limit. Please upload a smaller resume.');
      return;
    }

    const formattedSize =
      file.size > 1024 * 1024
        ? `${(file.size / (1024 * 1024)).toFixed(1)} MB`
        : `${Math.round(file.size / 1024)} KB`;

    setSelectedFile({
      name: file.name,
      size: formattedSize,
      type: file.type
    });
  };

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      validateAndSetFile(e.dataTransfer.files[0]);
    }
  };

  const handleRemoveFile = () => {
    setSelectedFile(null);
  };

  const handleClearJobDesc = () => {
    setJobDescription('');
  };

  const handleLoadSampleJD = () => {
    setJobTitle('Senior Full Stack & Java Engineer');
    setCompany('Apex Global Tech');
    setJobDescription(mockDefaultAnalysis.jobDescription);
  };

  const handleAnalyze = () => {
    if (!selectedFile) {
      setFileError('Please upload a resume file (PDF or DOCX) to analyze.');
      return;
    }
    if (!jobDescription.trim()) {
      alert('Please paste or enter a job description to compare against.');
      return;
    }

    setIsAnalyzing(true);
    // Simulate ATS analysis parsing delay
    setTimeout(() => {
      setIsAnalyzing(false);
      navigate('/results');
    }, 1000);
  };

  return (
    <div className="py-4">
      <div className="container">
        {/* Header Banner */}
        <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4 pb-2 border-bottom">
          <div>
            <h2 className="fw-bold mb-1">Resume & Job Match Dashboard</h2>
            <p className="text-muted mb-0">
              Upload your candidate resume and target job requirements to generate an instant ATS audit report.
            </p>
          </div>
          <button
            onClick={handleAnalyze}
            disabled={isAnalyzing}
            className="btn btn-primary-custom d-flex align-items-center gap-2 px-4 py-2"
          >
            {isAnalyzing ? (
              <>
                <span className="spinner-border spinner-border-sm" role="status"></span>
                <span>Running ATS Engine...</span>
              </>
            ) : (
              <>
                <BsStars />
                <span>Analyze Resume Now</span>
                <BsArrowRight />
              </>
            )}
          </button>
        </div>

        {/* Quick ATS Preview Stats Row */}
        <div className="row g-3 mb-4">
          <div className="col-md-4">
            <div className="custom-card d-flex align-items-center gap-3">
              <div
                className="rounded-3 d-flex align-items-center justify-content-center text-primary"
                style={{ width: '48px', height: '48px', backgroundColor: '#eff6ff' }}
              >
                <BsCheck2Circle size={24} />
              </div>
              <div>
                <small className="text-muted text-uppercase fw-bold" style={{ fontSize: '0.72rem' }}>
                  Latest ATS Match
                </small>
                <h4 className="fw-bold mb-0 text-dark">
                  84 <span className="fs-6 text-muted fw-normal">/ 100</span>
                </h4>
              </div>
            </div>
          </div>

          <div className="col-md-4">
            <div className="custom-card d-flex align-items-center gap-3">
              <div
                className="rounded-3 d-flex align-items-center justify-content-center text-success"
                style={{ width: '48px', height: '48px', backgroundColor: '#ecfdf5' }}
              >
                <BsCheckCircleFill size={22} />
              </div>
              <div>
                <small className="text-muted text-uppercase fw-bold" style={{ fontSize: '0.72rem' }}>
                  Top Matched Skills
                </small>
                <h6 className="fw-bold mb-0 text-dark">Java, Spring Boot, MySQL, REST</h6>
              </div>
            </div>
          </div>

          <div className="col-md-4">
            <div className="custom-card d-flex align-items-center gap-3">
              <div
                className="rounded-3 d-flex align-items-center justify-content-center text-danger"
                style={{ width: '48px', height: '48px', backgroundColor: '#fef2f2' }}
              >
                <BsExclamationTriangleFill size={20} />
              </div>
              <div>
                <small className="text-muted text-uppercase fw-bold" style={{ fontSize: '0.72rem' }}>
                  Missing High-Impact Skills
                </small>
                <h6 className="fw-bold mb-0 text-dark">Kubernetes, Kafka, Redis, AWS</h6>
              </div>
            </div>
          </div>
        </div>

        {/* Main Work Area: Upload + Job Description */}
        <div className="row g-4">
          {/* Resume Upload Card */}
          <div className="col-lg-5">
            <div className="custom-card h-100 d-flex flex-direction-column">
              <div className="card-header-styled">
                <h5 className="card-title-styled">
                  <BsBriefcase className="text-primary" />
                  <span>Resume Upload</span>
                </h5>
                <span className="badge bg-light text-dark border">PDF or DOCX</span>
              </div>

              {/* Upload Drop Zone */}
              {!selectedFile ? (
                <div
                  className={`upload-dropzone ${dragActive ? 'drag-active' : ''}`}
                  onDragEnter={handleDrag}
                  onDragLeave={handleDrag}
                  onDragOver={handleDrag}
                  onDrop={handleDrop}
                  onClick={() => document.getElementById('resumeFileInput').click()}
                >
                  <BsCloudArrowUpFill className="upload-icon" />
                  <h6 className="fw-bold text-dark mb-1">Click to browse or drag and drop</h6>
                  <p className="text-muted small mb-3">Accepts PDF, DOCX (Max file size 5MB)</p>
                  <button type="button" className="btn btn-sm btn-outline-primary rounded-pill px-3">
                    Choose Resume File
                  </button>
                  <input
                    id="resumeFileInput"
                    type="file"
                    className="d-none"
                    accept=".pdf,.docx,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    onChange={handleFileChange}
                  />
                </div>
              ) : (
                <div className="border rounded-3 p-3 bg-light">
                  <div className="d-flex align-items-center justify-content-between mb-2">
                    <div className="d-flex align-items-center gap-3">
                      <div
                        className="rounded-2 d-flex align-items-center justify-content-center text-white"
                        style={{
                          width: '42px',
                          height: '42px',
                          backgroundColor: selectedFile.name.endsWith('.docx') ? '#2563eb' : '#dc2626'
                        }}
                      >
                        {selectedFile.name.endsWith('.docx') ? (
                          <BsFileEarmarkWordFill size={22} />
                        ) : (
                          <BsFileEarmarkPdfFill size={22} />
                        )}
                      </div>
                      <div>
                        <h6 className="fw-bold text-dark mb-0 text-truncate" style={{ maxWidth: '200px' }}>
                          {selectedFile.name}
                        </h6>
                        <small className="text-muted">{selectedFile.size}</small>
                      </div>
                    </div>
                    <button
                      onClick={handleRemoveFile}
                      className="btn btn-sm text-danger"
                      title="Remove File"
                      style={{ background: 'transparent', border: 'none' }}
                    >
                      <BsTrash size={18} />
                    </button>
                  </div>

                  <div className="d-flex align-items-center gap-2 text-success small mt-2">
                    <BsCheckCircleFill /> Ready for ATS textual extraction
                  </div>
                </div>
              )}

              {/* Error Message */}
              {fileError && (
                <div className="alert alert-danger py-2 px-3 mt-3 small d-flex align-items-center gap-2">
                  <BsXCircleFill /> {fileError}
                </div>
              )}

              <div className="mt-4 pt-3 border-top">
                <h6 className="fw-bold small text-muted text-uppercase mb-2">ATS Formatting Guidelines</h6>
                <ul className="text-muted small ps-3 mb-0" style={{ lineHeight: '1.6' }}>
                  <li>Standard single or clean two-column layout.</li>
                  <li>Clear section headers (Experience, Skills, Education).</li>
                  <li>Avoid embedding vital text inside non-selectable images.</li>
                </ul>
              </div>
            </div>
          </div>

          {/* Job Description Card */}
          <div className="col-lg-7">
            <div className="custom-card h-100">
              <div className="card-header-styled">
                <h5 className="card-title-styled">
                  <span>Target Job Description</span>
                </h5>
                <div className="d-flex gap-2">
                  <button
                    onClick={handleLoadSampleJD}
                    className="btn btn-sm btn-outline-secondary py-1 px-2"
                    style={{ fontSize: '0.8rem' }}
                  >
                    Load Sample JD
                  </button>
                  <button
                    onClick={handleClearJobDesc}
                    className="btn btn-sm btn-outline-danger py-1 px-2"
                    style={{ fontSize: '0.8rem' }}
                    disabled={!jobDescription}
                  >
                    Clear
                  </button>
                </div>
              </div>

              {/* Optional Job Title & Company */}
              <div className="row g-2 mb-3">
                <div className="col-md-7">
                  <label className="form-label small fw-semibold text-muted mb-1">Target Job Title</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    placeholder="e.g. Senior Java Developer"
                    value={jobTitle}
                    onChange={(e) => setJobTitle(e.target.value)}
                  />
                </div>
                <div className="col-md-5">
                  <label className="form-label small fw-semibold text-muted mb-1">Company (Optional)</label>
                  <input
                    type="text"
                    className="form-control form-control-sm"
                    placeholder="e.g. Google / Microsoft"
                    value={company}
                    onChange={(e) => setCompany(e.target.value)}
                  />
                </div>
              </div>

              {/* Main JD Textarea */}
              <div className="mb-2">
                <textarea
                  className="form-control shadow-none"
                  rows={10}
                  placeholder="Paste the job description here..."
                  value={jobDescription}
                  onChange={(e) => setJobDescription(e.target.value)}
                  style={{ fontSize: '0.88rem', resize: 'vertical' }}
                />
              </div>

              <div className="d-flex justify-content-between align-items-center text-muted small">
                <span>{jobDescription.length} characters</span>
                <span>Minimum recommended: 150 characters</span>
              </div>
            </div>
          </div>
        </div>

        {/* Recent Analysis Section */}
        <div className="mt-5">
          <div className="d-flex align-items-center justify-content-between mb-3">
            <h5 className="fw-bold mb-0 d-flex align-items-center gap-2">
              <BsClockHistory className="text-primary" />
              <span>Recent Analyses</span>
            </h5>
            <Link to="/history" className="text-primary text-decoration-none small fw-semibold">
              View All History &rarr;
            </Link>
          </div>

          <div className="table-responsive bg-white rounded-3 border">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light">
                <tr style={{ fontSize: '0.82rem', color: '#64748b' }}>
                  <th>RESUME FILE</th>
                  <th>JOB ROLE</th>
                  <th>DATE</th>
                  <th>ATS SCORE</th>
                  <th>STATUS</th>
                  <th className="text-end">ACTION</th>
                </tr>
              </thead>
              <tbody style={{ fontSize: '0.88rem' }}>
                {mockHistoryData.slice(0, 3).map((item) => (
                  <tr key={item.id}>
                    <td className="fw-semibold text-dark">
                      <BsFileEarmarkPdfFill className="text-danger me-2" />
                      {item.resumeName}
                    </td>
                    <td>{item.jobRole}</td>
                    <td className="text-muted">{item.date}</td>
                    <td>
                      <span className="fw-bold text-primary">{item.atsScore}%</span>
                    </td>
                    <td>
                      <span
                        className={`badge ${
                          item.atsScore >= 80
                            ? 'bg-success'
                            : item.atsScore >= 60
                            ? 'bg-warning text-dark'
                            : 'bg-danger'
                        }`}
                      >
                        {item.status}
                      </span>
                    </td>
                    <td className="text-end">
                      <Link to="/results" className="btn btn-sm btn-outline-primary py-1 px-3 rounded-pill">
                        View
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
