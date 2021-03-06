/* This file has been autogenerated by Ivory
 * Compiler version  0.1.0.0
 */
#ifndef __MAVLINK_SET_QUAD_SWARM_ROLL_PITCH_YAW_THRUST_MSG_H__
#define __MAVLINK_SET_QUAD_SWARM_ROLL_PITCH_YAW_THRUST_MSG_H__
#ifdef __cplusplus
extern "C" {
#endif
#include "ivory.h"
#include "mavlinkSendModule.h"
#include "mavlink_pack_ivory.h"
struct set_quad_swarm_roll_pitch_yaw_thrust_msg {
    uint8_t group;
    uint8_t mode;
    int16_t roll[4U];
    int16_t pitch[4U];
    int16_t yaw[4U];
    uint16_t thrust[4U];
} __attribute__((__packed__));
void mavlink_set_quad_swarm_roll_pitch_yaw_thrust_msg_send(const
                                                           struct set_quad_swarm_roll_pitch_yaw_thrust_msg* n_var0,
                                                           uint8_t* n_var1,
                                                           uint8_t n_var2[80U]);
void mavlink_set_quad_swarm_roll_pitch_yaw_thrust_unpack(struct set_quad_swarm_roll_pitch_yaw_thrust_msg* n_var0,
                                                         const uint8_t* n_var1);

#ifdef __cplusplus
}
#endif
#endif /* __MAVLINK_SET_QUAD_SWARM_ROLL_PITCH_YAW_THRUST_MSG_H__ */