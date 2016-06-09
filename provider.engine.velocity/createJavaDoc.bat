echo off

del .\src\main\resources\helpData\\apidocs\com\iksgmbh\moglicc\provider\engine\velocity\TemplateJavaUtility.html
del .\src\main\resources\helpData\\apidocs\com\iksgmbh\moglicc\provider\engine\velocity\TemplateStringUtility.html

call mvn javadoc:javadoc

rem md .\src\main\resources\helpData\apidocs\com\iksgmbh\moglicc\provider\engine\velocity

copy .\target\apidocs\com\iksgmbh\moglicc\provider\engine\velocity\TemplateJavaUtility.html .\src\main\resources\helpData\apidocs\com\iksgmbh\moglicc\provider\engine\velocity
copy .\target\apidocs\com\iksgmbh\moglicc\provider\engine\velocity\TemplateStringUtility.html .\src\main\resources\helpData\apidocs\com\iksgmbh\moglicc\provider\engine\velocity