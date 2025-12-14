Installation / Execution Guide
Prerequisites
- JDK 17
- Maven 3.8+
- (Optional) Internet connection + YouTube API key (for online course search)

1) Open the correct project folder
1. Unzip the submission zip.
2. Open the folder that contains `pom.xml` (this is the Maven project root).

2) (Optional) Configure YouTube API key

Config file
- `src/main/resources/application.properties`

API Key Setup (YouTube Data API v3)
1. Open Cloud Console → enable YouTube Data API v3:
   https://console.cloud.google.com/apis/library/youtube.googleapis.com
2. Create an API key:
   https://console.cloud.google.com/apis/credentials
   
Required property
properties
youtube.api.key=YOUR_API_KEY_HERE

Other optional settings (already has defaults)
Optional (API base URL)
Default is already correct. Only change this if you know what you are doing:
youtube.api.base.url=https://www.googleapis.com/youtube/v3
Optional (timeout / retry / cache / fallback)
youtube.api.timeout.ms=3000
youtube.api.max.retries=2
cache.ttl.minutes=30
fallback.enabled=true

Security note: Never commit a real API key to a public repo.

3) Build the project (generate runnable jar)

From the folder with pom.xml:
mvn clean package

Expected output (Maven Shade Plugin):

target/LifelongLearningApp-1.0-SNAPSHOT-shaded.jar
(Some setups may also produce a non-shaded jar, but the runnable fat jar is the *-shaded.jar.)

4) Run the application (GUI)
If you are running the provided submission jar:
Run
java -jar LifelongLearningApp.jar

If you built from source with Maven
Run the shaded jar:
java -jar target/LifelongLearningApp-1.0-SNAPSHOT-shaded.jar

Expected: the Swing GUI window (MainFrame) will open.

Quick manual check (basic workflow):
Select a target role (example: Data Analyst).
Add 1–2 existing skills (example: SQL, Excel).
Click Analyze Gap to see gap result and match score.
Continue to course recommendation → learning path → resume bullets to verify end-to-end flow.

5) Run tests
Default test run (offline-safe)
mvn clean test

Note: integration tests are tagged as integration and excluded by default (Surefire excludes integration group).

Integration tests (may consume YouTube quota)

These call the real YouTube API and may consume quota:
edu.llapp.test.YouTubeClientTest
edu.llapp.test.CacheAndFallbackTest#testYouTubeCacheHit

To run integration tests manually:
mvn -Dgroups=integration test

Run a specific set of offline tests only (optional)
mvn -Dtest=SkillTest,SkillSetTest,UserProfileTest,SystemTest test

6) View code coverage (JaCoCo)

After running tests, open:
target/site/jacoco/index.html

Troubleshooting (common cases)

No API key / no internet: the system will still work with the local fallback course catalog (offline mode).
YouTube request failed / quota exceeded: retry later or rely on fallback mode.



