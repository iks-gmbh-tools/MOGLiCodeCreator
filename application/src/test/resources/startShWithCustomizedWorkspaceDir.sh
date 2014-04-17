#!/bin/sh

echo "Executing MOGLiCodeCreator..."

workspaceDir="workspaces/test2"
JAVABIN=$JAVA_HOME/bin
CLASSPATH=$CLASSPATH:./lib/*:./lib/plugins/*
export CLASSPATH

echo CLASSPATH: $CLASSPATH
if [$JAVA_HOME == ""] 
then

	echo "environment variable JAVA_HOME not set"

	if [ ! -f $JAVABIN/java ] 
	then

		echo "Java executable not found in " $JAVABIN

	else

		echo $JAVABIN/java
		$JAVABIN/java -cp $CLASSPATH com.iksgmbh.moglicc.MOGLiCodeCreator $workspaceDir

	fi

else

	if [ ! -f $JAVA_HOME/bin/java ] 
	then

	    	echo "Java executable not found in " $JAVA_HOME/bin

	else 
		echo $JAVA_HOME/bin/java
		$JAVA_HOME/bin/java -cp $CLASSPATH com.iksgmbh.moglicc.MOGLiCodeCreator $workspaceDir
	fi
	
fi