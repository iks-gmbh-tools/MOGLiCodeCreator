REM Windows Start Script of MOGLiCC

echo off

set "workspaceDir=%~1"
set classpath=".\lib\*;.\lib\plugins\*"
set SystemJavaHome=%JAVA_HOME%
set SystemJavaHomeBin=%SystemJavaHome%\bin

REM To avoid using the JAVA_HOME system variable, remove 'REM' from the following line and set the correct path where a java.exe can be found
rem set ScriptJavaHomeBin="C:\Programs\java\jdk1.8.0\bin"

echo Executing MOGLiCodeCreator...
echo .



REM Check 1 (ScriptJavaHomeBin defined?) 

echo Checking local ScriptJavaHomeBin variable...
echo .
if "%ScriptJavaHomeBin%" == "" goto ScriptJavaHomeBin_Not_OK
echo Local ScriptJavaHomeBin variable is defined: (%ScriptJavaHomeBin%)
echo .
goto check_ScriptJavaHomeBin



REM Check 2a (java in ScriptJavaHomeBin available?)
:check_ScriptJavaHomeBin
if not exist %ScriptJavaHomeBin%\java.exe goto ScriptJavaHomeBin_Not_OK
echo java.exe found in %ScriptJavaHomeBin%
echo .
goto ExeJavaInScriptJavaHomeBin

 
REM Check 2b (system variable Java_Home defined?) 

:ScriptJavaHomeBin_Not_OK
echo Local script variable of JavaHome not correct defined. Checking system variable JAVA_HOME...
echo .
if "%SystemJavaHome%" == "" goto NoJavaExeFound
echo JAVA_HOME is defined as system variable: %SystemJavaHome%
echo .
goto Check_SystemJavaHome_Exe



REM Check 3 (java in SystemJavaHome available)

:Check_SystemJavaHome_Exe
if not exist "%SystemJavaHome%\java.exe" goto SystemJavaHome_Exe_not_Found
echo java.exe found in %SystemJavaHome%
goto ExeJavaInSystemJavaHome


:SystemJavaHome_Exe_not_Found
echo java.exe NOT found in SystemJavaHome. Checking ScriptJavaHomeBin (%SystemJavaHomeBin%)...
echo .
if not exist "%SystemJavaHomeBin%\java.exe" goto SystemJavaHomeBin_Exe_not_Found
goto ExeJavaInSystemJavaHomeBin



REM Check 4 (java in SystemJavaHomeBin available)

:SystemJavaHomeBin_Exe_not_Found
echo PROBLEM:  java.exe NOT found in %SystemJavaHomeBin%. 
echo .
goto NoJavaExeFound


REM Execution Commands

:ExeJavaInSystemJavaHome
echo Using %SystemJavaHome%\java.exe
echo .
echo .
echo .
"%SystemJavaHome%"\java -cp %classpath% com.iksgmbh.moglicc.MOGLiCodeCreator %workspaceDir%
goto End

:ExeJavaInSystemJavaHomeBin
echo Using %SystemJavaHomeBin%\java.exe
echo .
echo .
echo .
"%SystemJavaHomeBin%"\java -cp %classpath% com.iksgmbh.moglicc.MOGLiCodeCreator %workspaceDir%
goto End

:ExeJavaInScriptJavaHomeBin
echo Using %ScriptJavaHomeBin%\java.exe
echo .
echo .
echo .
"%ScriptJavaHomeBin%"\java -cp %classpath% com.iksgmbh.moglicc.MOGLiCodeCreator %workspaceDir%
goto End


REM Closing Commands

:NoJavaExeFound
echo PROBLEM:  No java.exe found. Define correctly either the local variable 'ScriptJavaHomeBin' in this script or the system variable JAVA_HOME.
pause
goto End

:End
echo .
echo End of MOGLiCC batch script reached.
echo .
