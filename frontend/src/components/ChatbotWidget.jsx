import React, { useState, useRef, useEffect } from 'react';
import {
  BsChatDotsFill,
  BsX,
  BsSendFill,
  BsRobot,
  BsPersonFill,
  BsLightbulbFill,
  BsArrowCounterclockwise
} from 'react-icons/bs';
import { sampleChatQuestions } from '../data/mockData';

const initialMessages = [
  {
    id: 1,
    sender: 'bot',
    text: "Hello! I'm your CareerMatch AI Coach. Ask me how to improve your resume, analyze missing skills, or match recruiter expectations."
  }
];

const mockAnswers = {
  "how can i improve my resume":
    "Add relevant technical skills, measurable project achievements (e.g. 'reduced latency by 35%'), and precise keywords directly matching the job description.",
  "what skills am i missing":
    "Based on the analyzed role, your high-priority missing skills are: Kubernetes (K8s), AWS Cloud Infrastructure, Apache Kafka, and Redis caching.",
  "is my resume suitable for this job":
    "Yes! Your resume scores 84/100, which puts you in the top 15% of applicant pool. Addressing 2–3 missing keywords can easily push your match above 90%.",
  "what should i learn for this role":
    "Prioritize container orchestration (Kubernetes), followed by distributed event streaming (Apache Kafka) and system design optimization."
};

const ChatbotWidget = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState(initialMessages);
  const [inputValue, setInputValue] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
    }
  }, [messages, isOpen]);

  const handleSendMessage = (textToSend) => {
    const text = (textToSend || inputValue).trim();
    if (!text) return;

    const userMessage = {
      id: Date.now(),
      sender: 'user',
      text
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputValue('');
    setIsTyping(true);

    // Realistic bot response simulation
    setTimeout(() => {
      const lower = text.toLowerCase();
      let botResponse =
        "To maximize your ATS match, focus on aligning your job titles with the target role, adding quantified impact bullets, and verifying all required technical proficiencies.";

      for (const [key, answer] of Object.entries(mockAnswers)) {
        if (lower.includes(key) || key.includes(lower)) {
          botResponse = answer;
          break;
        }
      }

      setMessages((prev) => [
        ...prev,
        {
          id: Date.now() + 1,
          sender: 'bot',
          text: botResponse
        }
      ]);
      setIsTyping(false);
    }, 600);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const handleResetChat = () => {
    setMessages(initialMessages);
  };

  return (
    <>
      {/* Floating Action Button */}
      <button
        className="chatbot-fab"
        onClick={() => setIsOpen(!isOpen)}
        aria-label="Toggle AI Coach Chatbot"
        title="CareerMatch AI Assistant"
      >
        {isOpen ? <BsX size={32} /> : <BsChatDotsFill size={26} />}
      </button>

      {/* Chatbot Window Panel */}
      {isOpen && (
        <div className="chatbot-panel animate__animated animate__fadeInUp">
          {/* Header */}
          <div className="chatbot-header">
            <div className="d-flex align-items-center gap-2">
              <div
                className="d-flex align-items-center justify-content-center bg-primary text-white rounded-circle"
                style={{ width: '32px', height: '32px' }}
              >
                <BsRobot size={18} />
              </div>
              <div>
                <h6 className="mb-0 fw-bold" style={{ fontSize: '0.95rem' }}>AI Career Coach</h6>
                <small className="text-light opacity-75" style={{ fontSize: '0.75rem' }}>
                  Online • ATS Optimization
                </small>
              </div>
            </div>
            <div className="d-flex align-items-center gap-1">
              <button
                className="btn btn-sm text-light p-1"
                onClick={handleResetChat}
                title="Reset Conversation"
                style={{ background: 'transparent', border: 'none' }}
              >
                <BsArrowCounterclockwise size={16} />
              </button>
              <button
                className="btn btn-sm text-light p-1"
                onClick={() => setIsOpen(false)}
                title="Close Chat"
                style={{ background: 'transparent', border: 'none' }}
              >
                <BsX size={24} />
              </button>
            </div>
          </div>

          {/* Quick Prompts */}
          <div className="bg-light px-3 py-2 border-bottom">
            <div className="d-flex align-items-center gap-1 mb-1 text-muted" style={{ fontSize: '0.72rem' }}>
              <BsLightbulbFill className="text-warning" /> Suggested queries:
            </div>
            <div className="d-flex flex-wrap gap-1">
              {sampleChatQuestions.map((q, idx) => (
                <button
                  key={idx}
                  onClick={() => handleSendMessage(q)}
                  className="btn btn-sm btn-outline-secondary text-truncate text-start py-0 px-2 rounded-pill"
                  style={{ fontSize: '0.74rem', maxWidth: '100%' }}
                >
                  {q}
                </button>
              ))}
            </div>
          </div>

          {/* Body */}
          <div className="chatbot-body">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`chat-bubble ${msg.sender === 'user' ? 'chat-bubble-user' : 'chat-bubble-bot'}`}
              >
                <div className="d-flex align-items-center gap-1 mb-1 opacity-75" style={{ fontSize: '0.72rem' }}>
                  {msg.sender === 'user' ? (
                    <>
                      <BsPersonFill /> You
                    </>
                  ) : (
                    <>
                      <BsRobot /> CareerMatch AI
                    </>
                  )}
                </div>
                <div>{msg.text}</div>
              </div>
            ))}

            {isTyping && (
              <div className="chat-bubble chat-bubble-bot text-muted fst-italic py-2" style={{ fontSize: '0.8rem' }}>
                <span className="spinner-grow spinner-grow-sm text-primary me-2" role="status"></span>
                AI Coach is thinking...
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Input Footer */}
          <div className="chatbot-footer">
            <div className="input-group">
              <input
                type="text"
                className="form-control border-end-0 shadow-none"
                style={{ fontSize: '0.88rem', borderRadius: '8px 0 0 8px' }}
                placeholder="Ask about resume tips, skills, or matching..."
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                onKeyDown={handleKeyDown}
              />
              <button
                className="btn btn-primary"
                type="button"
                style={{ borderRadius: '0 8px 8px 0' }}
                onClick={() => handleSendMessage()}
                disabled={!inputValue.trim()}
              >
                <BsSendFill size={14} />
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default ChatbotWidget;
