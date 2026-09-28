import React from 'react';
import { Link } from 'react-router-dom';
import {
  BsShieldCheck,
  BsLightningChargeFill,
  BsCpuFill,
  BsFileEarmarkPdfFill,
  BsArrowRight,
  BsCheckCircleFill,
  BsBarChartFill,
  BsStars
} from 'react-icons/bs';

const LandingPage = () => {
  return (
    <div>
      {/* Hero Section */}
      <section className="hero-section text-center">
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-lg-9">
              <div className="hero-pill">
                <BsStars /> Next-Gen ATS Resume Intelligence
              </div>
              <h1 className="hero-title">
                Make Your Resume <span style={{ color: '#2563eb' }}>Job-Ready</span>
              </h1>
              <p className="hero-subtitle">
                Beat the Applicant Tracking Systems (ATS) with AI-powered resume parsing, keyword gap matching,
                and actionable recruiter insights. Optimize your resume for any role in seconds.
              </p>

              <div className="d-flex flex-wrap justify-content-center gap-3">
                <Link to="/dashboard" className="btn btn-primary-custom d-flex align-items-center gap-2 px-4 py-3 fs-6">
                  <span>Analyze My Resume</span>
                  <BsArrowRight />
                </Link>
                <Link to="/dashboard" className="btn btn-outline-custom px-4 py-3 fs-6">
                  Get Started Free
                </Link>
              </div>

              {/* Trust Badges */}
              <div className="d-flex flex-wrap justify-content-center align-items-center gap-4 mt-5 text-muted" style={{ fontSize: '0.9rem' }}>
                <span className="d-flex align-items-center gap-2">
                  <BsCheckCircleFill className="text-success" /> PDF & DOCX Support
                </span>
                <span className="d-flex align-items-center gap-2">
                  <BsCheckCircleFill className="text-success" /> Algorithmic ATS Scorer
                </span>
                <span className="d-flex align-items-center gap-2">
                  <BsCheckCircleFill className="text-success" /> Zero Paid API Lock-In
                </span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Interactive Mock Preview Banner */}
      <section className="py-4">
        <div className="container">
          <div
            className="p-4 p-md-5 rounded-4 shadow-sm border"
            style={{
              background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
              color: '#ffffff'
            }}
          >
            <div className="row align-items-center gy-4">
              <div className="col-lg-7">
                <span className="badge bg-primary px-3 py-2 rounded-pill mb-3">Live Analysis Engine</span>
                <h2 className="fw-bold mb-3">What ATS software looks for in your resume</h2>
                <p className="text-light opacity-75 mb-4" style={{ lineHeight: '1.7' }}>
                  Enterprise ATS systems filter out up to 75% of qualified resumes before human eyes ever see them.
                  CareerMatch AI extracts key skills, checks frequency, and compares your qualifications against job descriptions.
                </p>
                <div className="row g-3">
                  <div className="col-sm-6">
                    <div className="p-3 rounded-3" style={{ background: 'rgba(255, 255, 255, 0.06)' }}>
                      <div className="text-info fw-bold mb-1">84% Match Score</div>
                      <div className="text-light opacity-75 small">Comprehensive semantic and skill alignment</div>
                    </div>
                  </div>
                  <div className="col-sm-6">
                    <div className="p-3 rounded-3" style={{ background: 'rgba(255, 255, 255, 0.06)' }}>
                      <div className="text-success fw-bold mb-1">Missing Keyword Alerts</div>
                      <div className="text-light opacity-75 small">Identify critical gaps before submitting</div>
                    </div>
                  </div>
                </div>
              </div>
              <div className="col-lg-5 text-center">
                <div className="p-4 rounded-3 bg-white text-dark shadow-sm">
                  <div className="ats-score-badge mb-3" style={{ '--score-pct': 84 }}>
                    <div className="ats-score-inner">
                      <span className="ats-score-number">84</span>
                      <span className="ats-score-label">ATS Score</span>
                    </div>
                  </div>
                  <h6 className="fw-bold mb-1">Strong Match Verified</h6>
                  <p className="text-muted small mb-3">Target: Senior Full Stack & Java Engineer</p>
                  <Link to="/results" className="btn btn-outline-primary btn-sm w-100 rounded-pill">
                    View Demo Results
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Cards Grid */}
      <section className="py-5">
        <div className="container">
          <div className="text-center mb-5">
            <h6 className="text-primary fw-bold text-uppercase" style={{ letterSpacing: '1px' }}>
              Why CareerMatch AI
            </h6>
            <h2 className="fw-bold text-dark">Built for Job Seekers & Tech Talent</h2>
          </div>

          <div className="row g-4">
            <div className="col-md-6 col-lg-3">
              <div className="custom-card h-100">
                <div
                  className="rounded-3 d-flex align-items-center justify-content-center text-primary mb-3"
                  style={{ width: '48px', height: '48px', backgroundColor: '#eff6ff' }}
                >
                  <BsFileEarmarkPdfFill size={24} />
                </div>
                <h5 className="fw-bold mb-2">Resume Parsing</h5>
                <p className="text-muted small mb-0">
                  Instant text extraction from both PDF and DOCX formats without losing structural formatting.
                </p>
              </div>
            </div>

            <div className="col-md-6 col-lg-3">
              <div className="custom-card h-100">
                <div
                  className="rounded-3 d-flex align-items-center justify-content-center text-primary mb-3"
                  style={{ width: '48px', height: '48px', backgroundColor: '#eff6ff' }}
                >
                  <BsBarChartFill size={24} />
                </div>
                <h5 className="fw-bold mb-2">ATS Match Score</h5>
                <p className="text-muted small mb-0">
                  Algorithmic scoring factoring in education, years of experience, technical skills, and keyword presence.
                </p>
              </div>
            </div>

            <div className="col-md-6 col-lg-3">
              <div className="custom-card h-100">
                <div
                  className="rounded-3 d-flex align-items-center justify-content-center text-primary mb-3"
                  style={{ width: '48px', height: '48px', backgroundColor: '#eff6ff' }}
                >
                  <BsCpuFill size={24} />
                </div>
                <h5 className="fw-bold mb-2">AI Resume Coach</h5>
                <p className="text-muted small mb-0">
                  Personalized suggestions for rewording bullet points, adding missing metrics, and enhancing your summary.
                </p>
              </div>
            </div>

            <div className="col-md-6 col-lg-3">
              <div className="custom-card h-100">
                <div
                  className="rounded-3 d-flex align-items-center justify-content-center text-primary mb-3"
                  style={{ width: '48px', height: '48px', backgroundColor: '#eff6ff' }}
                >
                  <BsShieldCheck size={24} />
                </div>
                <h5 className="fw-bold mb-2">Audit History</h5>
                <p className="text-muted small mb-0">
                  Keep track of every resume tailored for specific job positions in a structured historical dashboard.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Call to Action */}
      <section className="py-5 bg-white border-top">
        <div className="container text-center">
          <h2 className="fw-bold mb-3">Ready to optimize your next job application?</h2>
          <p className="text-muted mb-4">
            Upload your resume now to compare your credentials against the job description.
          </p>
          <Link to="/dashboard" className="btn btn-primary-custom px-5 py-3 fs-6">
            Get Started Now
          </Link>
        </div>
      </section>
    </div>
  );
};

export default LandingPage;
