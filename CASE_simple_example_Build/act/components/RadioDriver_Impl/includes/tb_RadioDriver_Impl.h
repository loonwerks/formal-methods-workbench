#ifndef __tb_AADL_RadioDriver_Impl_types__H
#define __tb_AADL_RadioDriver_Impl_types__H

#include "../../../includes/tb_PROC_HW_types.h"

bool tb_recv_map_in_dequeue(SW__Command_Impl * tb_recv_map_in);

bool tb_send_status_out_enqueue(const SW__Coordinate_Impl * tb_send_status_out);

bool tb_send_status_in_dequeue(SW__Coordinate_Impl * tb_send_status_in);

bool tb_recv_map_out_enqueue(const SW__Command_Impl * tb_recv_map_out);

#endif // __tb_AADL_RadioDriver_Impl_types__H
