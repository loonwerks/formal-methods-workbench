/* testshare/components/subscriber/src/subscriber.c */

#include <camkes.h>
#include <camkes/dataport.h>
#include <testshare_types.h>
#include <stdio.h>

/* Dataport b2 */
extern thing_t *b2;
/* macro: void b2_acquire() */
/* macro: void b2_release() */


int run(void)
{
    printf("[subscriber] starting--poll for nonzero thing_t\n");

    while (1) {
        b2_acquire();           /* acquire memory fence */
        if (b2->bottom) {
            printf("[subscriber] b2={%d,%d,%d,%d}\n",
                   b2->lepht, b2->right, b2->top, b2->bottom );
            break;
        }
    }
    puts("[subscriber] Test finished");
    return 0;
}
