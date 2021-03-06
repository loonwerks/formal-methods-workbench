#ifndef __TB_AADL_CAN_Framing_types__H
#define __TB_AADL_CAN_Framing_types__H

#include <tb_smaccmpilot_tk1_types.h>

/**************************************************************************
  Copyright (c) 2013-2016 Rockwell Collins and the University of
  Minnesota. Developed with the sponsorship of the Defense Advanced
  Research Projects Agency (DARPA).

  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this data, including any software or models
  in source or binary form, as well as any drawings,
  specifications, and documentation (collectively "the Data"), to
  deal in the Data without restriction, including without
  limitation the rights to use, copy, modify, merge, publish,
  distribute, sublicense, and/or sell copies of the Data, and to
  permit persons to whom the Data is furnished to do so, subject to
  the following conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Data.

  THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS, SPONSORS,
  DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
  CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE DATA OR THE USE OR OTHER DEALINGS IN THE
  DATA.
 **************************************************************************/

/**************************************************************************

  ***AUTOGENERATED CODE: DO NOT MODIFY***

This header section contains the AADL gluecode interfaces used by the
client for the thread implementations.

 **************************************************************************/


// user entrypoints for periodic dispatch

void component_entry(const int64_t * periodic_dispatcher);


 void component_init(const int64_t *arg);
 // reader prototype for tb_server2self
bool tb_server2self_dequeue(tb_SMACCM_DATA__GIDL_container * tb_server2self);
 bool tb_self2server_enqueue(const SMACCM_DATA__GIDL * tb_self2server);
 // reader prototype for tb_can2self_frame
bool tb_can2self_frame_dequeue(SMACCM_DATA__CAN_Frame_i * tb_can2self_frame);
 // reader prototype for tb_can2self_status
bool tb_can2self_status_dequeue(bool * tb_can2self_status);
 bool tb_self2can_enqueue(const SMACCM_DATA__CAN_Frame_i * tb_self2can);
 


#endif // __TB_AADL_CAN_Framing_types__H
