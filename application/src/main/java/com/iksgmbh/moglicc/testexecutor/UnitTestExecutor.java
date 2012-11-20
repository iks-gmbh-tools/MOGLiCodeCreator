package com.iksgmbh.moglicc.testexecutor;
import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.runner.RunWith;

// Executes Unit Tests of product code

@RunWith(ClasspathSuite.class)
@ClassnameFilters({".*UnitTest"})
public class UnitTestExecutor {
}
