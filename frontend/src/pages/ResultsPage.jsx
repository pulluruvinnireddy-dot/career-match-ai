import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  BsCheckCircleFill,
  BsXCircleFill,
  BsExclamationCircleFill,
  BsArrowLeft,
  BsPrinter,
  BsLightbulbFill,
  BsSpeedometer2,
  BsMortarboardFill,
  BsBriefcaseFill,
  BsCodeSlash,
  BsSearch,
  BsStars,
  BsShieldCheck
} from 'react-icons/bs';
import { mockDefaultAnalysis } from '../data/mockData';

const ResultsPage = () => {
  const analysis = mockDefaultAnalysis;
  const [activeTab, setActiveTab] = useState('overview'); // 'overview' | 'comparison' | 'coach'

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="py-4">
      <div className="container">
        {/* Navigation & Header */}
        <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4 pb-3 border-bottom">
          <div>
            <Link to="/dashboard" className="text-decoration-none text-muted small d-inline-flex align-items-center gap-1 mb-2">
              <BsArrowLeft /> Back to Dashboard
            </Link>
            <h2 className="fw-bold mb-1 d-flex align-items-center gap-2">
              <span>ATS Match & Diagnostic Report</span>
              <span className="badge bg-primary fs-6 fw-semibold">Audit Complete</span>
            </h2>
            <p className="text-muted mb-0 small">
              Analyzed: <strong className="text-dark">{analysis.resumeName}</strong> against{' '}
              <strong className="text-dark">{analysis.jobTitle}</strong> ({analysis.company})
            </p>
          </div>

          <div className="d-flex gap-2">
            <button onClick={handlePrint} className="btn btn-outline-custom d-flex align-items-center gap-2 py-2">
              <BsPrinter /> Print / Export
            </button>
            <Link to="/dashboard" className="btn btn-primary-custom py-2">
              Analyze New Role
            </Link>
          </div>
        </div>

        {/* Top Summary Banner: ATS Score + Core Match Dimensions */}
        <div className="row g-4 mb-4">
          {/* Main ATS Score Card */}
          <div className="col-lg-4">
            <div className="custom-card text-center h-100 d-flex flex-column justify-content-center p-4">
              <span className="badge bg-light text-primary border align-self-center px-3 py-1 rounded-pill mb-3">
                Overall Compatibility
              </span>

              <div className="ats-score-badge mb-3" style={{ '--score-pct': analysis.atsScore }}>
                <div className="ats-score-inner">
                  <span className="ats-score-number">{analysis.atsScore}</span>
                  <span className="ats-score-label">out of 100</span>
                </div>
              </div>

              <h5 className="fw-bold mb-1 text-dark">Strong ATS Alignment</h5>
              <p className="text-muted small mb-3">
                Your resume meets the threshold for high-volume corporate screening filters.
              </p>

              <div className="d-flex justify-content-around border-top pt-3 text-start">
                <div>
                  <small className="text-muted d-block">Matched Skills</small>
                  <strong className="text-success">{analysis.matchedSkills.length} Verified</strong>
                </div>
                <div className="border-start ps-3">
                  <small className="text-muted d-block">Skill Gaps</small>
                  <strong className="text-danger">{analysis.missingSkills.length} Identified</strong>
                </div>
              </div>
            </div>
          </div>

          {/* Breakdown Dimensions: Education, Experience, Tech Skills */}
          <div className="col-lg-8">
            <div className="custom-card h-100">
              <div className="card-header-styled">
                <h5 className="card-title-styled">
                  <BsSpeedometer2 className="text-primary" />
                  <span>Category Match Breakdown</span>
                </h5>
                <span className="badge bg-light text-muted border">Multi-Factor Weighting</span>
              </div>

              <div className="mb-4">
                <div className="d-flex justify-content-between align-items-center mb-1">
                  <span className="fw-semibold text-dark d-flex align-items-center gap-2">
                    <BsCodeSlash className="text-primary" /> Technical Skills Match
                  </span>
                  <span className="fw-bold text-primary">{analysis.technicalSkillsMatch}%</span>
                </div>
                <div className="progress" style={{ height: '9px', borderRadius: '6px' }}>
                  <div
                    className="progress-bar bg-primary"
                    role="progressbar"
                    style={{ width: `${analysis.technicalSkillsMatch}%` }}
                    aria-valuenow={analysis.technicalSkillsMatch}
                    aria-valuemin="0"
                    aria-valuemax="100"
                  ></div>
                </div>
                <small className="text-muted">High density of core Java, Spring Boot, MySQL, and Docker proficiencies.</small>
              </div>

              <div className="mb-4">
                <div className="d-flex justify-content-between align-items-center mb-1">
                  <span className="fw-semibold text-dark d-flex align-items-center gap-2">
                    <BsBriefcaseFill className="text-primary" /> Experience Match
                  </span>
                  <span className="fw-bold text-success">{analysis.experienceMatch}%</span>
                </div>
                <div className="progress" style={{ height: '9px', borderRadius: '6px' }}>
                  <div
                    className="progress-bar bg-success"
                    role="progressbar"
                    style={{ width: `${analysis.experienceMatch}%` }}
                    aria-valuenow={analysis.experienceMatch}
                    aria-valuemin="0"
                    aria-valuemax="100"
                  ></div>
                </div>
                <small className="text-muted">{analysis.experienceDetails}</small>
              </div>

              <div>
                <div className="d-flex justify-content-between align-items-center mb-1">
                  <span className="fw-semibold text-dark d-flex align-items-center gap-2">
                    <BsMortarboardFill className="text-primary" /> Education Match
                  </span>
                  <span className="fw-bold text-info">{analysis.educationMatch}%</span>
                </div>
                <div className="progress" style={{ height: '9px', borderRadius: '6px' }}>
                  <div
                    className="progress-bar bg-info"
                    role="progressbar"
                    style={{ width: `${analysis.educationMatch}%` }}
                    aria-valuenow={analysis.educationMatch}
                    aria-valuemin="0"
                    aria-valuemax="100"
                  ></div>
                </div>
                <small className="text-muted">{analysis.educationDetails}</small>
              </div>
            </div>
          </div>
        </div>

        {/* Navigation Tabs for In-Depth Views */}
        <ul className="nav nav-pills mb-4 p-1 bg-white rounded-3 border d-inline-flex shadow-sm">
          <li className="nav-item">
            <button
              className={`nav-link fw-semibold px-4 ${activeTab === 'overview' ? 'active' : 'text-dark'}`}
              onClick={() => setActiveTab('overview')}
            >
              Skills & Keywords
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link fw-semibold px-4 ${activeTab === 'comparison' ? 'active' : 'text-dark'}`}
              onClick={() => setActiveTab('comparison')}
            >
              Resume Comparison
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link fw-semibold px-4 ${activeTab === 'coach' ? 'active' : 'text-dark'}`}
              onClick={() => setActiveTab('coach')}
            >
              AI Resume Coach
            </button>
          </li>
        </ul>

        {/* TAB 1: Skills & Keywords */}
        {activeTab === 'overview' && (
          <div className="row g-4">
            {/* Matched Skills */}
            <div className="col-md-6">
              <div className="custom-card h-100">
                <div className="card-header-styled">
                  <h5 className="card-title-styled text-success">
                    <BsCheckCircleFill />
                    <span>Matching Skills ({analysis.matchedSkills.length})</span>
                  </h5>
                  <span className="badge bg-success-subtle text-success border border-success-subtle">
                    Found in Resume
                  </span>
                </div>
                <p className="text-muted small mb-3">
                  These verified skills match requirements extracted from the target job posting:
                </p>
                <div className="d-flex flex-wrap">
                  {analysis.matchedSkills.map((skill, idx) => (
                    <span key={idx} className="skill-badge skill-badge-matched">
                      <BsCheckCircleFill className="text-success" size={13} /> {skill}
                    </span>
                  ))}
                </div>
              </div>
            </div>

            {/* Missing Skills */}
            <div className="col-md-6">
              <div className="custom-card h-100">
                <div className="card-header-styled">
                  <h5 className="card-title-styled text-danger">
                    <BsXCircleFill />
                    <span>Missing Skills ({analysis.missingSkills.length})</span>
                  </h5>
                  <span className="badge bg-danger-subtle text-danger border border-danger-subtle">
                    Recruiter Keyword Gap
                  </span>
                </div>
                <p className="text-muted small mb-3">
                  Adding experience or coursework for these skills will raise your match rate:
                </p>
                <div className="d-flex flex-wrap">
                  {analysis.missingSkills.map((skill, idx) => (
                    <span key={idx} className="skill-badge skill-badge-missing">
                      <BsXCircleFill className="text-danger" size={13} /> {skill}
                    </span>
                  ))}
                </div>
              </div>
            </div>

            {/* Keywords Analysis */}
            <div className="col-12">
              <div className="custom-card">
                <div className="card-header-styled">
                  <h5 className="card-title-styled">
                    <BsSearch className="text-primary" />
                    <span>Keyword Frequency & ATS Extraction</span>
                  </h5>
                  <span className="badge bg-light text-muted border">Keyword Match: {analysis.keywordMatchPercentage}%</span>
                </div>
                <div className="row g-4">
                  <div className="col-md-6">
                    <h6 className="fw-bold small text-muted text-uppercase mb-2">Keywords Found in Resume</h6>
                    <div className="d-flex flex-wrap">
                      {analysis.keywordsFound.map((kw, i) => (
                        <span key={i} className="skill-badge skill-badge-matched">
                          {kw}
                        </span>
                      ))}
                    </div>
                  </div>
                  <div className="col-md-6">
                    <h6 className="fw-bold small text-muted text-uppercase mb-2">High-Priority Keywords Missing</h6>
                    <div className="d-flex flex-wrap">
                      {analysis.keywordsMissing.map((kw, i) => (
                        <span key={i} className="skill-badge skill-badge-missing">
                          {kw}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* TAB 2: Resume Comparison Section */}
        {activeTab === 'comparison' && (
          <div className="row g-4">
            <div className="col-md-6">
              <div className="custom-card h-100">
                <div className="card-header-styled">
                  <h5 className="card-title-styled">
                    <BsBriefcaseFill className="text-primary" />
                    <span>Resume Keywords Detected</span>
                  </h5>
                  <span className="badge bg-light text-dark border">
                    {analysis.resumeKeywords.length} Detected
                  </span>
                </div>
                <p className="text-muted small mb-3">
                  All distinct technical terms, tools, and domain keywords extracted from your uploaded resume:
                </p>
                <div className="d-flex flex-wrap">
                  {analysis.resumeKeywords.map((kw, idx) => (
                    <span
                      key={idx}
                      className={`skill-badge ${
                        analysis.jobKeywords.includes(kw) ? 'skill-badge-matched' : 'skill-badge-neutral'
                      }`}
                    >
                      {kw}
                    </span>
                  ))}
                </div>
              </div>
            </div>

            <div className="col-md-6">
              <div className="custom-card h-100">
                <div className="card-header-styled">
                  <h5 className="card-title-styled">
                    <BsSearch className="text-primary" />
                    <span>Job Description Target Keywords</span>
                  </h5>
                  <span className="badge bg-light text-dark border">
                    {analysis.jobKeywords.length} Required
                  </span>
                </div>
                <p className="text-muted small mb-3">
                  Keywords and competencies required by the hiring company:
                </p>
                <div className="d-flex flex-wrap">
                  {analysis.jobKeywords.map((kw, idx) => (
                    <span
                      key={idx}
                      className={`skill-badge ${
                        analysis.resumeKeywords.includes(kw) ? 'skill-badge-matched' : 'skill-badge-missing'
                      }`}
                    >
                      {kw}
                    </span>
                  ))}
                </div>
              </div>
            </div>

            <div className="col-12">
              <div className="custom-card">
                <h5 className="fw-bold mb-3">Detailed Keyword Alignment Matrix</h5>
                <div className="table-responsive">
                  <table className="table table-bordered align-middle mb-0" style={{ fontSize: '0.88rem' }}>
                    <thead className="table-light">
                      <tr>
                        <th>Job Requirement Keyword</th>
                        <th>Present In Resume?</th>
                        <th>ATS Category</th>
                        <th>Recruiter Priority</th>
                      </tr>
                    </thead>
                    <tbody>
                      {analysis.jobKeywords.map((kw, index) => {
                        const isMatched = analysis.resumeKeywords.includes(kw);
                        return (
                          <tr key={index}>
                            <td className="fw-semibold">{kw}</td>
                            <td>
                              {isMatched ? (
                                <span className="text-success fw-bold d-inline-flex align-items-center gap-1">
                                  <BsCheckCircleFill /> Matched
                                </span>
                              ) : (
                                <span className="text-danger fw-bold d-inline-flex align-items-center gap-1">
                                  <BsXCircleFill /> Missing
                                </span>
                              )}
                            </td>
                            <td>Technical Expertise</td>
                            <td>
                              <span
                                className={`badge ${
                                  ['Kubernetes', 'AWS', 'Java', 'Spring Boot'].includes(kw)
                                    ? 'bg-danger-subtle text-danger'
                                    : 'bg-secondary-subtle text-secondary'
                                }`}
                              >
                                {['Kubernetes', 'AWS', 'Java', 'Spring Boot'].includes(kw) ? 'High' : 'Normal'}
                              </span>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* TAB 3: AI Resume Coach */}
        {activeTab === 'coach' && (
          <div className="row g-4">
            {/* Skills to Improve */}
            <div className="col-md-6">
              <div className="custom-card h-100">
                <div className="card-header-styled">
                  <h5 className="card-title-styled text-primary">
                    <BsLightbulbFill />
                    <span>Skills to Improve</span>
                  </h5>
                </div>
                <ul className="list-unstyled mb-0" style={{ lineHeight: '1.7', fontSize: '0.9rem' }}>
                  {analysis.aiCoachSuggestions.skillsToImprove.map((item, idx) => (
                    <li key={idx} className="d-flex align-items-start gap-2 mb-3">
                      <BsCheckCircleFill className="text-primary mt-1 flex-shrink-0" />
                      <span>{item}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>

            {/* Keywords to Add */}
            <div className="col-md-6">
              <div className="custom-card h-100">
                <div className="card-header-styled">
                  <h5 className="card-title-styled text-primary">
                    <BsStars />
                    <span>Keywords to Add</span>
                  </h5>
                </div>
                <p className="text-muted small mb-3">
                  Inject these exact phrases into your skills or projects section to improve parsing accuracy:
                </p>
                <div className="d-flex flex-wrap">
                  {analysis.aiCoachSuggestions.keywordsToAdd.map((kw, idx) => (
                    <span key={idx} className="skill-badge skill-badge-neutral border-primary text-primary">
                      + {kw}
                    </span>
                  ))}
                </div>
              </div>
            </div>

            {/* Summary Suggestions */}
            <div className="col-md-6">
              <div className="custom-card h-100">
                <div className="card-header-styled">
                  <h5 className="card-title-styled text-primary">
                    <BsShieldCheck />
                    <span>Professional Summary Enhancements</span>
                  </h5>
                </div>
                <div className="d-flex flex-column gap-3">
                  {analysis.aiCoachSuggestions.summarySuggestions.map((item, idx) => (
                    <div key={idx} className="p-3 rounded-3 bg-light border-start border-4 border-primary small">
                      {item}
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Project Description Suggestions */}
            <div className="col-md-6">
              <div className="custom-card h-100">
                <div className="card-header-styled">
                  <h5 className="card-title-styled text-primary">
                    <BsCodeSlash />
                    <span>Project Description Suggestions</span>
                  </h5>
                </div>
                <div className="d-flex flex-column gap-3">
                  {analysis.aiCoachSuggestions.projectSuggestions.map((item, idx) => (
                    <div key={idx} className="p-3 rounded-3 bg-light border-start border-4 border-success small">
                      {item}
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ResultsPage;
