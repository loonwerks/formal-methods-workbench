/* This file has been autogenerated by Ivory
 * Compiler version  0.1.0.3
 */
#ifndef __SEQUENCE_NUMBERED_TRISTATE_TYPES_H__
#define __SEQUENCE_NUMBERED_TRISTATE_TYPES_H__
#ifdef __cplusplus
extern "C" {
#endif
#include "ivory.h"
#include "ivory_serialize.h"
#include "sequence_num_types.h"
#include "tristate_types.h"
typedef struct sequence_numbered_tristate { uint32_t seqnum;
                                            uint8_t val;
} sequence_numbered_tristate;
void sequence_numbered_tristate_get_le(const uint8_t *n_var0, uint32_t n_var1, struct sequence_numbered_tristate *n_var2);
void sequence_numbered_tristate_get_be(const uint8_t *n_var0, uint32_t n_var1, struct sequence_numbered_tristate *n_var2);
void sequence_numbered_tristate_set_le(uint8_t *n_var0, uint32_t n_var1, const struct sequence_numbered_tristate *n_var2);
void sequence_numbered_tristate_set_be(uint8_t *n_var0, uint32_t n_var1, const struct sequence_numbered_tristate *n_var2);

#ifdef __cplusplus
}
#endif
#endif /* __SEQUENCE_NUMBERED_TRISTATE_TYPES_H__ */