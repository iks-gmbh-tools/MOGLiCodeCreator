/usr/bin/env sh

mvn install:install-file -DgroupId=cpsuite -DartifactId=cpsuite -Dpackaging=jar -Dversion=1.2.6 -Dfile=url://http://johanneslink.net/downloads/cpsuite/cpsuite-1.2.6.jar -DgeneratePom=true -U 
mvn install -Dmaven.test.skip=true