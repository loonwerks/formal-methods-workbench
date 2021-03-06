/* This file has been autogenerated by Ivory
 * Compiler version  0.1.0.0
 */
#ifndef __MAVLINK_DATA32_MSG_H__
#define __MAVLINK_DATA32_MSG_H__
#ifdef __cplusplus
extern "C" {
#endif
#include "ivory.h"
#include "mavlinkSendModule.h"
#include "mavlink_pack_ivory.h"
struct data32_msg {
    uint8_t data32_type;
    uint8_t len;
    uint8_t data32[32U];
} __attribute__((__packed__));
void mavlink_data32_msg_send(const struct data32_msg* n_var0, uint8_t* n_var1,
                             uint8_t n_var2[80U]);
void mavlink_data32_unpack(struct data32_msg* n_var0, const uint8_t* n_var1);

#ifdef __cplusplus
}
#endif
#endif /* __MAVLINK_DATA32_MSG_H__ */