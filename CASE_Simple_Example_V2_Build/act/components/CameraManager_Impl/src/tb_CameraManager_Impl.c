#include "../includes/tb_CameraManager_Impl.h"
#include <string.h>
#include <camkes.h>



void tb_entrypoint_tb_CameraManager_Impl_position_status(const SW__Coordinate_Impl * in_arg) { }

bool tb_gimbal_command_enqueue(const SW__MISSING_TYPE_Impl * tb_gimbal_command){
  bool tb_result = true;
  tb_result &= tb_gimbal_command0_enqueue((SW__MISSING_TYPE_Impl *) tb_gimbal_command);
  return tb_result;
}

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
