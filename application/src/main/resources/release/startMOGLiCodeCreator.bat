echo off
echo Executing MOGLiCodeCreator...
set "workspaceDir=%~1"
set classpath=".\lib\*;.\lib\plugins\*"
set SCRIPT_JAVA_HOME_BIN="C:\Programme\Java\jdk1.8.0\bin"

if "%JAVA_HOME%" == "" goto Java_Home_Not_Set
echo JAVA_HOME is defined as environmental variable: %JAVA_HOME%
if not exist "%JAVA_HOME%\java.exe" goto no_JavaExe_in_JAVA_HOME
echo java.exe found in %JAVA_HOME%
"%JAVA_HOME%\java" -cp %classpath% com.iksgmbh.moglicc.MOGLiCodeCreator %workspaceDir%
goto End

:Java_Home_Not_Set
echo Warning: Environmental variable 'JAVA_HOME' is not set! Trying script setting...
if not exist %SCRIPT_JAVA_HOME_BIN%\java.exe goto Script_Java_Home_Bin_Not_OK
echo java.exe found in %SCRIPT_JAVA_HOME_BIN%
%SCRIPT_JAVA_HOME_BIN%\java -cp %classpath% com.iksgmbh.moglicc.MOGLiCodeCreator %workspaceDir%
goto End

:no_JavaExe_in_JAVA_HOME
if not exist %JAVA_HOME%\bin\java.exe goto Java_Home_Bin_Not_OK
echo java.exe found in %JAVA_HOME%\bin
%JAVA_HOME%\bin\java -cp %classpath% com.iksgmbh.moglicc.MOGLiCodeCreator %workspaceDir%
goto End

:Script_Java_Home_Bin_Not_OK
echo Problem: No JVM available because no java executable was found in %SCRIPT_JAVA_HOME_BIN% !
goto End

:Java_Home_Bin_Not_OK
echo Problem: No JVM available because no java executable was found in %JAVA_HOME% !

:End
echo Done with MOGLiCC batch script.

