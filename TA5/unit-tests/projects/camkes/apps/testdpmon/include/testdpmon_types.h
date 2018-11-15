/* testdpmon/include/testdpmon_types.h */
#ifndef __TESTDPMON_TYPES__H__
#define __TESTDPMON_TYPES__H__

#include <stdbool.h>
#include <stdint.h>
#include <stddef.h>

#define MONITOR_READ_ACCESS  111
#define MONITOR_WRITE_ACCESS 222

/* thing_t The message object being spooled
 */
typedef struct thing  {
    uint16_t lepht; 
    uint16_t right; 
    uint16_t top; 
    uint16_t bottom; 
} thing_t;

#endif
