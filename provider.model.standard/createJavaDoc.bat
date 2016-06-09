echo off

del ..\interfaces\target\apidocs\com\iksgmbh\moglicc\provider\model\standard\metainfo\MetaInfoSupport.html 
del ..\interfaces\target\apidocs\com\iksgmbh\moglicc\provider\model\standard\AttributeDescriptor.html 
del ..\interfaces\target\apidocs\com\iksgmbh\moglicc\provider\model\standard\ClassDescriptor.html 
del ..\interfaces\target\apidocs\com\iksgmbh\moglicc\provider\model\standard\Model.html 


cd ..\interfaces

call mvn javadoc:javadoc

cd ..\provider.model.standard

rem md .\src\main\resources\helpData\apidocs\com\iksgmbh\moglicc\provider\model\standard\metainfo

copy ..\interfaces\target\apidocs\com\iksgmbh\moglicc\provider\model\standard\metainfo\MetaInfoSupport.html .\src\main\resources\helpData\apidocs\com\iksgmbh\moglicc\provider\model\standard\metainfo
copy ..\interfaces\target\apidocs\com\iksgmbh\moglicc\provider\model\standard\AttributeDescriptor.html .\src\main\resources\helpData\apidocs\com\iksgmbh\moglicc\provider\model\standard
copy ..\interfaces\target\apidocs\com\iksgmbh\moglicc\provider\model\standard\ClassDescriptor.html .\src\main\resources\helpData\apidocs\com\iksgmbh\moglicc\provider\model\standard
copy ..\interfaces\target\apidocs\com\iksgmbh\moglicc\provider\model\standard\Model.html .\src\main\resources\helpData\apidocs\com\iksgmbh\moglicc\provider\model\standard
