/* testpdmon/components/source/src/source.c */

#include <testdpmon_types.h>
#include <camkes.h>
#include <stdio.h>


/* control thread: keep calling enqueue for thing
 */
int run(void)
{
    thing_t thing1 = { 0, 0, 0, 0 };
    printf("[source] Start sending things to monitor\n");
    for (;;) {
        if ( enq_enqueue( &thing1 ) ) {
            printf("[source] Sent %d\n", thing1.lepht );
            thing1.lepht++;
            thing1.right++;
            thing1.top++;
            thing1.bottom++;
        }
    }
    return 0;
}
