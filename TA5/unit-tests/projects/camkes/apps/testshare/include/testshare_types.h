/* testdpmon/include/testshare_types.h */
#ifndef __TESTSHARE_TYPES__H__
#define __TESTSHARE_TYPES__H__

#include <stdint.h>
#include <stddef.h>

/* thing_t The message object being spooled
 */
typedef struct thing  {
    uint16_t lepht; 
    uint16_t right; 
    uint16_t top; 
    uint16_t bottom; 
} thing_t;

#endif
