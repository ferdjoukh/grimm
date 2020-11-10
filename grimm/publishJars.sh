#!/bin/bash

mvn install:install-file -Dfile=plugins4grimm/jdom-2.0.6.jar -DgroupId=jdom -DartifactId=org.jdom2 -Dversion=2.0.6 -Dpackaging=jar
mvn install:install-file -Dfile=plugins4grimm/lpg.runtime.java_2.0.17.v201004271640.jar -DgroupId=lpg.runtime.java -DartifactId=lpg.runtime.java -Dversion=2.0.17 -Dpackaging=jar
mvn install:install-file -Dfile=plugins4grimm/org.eclipse.ocl_3.6.100.v20160613-1351.jar -DgroupId=org.eclipse.ocl -DartifactId=org.eclipse.ocl -Dversion=3.6 -Dpackaging=jar
mvn install:install-file -Dfile=plugins4grimm/org.eclipse.ocl.ecore_3.6.100.v20160613-1351.jar -DgroupId=org.eclipse.ocl -DartifactId=org.eclipse.ocl.ecore -Dversion=3.6 -Dpackaging=jar
mvn install:install-file -Dfile=plugins4grimm/org.eclipse.ocl.common_1.4.100.v20160613-1351.jar -DgroupId=org.eclipse.ocl -DartifactId=org.eclipse.ocl.common -Dversion=1.4 -Dpackaging=jar


