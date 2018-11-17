#ifndef __tb_AADL_WaypointManager_Impl_types__H
#define __tb_AADL_WaypointManager_Impl_types__H

#include "../../../includes/tb_PROC_HW_types.h"

bool tb_flight_plan_read(SW__Mission_Impl * tb_flight_plan);

bool tb_waypoint_enqueue(const SW__MissionWindow_Impl * tb_waypoint);

bool tb_position_status_dequeue(SW__Coordinate_Impl * tb_position_status);

#endif // __tb_AADL_WaypointManager_Impl_types__H
