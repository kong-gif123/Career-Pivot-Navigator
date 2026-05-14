# Career Pivot Navigator

> A Java desktop app that helps users identify skill gaps, discover courses, and generate resume bullets — powered by YouTube Data API v3 with offline fallback.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-3.8+-blue?logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-green)

---

## Features

- **Skill Gap Analysis** — Select a target role and input existing skills; the app calculates a match score and highlights missing skills
- **Course Recommendations** — Fetches relevant YouTube courses via API; falls back to a local catalog when offline or quota is exceeded
- **Learning Path Generation** — Sequences recommended courses into a structured learning roadmap
- **Resume Bullet Generator** — Produces role-targeted resume bullet points based on your skill profile
- **Resilient API Client** — Retries with exponential backoff, response caching (TTL configurable), and graceful degradation

---

## Architecture

![Architecture Diagram](./Architecture%20for%20Career%20Pivot%20Navigator%20System.drawio.png)

Key design patterns used:
- **Strategy Pattern** — Swappable matchers and recommenders
- **ApiClient isolation** — YouTube client is fully decoupled from business logic
- **Cache + Fallback** — In-memory cache with configurable TTL; local course catalog as safety net

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Build | Maven 3.8+ (Shade Plugin) |
| GUI | Java Swing |
| API | YouTube Data API v3 |
| Testing | JUnit 5, JaCoCo |

---

## Getting Started

### Prerequisites

- JDK 17
- Maven 3.8+
- (Optional) YouTube Data API v3 key for live course search

### Installation

```bash
# Clone the repo
git clone https://github.com/kong-pd/Career-Pivot-Navigator.git
cd Career-Pivot-Navigator

# Build the runnable jar
mvn clean package

# Run the app
java -jar target/LifelongLearningApp-1.0-SNAPSHOT-shaded.jar
```

---

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# YouTube API (optional — app works offline without this)
youtube.api.key=YOUR_API_KEY_HERE

# Timeout / retry / cache settings (defaults shown)
youtube.api.timeout.ms=3000
youtube.api.max.retries=2
cache.ttl.minutes=30
fallback.enabled=true
```

> **Security:** Never commit a real API key to a public repo.

To get a YouTube API key:
1. Enable YouTube Data API v3 at [Google Cloud Console](https://console.cloud.google.com/apis/library/youtube.googleapis.com)
2. Create a key at [Credentials](https://console.cloud.google.com/apis/credentials)

---

## Usage

1. Select a **target role** (e.g. Data Analyst)
2. Add your **existing skills** (e.g. SQL, Excel)
3. Click **Analyze Gap** to see your match score and skill gaps
4. Browse **course recommendations** and build a learning path
5. Generate **resume bullets** tailored to your target role

---

## Running Tests

```bash
# Offline-safe tests (default)
mvn clean test

# Integration tests (requires YouTube API key, consumes quota)
mvn -Dgroups=integration test

# View code coverage report
open target/site/jacoco/index.html
```

---

## License

MIT © [kong-pd](https://github.com/kong-pd)
