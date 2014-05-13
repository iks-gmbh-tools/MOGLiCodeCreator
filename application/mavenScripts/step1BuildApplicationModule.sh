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
echo "*     *****                      Step 1:                    *****"
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
mvn clean install

echo "*"
echo "*"
echo "Press [Enter] to continue..."
read enter

