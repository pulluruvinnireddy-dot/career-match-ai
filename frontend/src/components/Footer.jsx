import React from 'react';
import { Link } from 'react-router-dom';
import { BsBriefcaseFill, BsShieldCheck, BsCpu, BsFileEarmarkCheck } from 'react-icons/bs';

const Footer = () => {
  return (
    <footer className="footer-custom mt-auto">
      <div className="container">
        <div className="row gy-4 align-items-center">
          <div className="col-md-5">
            <div className="d-flex align-items-center mb-2">
              <div
                className="d-flex align-items-center justify-content-center me-2 text-white rounded-3"
                style={{ width: '28px', height: '28px', background: 'linear-gradient(135deg, #1d4ed8, #2563eb)' }}
              >
                <BsBriefcaseFill size={14} />
              </div>
              <span className="fw-bold text-dark fs-5">CareerMatch</span>
              <span className="brand-badge">AI</span>
            </div>
            <p className="mb-0 text-muted" style={{ fontSize: '0.9rem', maxWidth: '380px' }}>
              Advanced ATS resume analytics & job matching assistant. Built for candidates aiming to conquer enterprise applicant tracking filters.
            </p>
          </div>

          <div className="col-md-3">
            <h6 className="text-dark fw-bold mb-2">Key Capabilities</h6>
            <ul className="list-unstyled mb-0" style={{ fontSize: '0.88rem', lineHeight: '1.8' }}>
              <li><BsFileEarmarkCheck className="text-primary me-2" /> PDF & DOCX Parsing</li>
              <li><BsCpu className="text-primary me-2" /> Algorithmic ATS Match</li>
              <li><BsShieldCheck className="text-primary me-2" /> Recruiter Keyword Coach</li>
            </ul>
          </div>

          <div className="col-md-4 text-md-end">
            <div className="d-flex justify-content-md-end gap-3 mb-2">
              <Link to="/" className="text-decoration-none text-muted">Home</Link>
              <Link to="/dashboard" className="text-decoration-none text-muted">Dashboard</Link>
              <Link to="/results" className="text-decoration-none text-muted">Results</Link>
              <Link to="/history" className="text-decoration-none text-muted">History</Link>
            </div>
            <p className="mb-0 text-muted" style={{ fontSize: '0.82rem' }}>
              &copy; {new Date().getFullYear()} CareerMatch AI. Modern ATS Optimization Platform.
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
