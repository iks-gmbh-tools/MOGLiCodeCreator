#!/bin/sh

################################################################
#                                                              #
#  WARNING: Do not modify or save this file under Windows.     #
#           It may be no more executable as shell script!      #
#                                                              #
################################################################

echo "*"
echo "*"
echo "*"
echo "*"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*     *****                                                 *****"
echo "*     *****                      Step 2:                    *****"
echo "*     *****                  PREPARE TESTING                *****"
echo "*     *****     mvn clean install on application module     *****"
echo "*     *****                                                 *****"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*"
echo "*"
echo "*"
echo "*"

cd ..
mvn clean install -Dtest=BuildTestExecutor

echo "*"
echo "*"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*     *                                                         *"
echo "*     *                  ATTENTION:                             *"
echo "*     *                                                         *"
echo "*     * Make sure that the maven settings are valid defined in  *"
echo "*     * application/src/main/resources/build.properties         *"
echo "*     *                                                         *"
echo "*     * If not, some tests in this step and the following steps *"
echo "*     * will fail!                                              *"
echo "*     *                                                         *"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*"
echo "*"

echo "Press [Enter] to continue..."
read enter

