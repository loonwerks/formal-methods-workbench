/* apps/testevent/components/Emitter/src/main.c */

/* Event generator--sends an endless stream of events.
 */
#include <camkes.h>

/* Control thread, active component.
 */
int run(void)
{
  while (1) {
    e_emit();
  }
  return 0;
}
