#!/usr/bin/expect -f
# This can be run via tests/run-case.py

set appname [file tail [file dirname [info script]]]

source [file join $::env(SCRIPT_DIR) procs.inc]

## Multiple architectures may be tested:
set x86_defconfig x86_testdpmon_defconfig
# set arm_defconfig arm_testdpmon_defconfig
# set x86_64_defconfig x86_64_testdpmon_defconfig

set override_timeout 120

set testscript {
    wait_for "thing \{1000"
}

source [file join $::env(SCRIPT_DIR) test.inc]
