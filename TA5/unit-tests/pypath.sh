#!/bin/bash
# Source this if needed--it will augment the PYTHONPATH to
# include the python-capdl-tool, which is needed during builds.

HERE=`pwd`
export PYTHONPATH=${HERE}/projects/camkes/capdl/python-capdl-tool
