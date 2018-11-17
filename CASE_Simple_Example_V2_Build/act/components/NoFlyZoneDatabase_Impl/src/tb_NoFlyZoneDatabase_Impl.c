#include "../includes/tb_NoFlyZoneDatabase_Impl.h"
#include <string.h>
#include <camkes.h>

void tb_entrypoint_tb_NoFlyZoneDatabase_Impl_map(const SW__Map_Impl * in_arg) { }

bool tb_zones_enqueue(const SW__MapArray_Impl * tb_zones){
  bool tb_result = true;
  tb_result &= tb_zones0_enqueue((SW__MapArray_Impl *) tb_zones);
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
