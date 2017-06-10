#!/bin/bash

java -jar -Dtron.gui=false -Dtron.coal="5 3" -Dtron.nbCoal="1 2" -Dtron.gridSize=7 -Dtron.result=true -Dtron.ptrace=true ./dist/Tron_Multiplayer-[0-9]*.[0-9]*.[0-9]*.jar
