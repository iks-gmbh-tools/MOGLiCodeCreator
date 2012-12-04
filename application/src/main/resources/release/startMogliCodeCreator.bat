echo off
set classpath=".\lib\*;.\lib\plugins\*"
set JAVABIN="C:\Program Files\Java\jre6\bin"


if "%JAVA_HOME%" == "" goto Java_Home_Not_Set
if not exist "%JAVA_HOME%\bin\java.exe" goto Java_EXE_Not_FOUND_IN_JAVA_HOME
echo %JAVA_HOME%\bin\java
"%JAVA_HOME%\bin\java" -cp %classpath% com.iksgmbh.moglicc.MOGLiCodeCreator  
goto End


:Java_Home_Not_OK
if not exist %JAVABIN%\java.exe goto Java_Bin_Not_OK
%JAVABIN%\java -cp %classpath% com.iksgmbh.moglicc.MOGLiCodeCreator  
goto End


:Java_Home_Not_Set
echo environment varaiable JAVA_HOME not set
goto Java_Home_Not_OK


:Java_EXE_Not_FOUND_IN_JAVA_HOME
echo Java executable not found in "%JAVA_HOME%\bin"
goto Java_Home_Not_OK


:Java_Bin_Not_OK
echo Java executable not found in %JAVABIN%\java


:End