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
echo "*     *****       Execute UNIT TESTS and build modules      *****"
echo "*     *****           mvn clean install on parent           *****"
echo "*     *****                                                 *****"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*     ***********************************************************"
echo "*"
echo "*"
echo "*"
echo "*"

cd ../../parent
mvn clean install

echo "*"
echo "*"
echo "Press [Enter] to continue..."
read enter

