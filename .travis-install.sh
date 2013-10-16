#!/usr/bin/env sh
mvn install:install-file -DgroupId=cpsuite -DartifactId=cpsuite -Dpackaging=jar -Dversion=1.2.6 -Dfile=url://http://johanneslink.net/downloads/cpsuite/cpsuite-1.2.6.jar -DgeneratePom=true
mvn clean install -Dmaven.test.skip=true --file ./parent/pom.xml --fail-never