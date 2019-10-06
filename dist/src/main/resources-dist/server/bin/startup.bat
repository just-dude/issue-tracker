SET scriptPath=%~dp0
SET curDir=%cd%
cd %scriptPath%
cd ..
SET app.dir=%cd%
cd %curDir%
java -jar -Dapp.dir=%app.dir% -Dlog4j.configurationFile=%app.dir%\config\log4j2.xml %app.dir%\lib\application.jar --spring.config.location=%app.dir%\config\application.yml