#include "../includes/tb_RadioDriver_Impl.h"
#include <string.h>
#include <camkes.h>

void tb_entrypoint_tb_RadioDriver_Impl_recv_map_in(const SW__Command_Impl * in_arg) { }



void tb_entrypoint_tb_RadioDriver_Impl_send_status_in(const SW__Coordinate_Impl * in_arg) { }

bool tb_recv_map_out_enqueue(const SW__Command_Impl * tb_recv_map_out){
  bool tb_result = true;
  tb_result &= tb_recv_map_out0_enqueue((SW__Command_Impl *) tb_recv_map_out);
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
