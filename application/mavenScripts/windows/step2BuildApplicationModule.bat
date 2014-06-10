echo off
echo *
echo *
echo *
echo *
echo *     ***********************************************************
echo *     ***********************************************************
echo *     ***********************************************************
echo *     *****                                                 *****
echo *     *****                      Step 2:                    *****
echo *     *****                  PREPARE TESTING                *****
echo *     *****     mvn clean install on application module     *****
echo *     *****                                                 *****
echo *     ***********************************************************
echo *     ***********************************************************
echo *     ***********************************************************
echo *
echo *
echo *
echo *

cd ..\..
call mvn clean install -Dtest=BuildTestExecutor

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

pause
