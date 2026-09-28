// Mock data for CareerMatch AI frontend demonstration

export const mockDefaultAnalysis = {
  id: 1,
  resumeName: "Alex_Morgan_Senior_Software_Engineer.pdf",
  fileSize: "184 KB",
  jobTitle: "Senior Full Stack & Java Engineer",
  company: "Apex Global Tech",
  jobDescription: `We are looking for an experienced Senior Full Stack Java Engineer to lead our microservices architecture and client-facing web applications.
Responsibilities:
- Build and maintain robust RESTful APIs using Java 17/21 and Spring Boot.
- Design relational schemas in MySQL and implement ORM with Hibernate/JPA.
- Integrate frontend interfaces with React.js, Vite, and Bootstrap.
- Deploy scalable services in Docker and orchestrate with Kubernetes on AWS.
- Implement high-performance messaging with Apache Kafka and in-memory caching with Redis.
- Drive automated testing with JUnit and CI/CD pipelines.

Requirements:
- Bachelor's or Master's in Computer Science or related field.
- 4+ years of professional backend engineering experience with Java and Spring ecosystem.
- Solid understanding of SQL, schema design, and query optimization.
- Experience with containerization (Docker) and cloud services (AWS).
- Knowledge of event-driven architectures (Kafka) and caching (Redis) is a plus.`,
  atsScore: 84,
  matchingPercentage: 84,
  keywordMatchPercentage: 82,
  educationMatch: 95,
  educationDetails: "Master of Science in Computer Science (Meets & exceeds degree criteria)",
  experienceMatch: 90,
  experienceDetails: "5+ years of verified software development experience (4+ years required)",
  technicalSkillsMatch: 82,
  matchedSkills: [
    "Java",
    "Spring Boot",
    "REST APIs",
    "MySQL",
    "Hibernate / JPA",
    "Microservices",
    "React.js",
    "Docker",
    "Git",
    "JUnit / Unit Testing"
  ],
  missingSkills: [
    "Kubernetes",
    "AWS (Cloud Architecture)",
    "Apache Kafka",
    "Redis (Caching)",
    "CI/CD Pipelines (Jenkins/GitHub Actions)"
  ],
  keywordsFound: [
    "Java 21",
    "Spring Boot",
    "RESTful APIs",
    "Relational Databases",
    "MySQL",
    "JPA/Hibernate",
    "Microservices Architecture",
    "React",
    "Docker",
    "Unit Testing"
  ],
  keywordsMissing: [
    "Kubernetes Orchestration",
    "Apache Kafka Event Streams",
    "Redis In-Memory Cache",
    "AWS Deployment (ECS/EKS)",
    "Automated CI/CD Workflow"
  ],
  resumeKeywords: [
    "Java",
    "Spring Boot",
    "MySQL",
    "REST APIs",
    "React.js",
    "Docker",
    "Git",
    "JUnit",
    "Hibernate",
    "Microservices",
    "SQL Query Tuning",
    "Agile/Scrum",
    "Web Security",
    "JavaScript",
    "HTML5/CSS3"
  ],
  jobKeywords: [
    "Java",
    "Spring Boot",
    "REST APIs",
    "MySQL",
    "Hibernate/JPA",
    "Microservices",
    "React.js",
    "Docker",
    "Kubernetes",
    "AWS",
    "Kafka",
    "Redis",
    "JUnit",
    "CI/CD"
  ],
  aiCoachSuggestions: {
    skillsToImprove: [
      "Add container orchestration experience: Mention any exposure to Kubernetes (e.g. minikube, Helm charts, or cluster management).",
      "Incorporate event streaming: Build or document an event-driven flow using Apache Kafka or RabbitMQ.",
      "Highlight caching techniques: Add a bullet point demonstrating response time reduction using Redis."
    ],
    keywordsToAdd: [
      "Kubernetes (K8s)",
      "Apache Kafka",
      "Redis Caching",
      "AWS Cloud Infrastructure",
      "CI/CD Automation",
      "High Availability"
    ],
    summarySuggestions: [
      "Make your opening summary more metric-focused: 'Senior Java & Full Stack Engineer with 5+ years building fault-tolerant microservices and responsive web platforms serving 1M+ active users.'",
      "Explicitly mention your core tech stack in the first 2 lines for quick recruiter scanning."
    ],
    projectSuggestions: [
      "Quantify database impact: 'Optimized complex MySQL queries and index structures, improving response latency by 38% under high load.'",
      "Highlight modern deployment: 'Dockerized multi-tier applications and spearheaded automated testing suites with JUnit, achieving 90% test coverage.'"
    ]
  }
};

export const mockHistoryData = [
  {
    id: 1,
    resumeName: "Alex_Morgan_Senior_Software_Engineer.pdf",
    jobRole: "Senior Full Stack & Java Engineer",
    company: "Apex Global Tech",
    atsScore: 84,
    date: "2026-09-28",
    matchedCount: 10,
    missingCount: 5,
    status: "Strong Match"
  },
  {
    id: 2,
    resumeName: "Alex_Morgan_Backend_Lead.docx",
    jobRole: "Backend Distributed Systems Engineer",
    company: "CloudScale Inc.",
    atsScore: 73,
    date: "2026-09-25",
    matchedCount: 8,
    missingCount: 7,
    status: "Moderate Match"
  },
  {
    id: 3,
    resumeName: "Alex_Morgan_FullStack_React_Java.pdf",
    jobRole: "Lead Full Stack Developer",
    company: "FinTech Prime",
    atsScore: 92,
    date: "2026-09-21",
    matchedCount: 13,
    missingCount: 2,
    status: "Exceptional Match"
  },
  {
    id: 4,
    resumeName: "Alex_Morgan_Draft_Resume.pdf",
    jobRole: "DevOps & Cloud Infrastructure Specialist",
    company: "InfraCore Networks",
    atsScore: 56,
    date: "2026-09-14",
    matchedCount: 5,
    missingCount: 11,
    status: "Needs Optimization"
  }
];

export const sampleChatQuestions = [
  "How can I improve my resume?",
  "What skills am I missing?",
  "Is my resume suitable for this job?",
  "What should I learn for this role?"
];
