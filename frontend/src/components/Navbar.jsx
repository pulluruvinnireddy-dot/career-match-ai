import React from 'react';
import { Link, NavLink } from 'react-router-dom';
import { BsBriefcaseFill, BsArrowRight } from 'react-icons/bs';

const Navbar = () => {
  return (
    <nav className="navbar navbar-expand-lg navbar-custom sticky-top">
      <div className="container">
        <Link className="navbar-brand d-flex align-items-center" to="/">
          <div
            className="d-flex align-items-center justify-content-center me-2 text-white rounded-3"
            style={{ width: '36px', height: '36px', background: 'linear-gradient(135deg, #1d4ed8, #2563eb)' }}
          >
            <BsBriefcaseFill size={18} />
          </div>
          <span className="brand-text">CareerMatch</span>
          <span className="brand-badge">AI</span>
        </Link>

        <button
          className="navbar-toggler border-0"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarContent"
          aria-controls="navbarContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarContent">
          <ul className="navbar-nav mx-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link-custom ${isActive ? 'active' : ''}`} to="/">
                Home
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link-custom ${isActive ? 'active' : ''}`} to="/dashboard">
                Dashboard
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link-custom ${isActive ? 'active' : ''}`} to="/results">
                ATS Results
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className={({ isActive }) => `nav-link-custom ${isActive ? 'active' : ''}`} to="/history">
                History
              </NavLink>
            </li>
          </ul>

          <div className="d-flex align-items-center gap-2">
            <Link to="/dashboard" className="btn btn-primary-custom d-flex align-items-center gap-2">
              <span>Get Started</span>
              <BsArrowRight size={15} />
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
