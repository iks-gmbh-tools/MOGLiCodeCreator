echo off
echo *
echo *
echo *     ***********************************************************
echo *     ***********************************************************
echo *     *****                                                 *****
echo *     *****      Performing full quality assurance          *****
echo *     *****                                                 *****
echo *     ***********************************************************
echo *     ***********************************************************
echo *
echo *
echo *     ***********************************************************
echo *     ***********************************************************
echo *     *                                                         *
echo *     *                  ATTENTION:                             *
echo *     *                                                         *
echo *     * Make sure that the maven settings are valid defined in  *
echo *     * application/src/main/resources/build.properties         *
echo *     *                                                         *
echo *     * If not, some tests in this step and the following steps *
echo *     * will fail!                                              *
echo *     *                                                         *
echo *     ***********************************************************
echo *     ***********************************************************
echo *
echo *


echo *      Starting step 1 (mvn clean install -Dtest=BuildTestExecutor): building and unit-testing the sources of the 'MOGLiReleaseBuilder' for the automated release process
echo *      Please wait until step 1 is finished successfully!
echo *
start step1TestReleaseBuilderSources.bat
pause


echo *
echo *
echo *


echo *     Starting step 2 (mvn clean install on module 'parent'): building and unit-testing the sources of product
echo *     Please wait until step 2 is finished successfully!
echo *
start step2ExecUnitTestsAndBuildModules.bat
pause


echo *
echo *
echo *


echo *      Starting step 3 (mvn clean test on module 'inttest'): perform integration tests
echo *      Please wait until step 3 is finished successfully!
echo *
start step3ExecIntegrationTests.bat
pause


echo *
echo *
echo *


echo *      Starting step 4 (mvn clean install test -Dtest=BuildReleaseAndTestSystem): building and system testing the release candidate (the MOGLiCC product)
echo *      Please wait until step 4 is finished successfully!
echo *
start step4BuildReleaseAndExecSystemTests.bat
pause

echo *
echo *
echo *     ***********************************************************
echo *     ***********************************************************
echo *     *****                                                 *****
echo *     *****              Done with all steps                *****
echo *     *****                                                 *****
echo *     ***********************************************************
echo *     ***********************************************************
echo *
pause