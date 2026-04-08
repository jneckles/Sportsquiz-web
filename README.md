# Sports Quiz Tomcat App

This project was originally wired to school-only infrastructure. It is now set up to build and run on your home machine with a local Tomcat install and a local MySQL database.

## What changed

- Ant now defaults to the local course-library folder next to this project instead of `/students/home/CS-Courses/javalib`.
- Database settings now come from `src/main/config/app.properties` by default, with environment variables taking precedence:
  - `SPORTSQUIZ_DB_URL`
  - `SPORTSQUIZ_DB_USER`
  - `SPORTSQUIZ_DB_PASSWORD`
  - `SPORTSQUIZ_DB_INITIALIZE`
- On first startup, the app will create the quiz tables and seed sample data automatically if the database is empty.
- The broken 500 error-page mapping was fixed.

## Local setup

1. Make sure MySQL is running.
2. Edit `src/main/config/app.properties` if your local username or password differs from the defaults.
   For better security, leave `db.password` blank there and export `SPORTSQUIZ_DB_PASSWORD` before starting Tomcat.
3. Build the WAR:

```bash
ant distwar
```

4. Deploy the generated WAR:
   - Manual Tomcat deploy: copy `dist/Project3.war` into your Tomcat `webapps/` folder.
   - Tomcat Manager deploy: update `build.local.properties` from `build.local.properties.example`, then run:

```bash
ant deploy
```

5. Open the app at:

```text
http://localhost:8080/Project3/
```

## Notes

- The default database URL is `jdbc:mysql://localhost:3306/sports_quiz?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/New_York`.
- If you want different Tomcat manager credentials or jar locations, create `build.local.properties` and override the values there.
- To keep the DB password out of the repo, start Tomcat like this:

```bash
export SPORTSQUIZ_DB_PASSWORD='your-password-here'
/usr/local/tomcat/bin/startup.sh
```
