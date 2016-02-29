package com.iksgmbh.moglicc.testexecutor;
import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.runner.RunWith;

// Executes system tests on released sources

@RunWith(ClasspathSuite.class)
@ClassnameFilters({".*SystemTest"})
public class SystemTestExecutor {
}
