#!/bin/bash


#Ubuntu package install 
sudo apt-get update
sudo apt-get install build-essential
sudo apt-get install ninja-build

sudo apt-get install git-repo
sudo apt-get install cmake
sudo apt-get isntall haskell-stack
sudo apt-get install ccache
sudo apt-get install python-dev python-pip python3-dev python3-pip
sudo apt-get install libxml2-utils ncurses-dev
sudo apt-get install curl git doxygen

sudo curl -sSL https://get.haskellstack.org/ | sh

sudo apt-get install gcc-arm-linux-gnueabi g++-arm-linux-gnueabi
sudo apt-get install gcc-aarch64-linux-gnu g++-aarch64-linux-gnu
sudo apt-get install qemu-system-arm qemu-system-x86


sudo apt-get install clang gdb
sudo apt-get install libssl-dev libclang-dev libcunit1-dev libsqlite3-dev
sudo apt-get install qemu-kvm

sudo apt-get install libwww-perl libxml2-dev libxslt-dev
sudo apt-get install rsync
sudo apt-get install texlive-fonts-recommended texlive-latex-extra texlive-metapost texlive-bibtex-extra


#polyml
sudo apt-get install polyml
sudo apt-get install libpolyml-dev


#Python package install
pip install --user setuptools
pip install --user sel4-deps
pip install --user camkes-deps


#this script's dir
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
PATH=$PATH:$DIR


#Repo Tool
cd $DIR/..
git clone https://gerrit.googlesource.com/git-repo
REPODIR=$DIR/../git-repo
cd $REPODIR
sudo cp $REPODIR/repo /usr/bin
PATH=$PATH:$REPODIR


#HOL install
cd $DIR/..
git clone git://github.com/HOL-Theorem-Prover/HOL.git
HOLDIR=$DIR/../HOL
cd $HOLDIR
git checkout 7f7650b1f7
echo 'val polymllibdir = "/usr/lib/x86_64-linux-gnu";' > tools-poly/poly-includes.ML
poly < tools/smart-configure.sml
bin/build
cd $DIR 
sudo cp $HOLDIR/bin/Holmake /usr/bin
PATH=$PATH:$HOLDIR/bin


#CakeML install
cd $DIR/..
git clone https://github.com/CakeML/cakeml.git
CAKEDIR=$DIR/../cakeml
cd $CAKEDIR
git checkout 59886cd0205
cd $DIR


#regex filter install
FILT=$DIR/../cakeml-regex-filter
mkdir -p $FILT; mkdir -p $FILT/build
cd $FILT && repo init -u https://github.com/seL4/camkes-manifest.git && repo sync
cd $DIR

#ACT system-build install
cd $DIR/..
git clone git://github.com/chaosape/ACT_Demo_Dec2018.git
ACT=$DIR/../ACT_Demo_Dec2018
cd $DIR

#cake compiler
sudo cp cake /usr/bin


#bash
export PATH
bash



