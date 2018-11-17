#ifndef __tb_AADL_CameraManager_Impl_types__H
#define __tb_AADL_CameraManager_Impl_types__H

#include "../../../includes/tb_PROC_HW_types.h"

bool tb_flight_plan_read(SW__Mission_Impl * tb_flight_plan);

bool tb_position_status_dequeue(SW__Coordinate_Impl * tb_position_status);

bool tb_gimbal_command_enqueue(const SW__MISSING_TYPE_Impl * tb_gimbal_command);

#endif // __tb_AADL_CameraManager_Impl_types__H
