#!/bin/sh

################################################################
#                                                              #
#  WARNING: Do not modify or save this file under Windows.     #
#           It may be no more executable as shell script!      #
#                                                              #
################################################################


echo "*"
echo "*"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*     *****                                                 *****"
echo "*     *****      Performing full quality assurance          *****"
echo "*     *****                                                 *****"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*"
echo "*"


echo "*"
sh step1BuildApplicationModule.sh

echo "*"
echo "*"
echo "*"


echo "*"
sh step2ExecUnitTestsAndBuildModules.sh


echo "*"
echo "*"
echo "*"


echo "*"
sh step3ExecIntegrationTests.sh


echo "*"
echo "*"
echo "*"


echo "*"
sh step4BuildReleaseAndExecSystemTests.sh


echo "*"
echo "*"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*     *****                                                 *****"
echo "*     *****              Done with all steps                *****"
echo "*     *****                                                 *****"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*"
echo "*"
echo "Press [Enter] to continue..."
read enter


