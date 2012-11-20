package com.iksgmbh.moglicc;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.runner.RunWith;

// Builds first release candidate and second executes system tests on it

@RunWith(ClasspathSuite.class)
@ClassnameFilters({".*TestPropertiesActivator", ".*SystemTest"})
public class BuildReleaseAndTestSystem {

}
