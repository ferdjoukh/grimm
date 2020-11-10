#!/bin/bash

# Parametre 1: nom du fichier dot produit par grimm
# Parametre 2: nom du fichier dot à produire

echo "Graph g{" > res

#Chercher les EVin et les EVout
cat $1 | grep "EV" > edges1
sed -re 's/\[arrowhead=open,arrowtail=open,dir=both,label="EVin"]//g' edges1 > edges2
sed -re 's/\[arrowhead=open,arrowtail=open,dir=both,label="EVout"]//g' edges2 > edges3
sed -re 's/\ //g' edges3 | sed -re 's/struct//g' | sed -re 's/;//g'  > edges4


cat $1 | grep -o "weight=[0-9]*" | cut -d= -f2 > weights

#Combien de Edge ?
l1=$(cat edges4 | wc -w) 
l2=$(($l1 / 2))

#Creation des liens de scaffolding
for ((i=1;i<l2;i=i+2))
{
  j=$(($i+1))
  s=$(sed -n $i'p' edges4 | cut -f 3 -d '-')
  c=$(sed -n $j'p' edges4 | cut -f 3 -d '-')
  weight=$(sed -n $j'p' weights)
  echo $s--$c" [label=\"$weight\"];" >> res
}

##Creation des vertex
cat $1 | grep "Vertex" > nodes1
grep -oe "[0-9]*\ " nodes1 > nodes2
grep -oe "[0-9]*" nodes2 > nodes3

n=$(grep -oe "[0-9]*\ " nodes1 | wc -w)

#Creation des liens de contigs
for ((i=1;i<n;i=i+2))
{
  j=$(($i+1))
  s=$(sed -n $i'p' nodes3)
  c=$(sed -n $j'p' nodes3)
  echo $s--$c' [penwidth=10];' >> res
  echo $s';' >> res
  echo $c';' >> res
} 

echo "}" >> res

uniq res $2

#Generation du Pdf
dot -Tpdf $2 -o $2.pdf

#Fin
rm nodes*
rm edges*
rm res
rm weights
