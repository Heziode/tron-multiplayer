#!/bin/bash

baseDir=./paranoid_traceback
files=( $baseDir/*.dot )

for file in "${files[@]}"
do
    filename="${file##*/}"
    filenameWithoutExtension="${filename%.*}"
    dot -Tsvg "$baseDir/$filenameWithoutExtension.dot" -o "$baseDir/$filenameWithoutExtension.svg"
done
