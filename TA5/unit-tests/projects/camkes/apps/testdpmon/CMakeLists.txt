#
cmake_minimum_required(VERSION 3.7.2)

project(testdpmon C)

DeclareCAmkESComponent(source SOURCES components/source/src/source.c
                       INCLUDES include)
DeclareCAmkESComponent(monitor SOURCES components/monitor/src/monitor.c
                       INCLUDES include)
DeclareCAmkESComponent(destination SOURCES components/destination/src/destination.c
                       INCLUDES include)

DeclareCAmkESRootserver(testdpmon.camkes)
