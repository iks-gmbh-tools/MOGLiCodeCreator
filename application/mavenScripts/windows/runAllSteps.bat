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


echo *     Starting step 1
echo *     Please wait until step 1 is finished!
echo *
start step1BuildApplicationModule.bat
pause


echo *
echo *
echo *


echo *      Starting step 2
echo *      Please wait until step 2 is finished!
echo *
start step2ExecUnitTestsAndBuildModules.bat
pause


echo *
echo *
echo *


echo *      Starting step 3
echo *      Please wait until step 3 is finished!
echo *
start step3ExecIntegrationTests.bat
pause


echo *
echo *
echo *


echo *      Starting step 4
echo *      Please wait until step 4 is finished!
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