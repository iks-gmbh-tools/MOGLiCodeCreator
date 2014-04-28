echo off
set "workspaceDir=%~1"
set classpath=".\lib\*;.\lib\plugins\*"

if "%JAVA_HOME%" == "" goto Java_Home_Not_Set
if not exist "%JAVA_HOME%\java.exe" goto Java_EXE_Not_FOUND_IN_JAVA_HOME
echo java.exe found in %JAVA_HOME%
"%JAVA_HOME%\java" -cp %classpath% com.iksgmbh.moglicc.MOGLiCodeCreator %workspaceDir%
goto End

:Java_Home_Not_Set
echo "Problem: no JVM available because environment variable JAVA_HOME is not set"
goto End

:Java_EXE_Not_FOUND_IN_JAVA_HOME
if not exist %JAVA_HOME%\bin\java.exe goto Java_Home_Bin_Does_Not_Exist
echo java.exe found in %JAVA_HOME%\bin
%JAVA_HOME%\bin\java -cp %classpath% com.iksgmbh.moglicc.MOGLiCodeCreator %workspaceDir%
goto End

:Java_Home_Bin_Does_Not_Exist
echo "Problem: no JVM available because no java executable was found in %JAVA_HOME%"

:End