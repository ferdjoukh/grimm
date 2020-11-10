#!/bin/bash

#Il suffit de mettre ce fichier et grimm2scaffold 
#dans le dossier contenant les modeles

mkdir scaffoldGraphs

for dot in $(ls -t Graph* | grep ".dot" | sed -re 's/.dot//g')
do
echo 'Parsing '$dot'.dot...'   

./grimm2scaffold.sh ' ./'$dot'.dot' 'scaffoldGraphs/scaffold'$dot'.dot' 

echo 'OK'

done
