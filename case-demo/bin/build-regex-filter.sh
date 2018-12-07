#!/bin/bash

DIR="$( cd "$(dirname "$0")" ; pwd -P )"
FILT=$DIR/../cakeml-regex-filter
CAKEDIR=$DIR/../cakeml
HOLDIR=$DIR/../HOL

cd $FILT/build

#if [-z "$1"] then
  $FILT/init-build.sh -DCAKEMLDIR=$CAKEDIR -DCAMKES_APP=cakeml_regex
#else 
#  $FILT/init-build.sh -DCAKEMLDIR=$CAKEDIR -DCAMKES_APP=cakeml_regex -DFilterRegex=$1
#fi
ninja

cd $DIR


