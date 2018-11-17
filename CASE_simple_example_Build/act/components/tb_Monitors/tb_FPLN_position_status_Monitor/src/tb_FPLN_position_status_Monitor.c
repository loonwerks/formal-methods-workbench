#ifndef TB_VERIFY
#include <stdio.h>
#endif // TB_VERIFY

#include "../../../../includes/tb_PROC_HW_types.h"
#include "../includes/tb_FPLN_position_status_Monitor.h"

int mon_get_sender_id(void);
int monsig_emit(void);

SW__Coordinate_Impl contents[1];
static uint32_t front = 0;
static uint32_t length = 0;

static bool is_full(void) {
  return length == 1;
}

static bool is_empty(void) {
  return length == 0;
}

bool mon_dequeue(SW__Coordinate_Impl * m) {
  if (mon_get_sender_id() != TB_MONITOR_READ_ACCESS) {
    return false;
  } else if (is_empty()) {
    return false;
  } else {
    *m = contents[front];
    front = (front + 1) % 1;
    length--;
    return true;
  }
}

bool mon_enqueue(const SW__Coordinate_Impl * m) {
  if (mon_get_sender_id() != TB_MONITOR_WRITE_ACCESS) {
    return false;
  } else if (is_full()) {
    return false;
  } else {
    contents[(front + length) % 1] = *m;
    length++;
    monsig_emit();
    return true;
  }
}
