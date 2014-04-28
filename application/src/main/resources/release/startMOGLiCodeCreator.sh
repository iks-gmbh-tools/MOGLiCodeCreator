#!/bin/sh
echo "Executing MOGLiCodeCreator..."
workspaceDir="$1"
JAVABIN=$JAVA_HOME/bin
export CLASSPATH="$CLASSPATH:./lib/*:./lib/plugins/*"
echo CLASSPATH: $CLASSPATH
echo workspaceDir: $1
if [$JAVA_HOME == ""] 
then

	echo "Problem: no JVM available because environment variable JAVA_HOME is not set"

else

	echo "JAVA_HOME: $JAVA_HOME"
	
	if [ -f $JAVA_HOME/java ] 
	then

		$JAVA_HOME/java -cp $CLASSPATH com.iksgmbh.moglicc.MOGLiCodeCreator $workspaceDir

	else 

		if [ -f $JAVA_HOME/bin/java ]
		then
		
			$JAVA_HOME/bin/java -cp $CLASSPATH com.iksgmbh.moglicc.MOGLiCodeCreator $workspaceDir
			
		else

	    	echo "Problem: no JVM available because no java executable was found in %JAVA_HOME%"

		fi
		
	fi

fi