#!/bin/bash

echo "*building biosim documentation"
echo "	-initializing biosim documentation build...";
userSelect="$@"
# see if the biosim directory exists, if it doesn't, assume it's one directory back (i.e., user is in bin directory)
devRootDir=$BIOSIM_HOME
if [ -z "$devRootDir" ]
then
	devRootDir=".."
	echo "		-assuming BIOSIM_HOME is $devRootDir"
fi
JACORB_HOME="$devRootDir/lib/jacorb"
javadocCommand="javadoc"
type $javadocCommand 2> /dev/null >/dev/null
if [ $? != 0 ]; then
	echo "		-couldn't find javadoc command!"
	#should quit here
fi
docString="/doc"
docDir=$devRootDir$docString
separator=":"
machineType=`uname`
winName="CYGWIN"
case $machineType in
	*$winName*) separator=";";echo "		-machine type is $winName";;
	*)separator=":";echo "		-assuming Unix machine type";;
esac
genString="/generated"
genDir=$devRootDir$genString
clientString="/client"
clientGenDir=$genDir$clientString
serverString="/server"
serverGenDir=$genDir$serverString
stubString="/stubs"
stubDir=$clientGenDir$stubString
skeletonString="/skeletons"
skeletonDir=$serverGenDir$skeletonString
serverClassesString="/classes"
serverClassesDir=$serverGenDir$serverClassesString
clientClassesString="/classes"
clientClassesDir=$clientGenDir$clientClassesString
relativeIDLDir="/src/biosim/idl/biosim.idl"
fullIDLDir=$devRootDir$relativeIDLDir
simString="biosim"
simStubDir="$stubDir/$simString"
sourceDir="$devRootDir/src"
clientDir="$sourceDir/biosim/client"
jacoClasspath="$JACORB_HOME/lib/jacorb.jar$separator$JRE_HOME/lib/rt.jar$separator$JACORB_HOME/lib$separator$JACORB_HOME/lib/idl.jar"
docSourcepath="$sourceDir$separator$stubDir$separator$skeletonDir$"
docClasspath="$clientClassesDir$separator$serverClassesDir$separator$jacoClasspath"
####################
#	DOC BUILD             #
####################
echo "	-creating docs"
echo "		-creating package list"
java -classpath $devRootDir/lib/docutil/doccheck.jar com.sun.tools.doclets.util.PackageList -skipAll CVS $sourceDir $serverGenDir$skeletonString > $docDir/package-list
echo "		-creating html documentation"
javadocInvocation="$javadocCommand -breakiterator -d $docDir -classpath $docClasspath -sourcepath $docSourcepath"
$javadocInvocation @$docDir/package-list
echo "		-removing package list"
rm -f $docDir/package-list
echo "*done creating biosim docs"



