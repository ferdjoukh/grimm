#!/bin/bash

echo "This script creates the executable version of grimm tool"
echo "	"

grimmExecutable="grimm-executable"

if [[ ! -d "$grimmExecutable" ]]; then
	mkdir "$grimmExecutable"
	echo "1) $grimmExecutable dir was created"	
else
	rm -r "$grimmExecutable"
	mkdir "$grimmExecutable"
	echo "1) $grimmExecutable dir was re-created"
fi

cp target/grimm*.jar "$grimmExecutable/"
echo "2) Add grimm.jar"

cp "abssol.jar" "$grimmExecutable/"
echo "3) Add abssol.jar"

cp -r "meta-models" "$grimmExecutable/"
echo "4) Add example meta-models"

cp -r "parameters-files" "$grimmExecutable/"
echo "5) Add example parameters files"

cp -r "config-files" "$grimmExecutable/"
echo "6) Add example config files"

cp -r "CHR" "$grimmExecutable/"
echo "7) Add example CHR files"

zip -r "$grimmExecutable.zip" "$grimmExecutable/" > /dev/null
echo "8) Create $grimmExecutable.zip"

echo ""

echo "Success"
