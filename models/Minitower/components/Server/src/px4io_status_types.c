/* This file has been autogenerated by Ivory
 * Compiler version  0.1.0.4
 */
#include "px4io_status_types.h"

void px4io_status_get_le(const uint8_t *n_var0, uint32_t n_var1, struct px4io_status *n_var2)
{
    ibool_get_le(n_var0, n_var1, &n_var2->safety_off);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 1U), &n_var2->failsafe);
    ibool_get_le(n_var0, (uint32_t) ((uint32_t) 2U + n_var1), &n_var2->init_ok);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 3U), &n_var2->arm_sync);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 4U), &n_var2->mixer_ok);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 5U), &n_var2->raw_pwm);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 6U), &n_var2->fmu_ok);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 7U), &n_var2->rc_sbus);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 8U), &n_var2->rc_dsm);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 9U), &n_var2->rc_ppm);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 10U), &n_var2->rc_ok);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 11U), &n_var2->outputs_armed);
    ibool_get_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 12U), &n_var2->override);
}

void px4io_status_get_be(const uint8_t *n_var0, uint32_t n_var1, struct px4io_status *n_var2)
{
    ibool_get_be(n_var0, n_var1, &n_var2->safety_off);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 1U), &n_var2->failsafe);
    ibool_get_be(n_var0, (uint32_t) ((uint32_t) 2U + n_var1), &n_var2->init_ok);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 3U), &n_var2->arm_sync);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 4U), &n_var2->mixer_ok);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 5U), &n_var2->raw_pwm);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 6U), &n_var2->fmu_ok);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 7U), &n_var2->rc_sbus);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 8U), &n_var2->rc_dsm);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 9U), &n_var2->rc_ppm);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 10U), &n_var2->rc_ok);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 11U), &n_var2->outputs_armed);
    ibool_get_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 12U), &n_var2->override);
}

void px4io_status_set_le(uint8_t *n_var0, uint32_t n_var1, const struct px4io_status *n_var2)
{
    ibool_set_le(n_var0, n_var1, &n_var2->safety_off);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 1U), &n_var2->failsafe);
    ibool_set_le(n_var0, (uint32_t) ((uint32_t) 2U + n_var1), &n_var2->init_ok);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 3U), &n_var2->arm_sync);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 4U), &n_var2->mixer_ok);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 5U), &n_var2->raw_pwm);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 6U), &n_var2->fmu_ok);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 7U), &n_var2->rc_sbus);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 8U), &n_var2->rc_dsm);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 9U), &n_var2->rc_ppm);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 10U), &n_var2->rc_ok);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 11U), &n_var2->outputs_armed);
    ibool_set_le(n_var0, (uint32_t) (n_var1 + (uint32_t) 12U), &n_var2->override);
}

void px4io_status_set_be(uint8_t *n_var0, uint32_t n_var1, const struct px4io_status *n_var2)
{
    ibool_set_be(n_var0, n_var1, &n_var2->safety_off);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 1U), &n_var2->failsafe);
    ibool_set_be(n_var0, (uint32_t) ((uint32_t) 2U + n_var1), &n_var2->init_ok);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 3U), &n_var2->arm_sync);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 4U), &n_var2->mixer_ok);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 5U), &n_var2->raw_pwm);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 6U), &n_var2->fmu_ok);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 7U), &n_var2->rc_sbus);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 8U), &n_var2->rc_dsm);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 9U), &n_var2->rc_ppm);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 10U), &n_var2->rc_ok);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 11U), &n_var2->outputs_armed);
    ibool_set_be(n_var0, (uint32_t) (n_var1 + (uint32_t) 12U), &n_var2->override);
}