#ifndef __tb_AADL_UARTDriver_Impl_types__H
#define __tb_AADL_UARTDriver_Impl_types__H

#include "../../../includes/tb_PROC_HW_types.h"

bool tb_position_status_in_dequeue(SW__Coordinate_Impl * tb_position_status_in);

bool tb_waypoint_out_enqueue(const SW__MissionWindow_Impl * tb_waypoint_out);

bool tb_position_status_out_enqueue(const SW__Coordinate_Impl * tb_position_status_out);

bool tb_waypoint_in_dequeue(SW__MissionWindow_Impl * tb_waypoint_in);

#endif // __tb_AADL_UARTDriver_Impl_types__H
