#include "../includes/tb_UARTDriver_Impl.h"
#include <string.h>
#include <camkes.h>

void tb_entrypoint_tb_UARTDriver_Impl_position_status_in(const SW__Coordinate_Impl * in_arg) { }



bool tb_position_status_out_enqueue(const SW__Coordinate_Impl * tb_position_status_out){
  bool tb_result = true;
  tb_result &= tb_position_status_out0_enqueue((SW__Coordinate_Impl *) tb_position_status_out);
  tb_result &= tb_position_status_out1_enqueue((SW__Coordinate_Impl *) tb_position_status_out);
  tb_result &= tb_position_status_out2_enqueue((SW__Coordinate_Impl *) tb_position_status_out);
  return tb_result;
}

void tb_entrypoint_tb_UARTDriver_Impl_waypoint_in(const SW__MissionWindow_Impl * in_arg) { }

void pre_init(void) { }

int run(void) {
  // Initial lock to await dispatch input.
  MUTEXOP(tb_dispatch_sem_wait())
  for(;;) {
    MUTEXOP(tb_dispatch_sem_wait())
    // Drain the queues
  }
  return 0;
}
