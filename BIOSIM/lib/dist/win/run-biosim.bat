@echo off
call setENV.bat
set PATH=%BIOSIM_JAVA_HOME%\bin;%PATH
set jacoOrbClass=org.omg.CORBA.ORBClass=org.jacorb.orb.ORB
set jacoSingletonOrbClass=org.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton
set machineType=MACHINE_TYPE=CYGWIN
start /B javaw -D%jacoOrbClass% -D%jacoSingletonOrbClass% -D%machineType% -classpath biosim.jar BiosimStandalone
