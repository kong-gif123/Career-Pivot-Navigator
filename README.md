Installation/Execution Guide

Prerequisites
JDK 17
Maven 3.8+
(Optional) Internet connection + YouTube API key (for online course search)
1) Open the correct project folder
   Unzip the submission zip.
   Open the folder that contains pom.xml (this is the Maven project root).

2) (Optional) Configure YouTube API key
   Config file: src/main/resources/application.properties
   Set: youtube.api.key=YOUR_API_KEY_HERE
   Security note: do not put real API key in public repo.

## API Key Setup (YouTube Data API v3)

1. Open Cloud Console → enable YouTube Data API v3:
   https://console.cloud.google.com/apis/library/youtube.googleapis.com

2. Create an API key:
   https://console.cloud.google.com/apis/credentials

3. Put your key into `application.local.properties` (DO NOT commit this file):
   youtube.apiKey=YOUR_API_KEY_HERE
> Security note: Never commit real API keys. Keep `application.local.properties` in `.gitignore`.

3) Build the project (generate runnable jar)
   From the folder with pom.xml:
   mvn clean package

Expected output: a jar file will be generated under target/ (example: target/*.jar).

4) Run the application (GUI)
   Run the generated jar:
   java -jar target/*.jar
   Expected: the Swing GUI window (MainFrame) will open.
   Quick manual check (basic workflow)
   Select a target role (example: Data Analyst).
   Add 1–2 existing skills (example: SQL, Excel).
   Click “Analyze Gap” to see gap result and match score.
   Go to recommendation / learning path / resume bullet section to verify end-to-end flow.

5) Run unit tests
   Run all tests:
   mvn clean test

Important: some tests call real YouTube API and may consume quota (manual/integration):
edu.llapp.test.YouTubeClientTest
edu.llapp.test.CacheAndFallbackTest#testYouTubeCacheHit

If you want to run only offline safe tests:

mvn -Dtest=SkillTest,SkillSetTest,UserProfileTest,SystemTest test

6) View code coverage (JaCoCo)
After running tests, open:  
- `target/site/jacoco/index.html`

Troubleshooting (common cases)
- No API key / no internet: system will still work with local fallback course catalog (offline mode).  
- YouTube request failed / quota exceeded: retry later or use fallback mode.

