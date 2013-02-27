#The MOGLi Code Creator   

* * *

Just another code generator? - NO! - It's THE Lightweight autogeneration tool! 

It is a small standalone Tool for a quick start into model based development!

It's written in Java but made to generate more than only Java code.  

You find the lastest release build under application/releasedBuilds.

* * *

**MOGLi** stands for the following attributes: 

**M**  odel based

**O**  pen for extension

**G**  enerator based

**Li**  ghtweight  


* * *

License: **All Rights Reserved**

Current version: **1.1.0**

**Changes to 1.0.1**
- New property 'BraceSymbolForMetaInfoValues' for the plugin.properties file of the StandardModelProvider. By replacing the standard symbol Double Quotes <"> by another symbol Double Quotes can be used within names or values of MetaInfos. Detailed Information is available in the default _model.properties file of the StandardModelProvider.
- New template header attribute '@OutputEncodingFormat' that is used by generator plugins to create output files. See also <root dir>\help\VelocityModelBasedInserter\TemplateFileHeaderInserterAttributes.htm and <root dir>\help\VelocityClassBasedGenerator\TemplateFileHeaderInserterAttributes.htm
- New Feature when starting MOGLiCC: an argument specifying the workspace to use can be defined. If not set, the workspace to use is read from the application properies file as in version 1.0.1.See <rootDir>/startMOGLiCodeCreator.bat
- New type of MetaInfo validation introduced: MetaInfo can be validated both by number of occurrences and by MetaInfo values. In addition, validation can depend on conditions that must be met. For this purpose a specific MetaInfoValidator-DSL is introduced.Detailed Information is available in the default MetaInfo.validation file of the VelocityClassBasedGenerator.
- Since V1.1.0 plugins are deactivated by default. To activate plugins, a corresponding activation setting in the workspace.properties file must exist.
- Method 'doesHaveAnyMetaInfosWithName(metaInfoName)' added to Model, ClassDescriptor and AttributeDescriptor of StandardModelProvider.
- New model file 'MOGLiCC-Plugin' added to the StandardModelProvider
- New artefact 'MOGLiCC-Plugin" added to the VelocityClassBasedGenerator 

**Changes to 1.0.0**
- empty output file bug fixed

* * *


Versioning convention: major.minor.revision

major:    will change for basic framework modification

minor:    will change for new features

revision: will change for bug fixes


* * *


####Markdown Documentation

you can find documentation around markdown here:
- [Daring Fireball] [1]
- [Wikipedia - markdown] [2]

  [1]: http://daringfireball.net/projects/markdown/syntax
  [2]: http://en.wikipedia.org/wiki/Markdown