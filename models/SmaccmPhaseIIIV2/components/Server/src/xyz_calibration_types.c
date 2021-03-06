/* This file has been autogenerated by Ivory
 * Compiler version  0.1.0.3
 */
#include "xyz_calibration_types.h"

void xyz_calibration_get_le(const uint8_t *n_var0, uint32_t n_var1, struct xyz_calibration *n_var2)
{
    ibool_get_le(n_var0, n_var1, &n_var2->valid);
    ivory_serialize_unpack_float_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 1U), &n_var2->progress);
    xyz_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 5U), &n_var2->bias);
    xyz_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 17U), &n_var2->scale);
    time_micros_t_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 29U), &n_var2->time);
}

void xyz_calibration_get_be(const uint8_t *n_var0, uint32_t n_var1, struct xyz_calibration *n_var2)
{
    ibool_get_be(n_var0, n_var1, &n_var2->valid);
    ivory_serialize_unpack_float_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 1U), &n_var2->progress);
    xyz_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 5U), &n_var2->bias);
    xyz_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 17U), &n_var2->scale);
    time_micros_t_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 29U), &n_var2->time);
}

void xyz_calibration_set_le(uint8_t *n_var0, uint32_t n_var1, const struct xyz_calibration *n_var2)
{
    ibool_set_le(n_var0, n_var1, &n_var2->valid);
    ivory_serialize_pack_float_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 1U), &n_var2->progress);
    xyz_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 5U), &n_var2->bias);
    xyz_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 17U), &n_var2->scale);
    time_micros_t_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 29U), &n_var2->time);
}

void xyz_calibration_set_be(uint8_t *n_var0, uint32_t n_var1, const struct xyz_calibration *n_var2)
{
    ibool_set_be(n_var0, n_var1, &n_var2->valid);
    ivory_serialize_pack_float_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 1U), &n_var2->progress);
    xyz_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 5U), &n_var2->bias);
    xyz_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 17U), &n_var2->scale);
    time_micros_t_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 29U), &n_var2->time);
}