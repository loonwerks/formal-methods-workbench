#!/bin/bash


#Ubuntu package install 
sudo apt-get update
sudo apt-get install build-essential

sudo apt-get install ccache
sudo apt-get install python-dev python-pip python3-dev python3-pip
sudo apt-get install libxml2-utils ncurses-dev
sudo apt-get install curl git doxygen

sudo apt-get install gcc-arm-linux-gnueabi g++-arm-linux-gnueabi
sudo apt-get install gcc-aarch64-linux-gnu g++-aarch64-linux-gnu
sudo apt-get install qemu-system-arm qemu-system-x86


sudo apt-get install clang gdb
sudo apt-get install libssl-dev libclang-dev libcunit1-dev libsqlite3-dev
sudo apt-get install qemu-kvm

sudo apt-get install libwww-perl libxml2-dev libxslt-dev
sudo apt-get install mlton rsync
sudo apt-get install texlive-fonts-recommended texlive-latex-extra texlive-metapost texlive-bibtex-extra


#Python package install
pip install --user setuptools
pip install --user sel4-deps
pip install --user camkes-deps



#this script's dir
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
PATH=$PATH:$DIR


#ninja
cd $DIR
wget https://github.com/ninja-build/ninja/releases/download/v1.8.2/ninja-linux.zip
unzip ninja-linux.zip
rm ninja-linux.zip
sudo cp ninja /usr/bin/ninja


#cmake install
cd $DIR/..
CMAKEDIR=$DIR/../cmake
wget https://github.com/Kitware/CMake/releases/download/v3.13.1/cmake-3.13.1.tar.gz
mv cmake* "$CMAKEDIR.tar.gz"
tar xf "$CMAKEDIR.tar.gz"
rm "$CMAKEDIR.tar.gz"
mv cmake* "$CMAKEDIR"
cd $CMAKEDIR
./bootstrap
sudo make
sudo make install
cd $DIR

#haskell-stack
cd $DIR/..
wget https://github.com/commercialhaskell/stack/releases/download/v1.9.3/stack-1.9.3-linux-x86_64.tar.gz
STACKDIR=$DIR/../stack
mv stack* "$STACKDIR.tar.gz"
tar xf "$STACKDIR.tar.gz"
rm "$STACKDIR.tar.gz"
mv stack* $STACKDIR
cd $STACKDIR
stack upgrade
PATH=$PATH:$STACKDIR
cd $DIR

#Repo Tool
cd $DIR/..
git clone https://gerrit.googlesource.com/git-repo
REPODIR=$DIR/../git-repo
cd $REPODIR
PATH=$PATH:$REPODIR


#ghc install
cd $DIR/..
wget https://downloads.haskell.org/~ghc/8.0.2/ghc-8.0.2-x86_64-deb8-linux.tar.xz
GHCDIR=$DIR/../ghc
mv ghc* "$GHCDIR.tar.gz"
tar xf "$GHCDIR.tar.gz"
rm "$GHCDIR.tar.gz"
mv ghc* "$GHCDIR"
cd $GHCDIR
./configure
sudo make install
cd $DIR

#ghc install
GHCDIR=$DIR/../ghc
unzip "$GHCDIR.zip"
cd $GHCDIR
./configure
sudo make install
cd $DIR


#PolyML config 
cd $DIR/..
git clone https://github.com/polyml/polyml.git
POLYDIR=$DIR/../polyml
cd $POLYDIR
./configure --prefix=/usr
sudo make
sudo make compiler
sudo make install
cd $DIR


#HOL install
cd $DIR/..
git clone git://github.com/HOL-Theorem-Prover/HOL.git
git checkout 7f7650b1f7
HOLDIR=$DIR/../HOL
cd $HOLDIR
sudo poly < tools/smart-configure.sml
sudo bin/build
cd $DIR 
PATH=$PATH:$HOLDIR/bin


#CakeML install
cd $DIR/..
git clone https://github.com/CakeML/cakeml.git
git checkout 59886cd0205
CAKEDIR=$DIR/../cakeml
cd $DIR


#regex filter install
FILT=$DIR/../cakeml-regex-filter
mkdir -p $FILT; mkdir -p $FILT/build
cd $FILT && repo init -u https://github.com/seL4/camkes-manifest.git && repo sync
cd $DIR


#bash config
export PATH
bash



