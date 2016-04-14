#!/bin/sh

################################################################
#                                                              #
#  WARNING: Do not modify / save this file under Windows.      #
#           This would change the encoding and the file would  #
#           be no more executable as shell script on a         #
#           non-Windows maschine !!!                           #
#                                                              #
################################################################

JavaFound="false"
workspaceDir="$1"
export CLASSPATH="$CLASSPATH:./lib/*:./lib/plugins/*;./lib/dropins/*"

SystemJavaHome=$JAVA_HOME
SystemJavaHomeBin="$SystemJavaHome/bin"


# To avoid using the JAVA_HOME system variable, remove '#' from the following line and
# set correct path where a java.exe can be found:
#ScriptJavaHomeBin="/usr/lib/jvm/jdk1.8.0/bin"



echo "Executing MOGLiCodeCreator..."
echo .


# Check 1 (ScriptJavaHomeBin defined?)

echo "Checking local ScriptJavaHomeBin variable..."
echo .
if [ $ScriptJavaHomeBin != "" ]
then
	echo "Local ScriptJavaHomeBin is defined: $ScriptJavaHomeBin"
	echo .

	# Check 2a (java in ScriptJavaHomeBin available?)

	if [ -f $ScriptJavaHomeBin/java ] 
	then
		echo "java.exe found in $ScriptJavaHomeBin"
		echo .
		echo .
		echo .
		"$ScriptJavaHomeBin"/java -cp $CLASSPATH com.iksgmbh.moglicc.MOGLiCodeCreator $workspaceDir
		JavaFound="true"

	else
		echo "Local script variable of JavaHome not correct defined. Checking system varialbe JAVA_HOME..."
		echo .
	fi

else
	echo "Local script variable of JavaHome not defined. Checking system varialbe JAVA_HOME..."
fi


if [ $JavaFound = "false" ]
then

	# Check 2b (system variable JAVA_HOME defined?)
	if [ $SystemJavaHome != "" ]
	then

		echo "JAVA_HOME is defined as system variable: $SystemJavaHome"
		echo .


		# Check 3 (java in SystemJavaHome available?)

		if [ -f $SystemJavaHome/java ] 
		then
			echo "java.exe found in $SystemJavaHome"
			echo .
			echo .
			echo .
			"$SystemJavaHome"/java -cp $CLASSPATH com.iksgmbh.moglicc.MOGLiCodeCreator $workspaceDir
			JavaFound="true"
		else

			# Check 4 (java in SystemJavaHomeBin available?)

			echo "No java.exe found in SystemJavaHome. Checking SystemJavaHomeBin ($SystemJavaHomeBin)"
			if [ -f $SystemJavaHomeBin/java ] 
			then
				echo "java.exe found in $SystemJavaHomeBin"
				echo .
				echo .
				echo .
				"$SystemJavaHomeBin"/java -cp $CLASSPATH com.iksgmbh.moglicc.MOGLiCodeCreator $workspaceDir
				JavaFound="true"
			fi
		fi

	else

		echo "JAVA_HOME is not defined as system variable."

	fi

fi

if [ $JavaFound = "false" ]
then
	echo "Problem: No java.exe found. Define correctly either the local variable 'ScriptJavaHomeBin' in this script or the system variable JAVA_HOME."
	echo .
	echo "Press [Enter] to continue..."
	read enter
fi	

echo "End of MOGLiCC shell script reached."
