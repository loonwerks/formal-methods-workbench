/* testpdmon/components/destination/src/destination.c */

#include <testdpmon_types.h>
#include <camkes.h>
#include <stdio.h>


/* Handle monsig notification: there is QueuedData
 */
void handler(void* ignore __attribute__ ((unused)))
{
    thing_t thing = { -1, -1, -1, -1 };

    /* keep dequeuing until no more things can be had
     */
    while ( deq_dequeue( &thing ) ) {
        printf("[destination] thing {%d,%d,%d,%d}\n",
               thing.lepht, thing.right, thing.top, thing.bottom);
    }
    if ( notif_reg_callback( &handler, NULL ) ) {
        printf("[destination] Error: callback registration failed.");
    }
}


/* initialize notification interface 
 */
void notif__init(void)
{
    if ( notif_reg_callback( &handler, NULL ) ) {
        printf("[destination] Error: initial callback registration failed.");
    } else {
        printf("[destination] Waiting for notification QueuedData...\n");
    }
}


