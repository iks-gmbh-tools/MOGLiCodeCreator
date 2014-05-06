#!/bin/sh

################################################################
#                                                              #
#  WARNING: Do not modify or save this file under Windows.     #
#           It may be no more executable as shell script!      #
#                                                              #
################################################################

echo "Executing MOGLiCodeCreator..."
workspaceDir="$1"
SCRIPT_JAVA_HOME_BIN="/usr/lib/jvm/jdk1.8.0/bin"
export CLASSPATH="$CLASSPATH:./lib/*:./lib/plugins/*"

if [ $JAVA_HOME = "" ]
then
	echo "Warning: Environmental variable $JAVA_HOME is not set! Trying script setting..."

	if [ -f $SCRIPT_JAVA_HOME_BIN/java ] 
	then
		echo "java.exe found in $SCRIPT_JAVA_HOME_BIN"
		$SCRIPT_JAVA_HOME_BIN/java -cp $CLASSPATH com.iksgmbh.moglicc.MOGLiCodeCreator $workspaceDir
	else

		echo "Problem: No JVM available because no java executable was found in $SCRIPT_JAVA_HOME_BIN !"

	fi
else

	echo "JAVA_HOME is defined as environmental variable: $JAVA_HOME"
	
	if [ -f $JAVA_HOME/java ] 
	then
		echo "java.exe found in $JAVA_HOME"
		$JAVA_HOME/java -cp $CLASSPATH com.iksgmbh.moglicc.MOGLiCodeCreator $workspaceDir
	else 

		if [ -f $JAVA_HOME/bin/java ]
		then
		
			echo "java.exe found in $JAVA_HOME/bin"
			$JAVA_HOME/bin/java -cp $CLASSPATH com.iksgmbh.moglicc.MOGLiCodeCreator $workspaceDir
			
		else

	    		echo "Problem: no JVM available because no java executable was found in $JAVA_HOME !"

		fi
		
	fi

fi

echo "Done with Shell Script."
