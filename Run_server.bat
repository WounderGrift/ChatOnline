echo Info: (Shift + Right Mouse Button) Batnik don't should go out from the root
cd out/artifacts/server_jar
IF NOT EXIST "%JAVA_HOME%/bin/java.exe" (
echo Error: file java.exe not found, need switch path on main directory Java\jdk\bin) else (
echo Info: Path is truth)
path =  %JAVA_HOME%/bin
dir/s *java.exe*
java -jar server.jar