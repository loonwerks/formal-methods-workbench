#!/bin/bash

DIR="$( cd "$(dirname "$0")" ; pwd -P )"
FILT=$DIR/../cakeml-regex-filter
CAKEDIR=$DIR/../cakeml
HOLDIR=$DIR/../HOL



REGEX=`</dev/stdin`

cd $FILT/build
echo "Building filter for '$REGEX'"
../init-build.sh -DCAKEMLDIR=$CAKEDIR -DCAMKES_APP=cakeml_regex -DFilterRegex="$REGEX"
ninja
echo "Completed filter and with example producer, consumer."
echo "Try it out: $FILT/build/simulate"

cd $DIR


