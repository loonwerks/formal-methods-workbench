#include "../../../../includes/tb_PROC_HW_types.h"
#include "../includes/tb_WPM_flight_plan_Monitor.h"

int mon_get_sender_id(void);
int monsig_emit(void);

static SW__Mission_Impl contents;

bool mon_read(SW__Mission_Impl * m) {
  if (mon_get_sender_id() != TB_MONITOR_READ_ACCESS) {
    return false;
  } else {
    *m = contents;
    return true;
  }
}

bool mon_write(const SW__Mission_Impl * m) {
  if (mon_get_sender_id() != TB_MONITOR_WRITE_ACCESS) {
    return false;
  } else {
    contents = *m;
    monsig_emit();
    return true;
  }
}
