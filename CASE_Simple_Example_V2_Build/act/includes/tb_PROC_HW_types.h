#ifndef __TB_AADL_tb_PROC_HW_types__H
#define __TB_AADL_tb_PROC_HW_types__H

#include <stdbool.h>
#include <stdint.h>

#ifndef TB_VERIFY
#include <stddef.h>
#endif // TB_VERIFY

#define __TB_OS_CAMKES__
#define TB_MONITOR_READ_ACCESS 111
#define TB_MONITOR_WRITE_ACCESS 222

#ifndef TB_VERIFY
#define MUTEXOP(OP)\
if((OP) != 0) {\
  fprintf(stderr,"Operation " #OP " failed in %s at %d.\n",__FILE__,__LINE__);\
  *((int*)0)=0xdeadbeef;\
}
#else
#define MUTEXOP(OP) OP
#endif // TB_VERIFY
#ifndef TB_VERIFY
#define CALLBACKOP(OP)\
if((OP) != 0) {\
  fprintf(stderr,"Operation " #OP " failed in %s at %d.\n",__FILE__,__LINE__);\
  *((int*)0)=0xdeadbeef;\
}
#else
#define CALLBACKOP(OP) OP
#endif // TB_VERIFY

typedef
  struct SW__FlightPattern_Impl {
    int32_t enumAsInt;
  } SW__FlightPattern_Impl;

typedef
  struct SW__Coordinate_Impl {
    int32_t lat;
    int32_t longitude;
    int32_t alt;
  } SW__Coordinate_Impl;

typedef
  struct SW__Map_Impl {
    SW__Coordinate_Impl wp1;
    SW__Coordinate_Impl wp2;
    SW__Coordinate_Impl wp3;
    SW__Coordinate_Impl wp4;
  } SW__Map_Impl;

typedef
  struct SW__Command_Impl {
    SW__Map_Impl Map;
    SW__FlightPattern_Impl Pattern;
    bool HMAC;
  } SW__Command_Impl;

typedef
  struct SW__MISSING_TYPE_Impl {
    int32_t MISSING;
  } SW__MISSING_TYPE_Impl;

typedef
  struct SW__MapArray_Impl {
    SW__Map_Impl map1;
    SW__Map_Impl map2;
    SW__Map_Impl map3;
    SW__Map_Impl map4;
    SW__Map_Impl map5;
  } SW__MapArray_Impl;

typedef
  struct SW__MissionWindow_Impl {
    SW__Coordinate_Impl wp1;
    SW__Coordinate_Impl wp2;
    SW__Coordinate_Impl wp3;
    SW__Coordinate_Impl wp4;
    bool crc;
  } SW__MissionWindow_Impl;

typedef
  struct SW__Mission_Impl {
    SW__Coordinate_Impl wp1;
    SW__Coordinate_Impl wp2;
    SW__Coordinate_Impl wp3;
    SW__Coordinate_Impl wp4;
    SW__Coordinate_Impl wp5;
    SW__Coordinate_Impl wp6;
    SW__Coordinate_Impl wp7;
    SW__Coordinate_Impl wp8;
    SW__Coordinate_Impl wp9;
    SW__Coordinate_Impl wp10;
  } SW__Mission_Impl;

#endif // __TB_AADL_tb_PROC_HW_types__H
