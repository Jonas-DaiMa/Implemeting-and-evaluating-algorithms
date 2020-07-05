#!/bin/bash

# if want to run on 1 to 3 algo
ALGO1=${1?Error: no algo given}
ALGO2=${2:-""}
ALGO3=${3:-""}
nn="$ALGO1 $ALGO2 $ALGO3"

# how many tests cases want to consider
read -p 'All tests? : ' ans
if [ $ans = y -o $ans = yes ]; then
    tests=$(seq 0 20)
else
    read -p 'start : ' start
    read -p 'end : ' end
    tests=$(seq $start $end)
fi

# runs the actual tests
for algo in $nn
do
    if [ ! -d "data/BinSearchTests/$algo" ]; then
    mkdir $algo
    fi

    echo "-------------------------------------------------"
    echo "                 Testing $algo                   "
    echo "-------------------------------------------------"

    for i in $tests
    do
        if [ $i -lt 10 ]; then
            n="0$i"
        else
            n="$i"
        fi
        ourAns="data/BinSearchTests/$algo/Test$n.txt"
        orgAns="data/BinSearchTests/Test$n.ans"
        java -cp bin part1.$algo < data/BinSearchTests/Test$n.in > $ourAns
        if cmp -s "$orgAns" "$ourAns" ; then
            echo "Test$n passed"
        else
            echo "Test$n failed -- Run 'make $algo' on $n"
        fi
    done
done