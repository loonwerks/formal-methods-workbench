#!/bin/bash

DIR="$( cd "$(dirname "$0")" ; pwd -P )"
FILT=$DIR/../cakeml-regex-filter
CAKEDIR=$DIR/../cakeml
HOLDIR=$DIR/../HOL



REGEX=$1

if [ -z "$REGEX" ]
then

  echo "usage: build-regex-filter <regex>"

else
  cd $FILT/build
  rm -r *
  echo "Building filter for '$REGEX'"
  ../init-build.sh -DCAKEMLDIR=$CAKEDIR -DCAMKES_APP=cakeml_regex -DFilterRegex="$REGEX"
  ninja

  echo "Try it out: $FILT/build/simulate"

cd $DIR

fi


