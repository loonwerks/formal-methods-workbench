/* apps/testevent/components/Consumer/src/main.c */

/* Similar to the "helloevent" example in CAmkES doc,
 * tidied up a bit.
 */

#include <camkes.h>
#include <stdio.h>

/* Callback handles events
 */
static void handler(void* ignore __attribute__ ((unused)))
{
  static int fired = 0;

  printf("[Consumer] Callback fired\n");
  if (!fired) {
    fired = 1;
    if (s_reg_callback(&handler,NULL)) {
        printf("[Consumer] Error: Callback registration failed");
    }
  }
}

/* Active component will try all 3 methods for handling events:
 * poll, wait, register callback
 */
int run(void)
{
  printf("[Consumer] Registering callback...\n");
  if ( s_reg_callback(&handler,NULL) ) {
      printf("[Consumer] Error: Initial callback registration failed");
  }

  printf("[Consumer] Polling...\n");
  if (s_poll()) {
    printf("[Consumer] Poll found an event\n");
  } else {
    printf("[Consumer] We didn't find an event\n");
  }

  printf("[Consumer] Waiting for an event\n");
  s_wait();
  printf("[Consumer] Unblocked by an event\n");
  printf("[Consumer] Test finished.\n");
  return 0;
}

