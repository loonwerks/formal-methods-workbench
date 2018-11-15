/* testpdmon/components/monitor/src/monitor.c */

#include <testdpmon_types.h>
#include <camkes.h>
#include <stdio.h>

/* defined automatically */
int mon_get_sender_id(void);
void monsig_emit(void);

/* Size of the queue */
#define QSIZE    10

/* Global State of Queue
 */
thing_t contents[QSIZE];
static uint32_t front = 0;
static uint32_t length = 0;

static bool is_full(void) {
  return length == QSIZE;
}

static bool is_empty(void) {
  return length == 0;
}

bool mon_dequeue( thing_t * m) {
    // if (mon_get_sender_id() != MONITOR_READ_ACCESS) {
    // fprintf(stderr, "[monitor] Error: attempt to dequeue without permission\n");
    // return false;
    // } else
    if (is_empty()) {
        return false;
    }
    else {
        *m = contents[front];
        front = (front + 1) % QSIZE;
        length--;
        return true;
    }
}

bool mon_enqueue(const thing_t * m) {
    //if (mon_get_sender_id() != MONITOR_WRITE_ACCESS) {
    //  fprintf(stderr, "[monitor] Error: attempt to enqueue without permission\n");
    //   return false;
    //}
    // else
    if (is_full()) {
        // fprintf(stderr,"[Monitor] Error: queue is full\n");
        return false;
    }
    else {
        contents[(front + length) % QSIZE] = *m;
        length++;
        monsig_emit();
        return true;
    }
}
