#
cmake_minimum_required(VERSION 3.7.2)

project(testevent C)

DeclareCAmkESComponent(Emitter SOURCES components/Emitter/src/main.c)
DeclareCAmkESComponent(Consumer SOURCES components/Consumer/src/main.c)

DeclareCAmkESRootserver(testevent.camkes)
