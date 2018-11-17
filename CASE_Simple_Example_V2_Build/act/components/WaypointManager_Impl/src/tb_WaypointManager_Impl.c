#include "../includes/tb_WaypointManager_Impl.h"
#include <string.h>
#include <camkes.h>



bool tb_waypoint_enqueue(const SW__MissionWindow_Impl * tb_waypoint){
  bool tb_result = true;
  tb_result &= tb_waypoint0_enqueue((SW__MissionWindow_Impl *) tb_waypoint);
  return tb_result;
}

void tb_entrypoint_tb_WaypointManager_Impl_position_status(const SW__Coordinate_Impl * in_arg) { }

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
