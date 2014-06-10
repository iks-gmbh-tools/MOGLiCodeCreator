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
echo "*     *****                      Step 3:                    *****"
echo "*     *****             EXECUTE INTGRATION TESTS            *****"
echo "*     *****             mvn clean test on inttest           *****"
echo "*     *****                                                 *****"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*"
echo "*"
echo "*"
echo "*"

cd ../../inttest
mvn clean test

echo "*"
echo "*"
echo "Press [Enter] to continue..."
read enter

