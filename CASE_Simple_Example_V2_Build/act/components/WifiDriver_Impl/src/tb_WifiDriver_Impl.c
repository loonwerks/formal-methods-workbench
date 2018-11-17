#include "../includes/tb_WifiDriver_Impl.h"
#include <string.h>
#include <camkes.h>

void tb_entrypoint_tb_WifiDriver_Impl_gimbal_command_in(const SW__MISSING_TYPE_Impl * in_arg) { }



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
