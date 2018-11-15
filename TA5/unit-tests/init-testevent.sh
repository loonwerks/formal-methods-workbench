#!/bin/bash
#
# Module: init-testevent.sh
# Description:
#   Prepare the testevent example. Illustrates one component
# sending events to another, which handles them in various ways.
#
# You must link this script and the projects/camkes/apps/testevent
# directory to the corresponding places in your camkes-project/
# directory.
#
# You may need to add the python-capdl-tool to your PYTHONPATH
# See example below.
#
# Run this from the camkes-project/ directory top level;
# it will create a build-directory "build-tev".

APP=testevent
BUILDDIR=./build-tev
PLAT=ia32

#--------------------------------------

HERE=`pwd`
export PYTHONPATH=${HERE}/projects/camkes/capdl/python-capdl-tool

set -e
rm -rf $BUILDDIR
mkdir  $BUILDDIR
cd $BUILDDIR

../init-build.sh -DPLATFORM=$PLAT -DCAMKES_APP=$APP

echo "Done. Output in $BUILDDIR, ready for ninja"
