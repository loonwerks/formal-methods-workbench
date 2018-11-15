#
cmake_minimum_required(VERSION 3.7.2)

project(testshare C)

DeclareCAmkESComponent(publisher SOURCES components/publisher/src/publisher.c
  INCLUDES include)

DeclareCAmkESComponent(subscriber SOURCES components/subscriber/src/subscriber.c
  INCLUDES include)

DeclareCAmkESRootserver(testshare.camkes)
