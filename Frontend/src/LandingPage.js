import React, { useState } from "react";
import "./LandingPage.css";

const LandingPage = () => {
  const [loading, setLoading] = useState({
    userClick: false,
    exploreClick: false,
  });

  const emitEvent = async (eventName) => {
    setLoading((prev) => ({ ...prev, [eventName]: true }));

    try {
      await fetch("http://localhost:8080/producer/event", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ event: eventName }),
      });

      setTimeout(() => {
        setLoading((prev) => ({ ...prev, [eventName]: false }));
      }, 800);
    } catch (error) {
      console.error("Error:", error);
      setLoading((prev) => ({ ...prev, [eventName]: false }));
    }
  };

  return (
    <div className="app">
      <header className="header">
        <button className="blog-btn">Read Blog</button>
      </header>

      <main className="main">
        <div className="content">
          <h1>Kafka Event Tracking Demo</h1>
          <p>
            This frontend demonstrates a real-time event tracking pipeline.
            Button clicks are sent to backend → Kafka → Prometheus → Grafana for
            monitoring and analytics.
          </p>

          <div className="buttons">
            <button
              className={`btn primary ${loading.userClick ? "loading" : ""}`}
              onClick={() => emitEvent("userClick")}
              disabled={loading.userClick}
            >
              {loading.userClick ? "Sending..." : "Start Learning Click Event"}
            </button>

            <button
              className={`btn secondary ${
                loading.exploreClick ? "loading" : ""
              }`}
              onClick={() => emitEvent("exploreClick")}
              disabled={loading.exploreClick}
            >
              {loading.exploreClick
                ? "Sending..."
                : "Explore Courses Click Event"}
            </button>
          </div>

          <div className="pipeline">
            <span>Frontend</span>
            <span>→</span>
            <span>Backend</span>
            <span>→</span>
            <span>Kafka</span>
            <span>→</span>
            <span>Prometheus</span>
            <span>→</span>
            <span>Grafana</span>
          </div>
        </div>
      </main>
    </div>
  );
};

export default LandingPage;
