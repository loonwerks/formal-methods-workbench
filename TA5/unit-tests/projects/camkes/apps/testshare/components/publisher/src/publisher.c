/* testshare/components/publisher/src/publisher.c */

#include <camkes.h>
#include <camkes/dataport.h>
#include <testshare_types.h>
#include <stdio.h>

/* Dataport b1 */
extern thing_t *b1;
/* macro: void b1_acquire() */
/* macro: void b1_release() */


int run(void)
{
    printf("[publisher] starting\n");
    b1->lepht = 1;
    b1->right = 2;
    b1->top   = 3;
    b1_release();               /* release memory fence */
    b1->bottom = 4;
    printf("[publisher] wrote b1={%d,%d,%d,%d}\n",
           b1->lepht, b1->right, b1->top, b1->bottom );
    return 0;
}
