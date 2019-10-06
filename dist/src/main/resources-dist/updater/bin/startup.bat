SET scriptPath=%~dp0
SET curDir=%cd%
cd %scriptPath%
cd ..
SET app.dir=%cd%
cd ..
SET server.dir=%cd%\server
cd %curDir%
java -jar -Dapp.dir=%app.dir% %app.dir%\lib\update.jar --server.config=%server.dir%\config\application.yml --db.root.username=postgres --db.root.password=postgres