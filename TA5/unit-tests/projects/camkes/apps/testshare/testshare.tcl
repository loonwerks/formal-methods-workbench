#!/usr/bin/expect -f
# Module: testshare.tcl
# This can be run via tests/run-case.py

set appname [file tail [file dirname [info script]]]

source [file join $::env(SCRIPT_DIR) procs.inc]

## Multiple architectures may be tested:
set x86_defconfig x86_testshare_defconfig
# set arm_defconfig arm_testshare_defconfig
# set x86_64_defconfig x86_64_testshare_defconfig

set override_timeout 120

set testscript {
    wait_for ".*Test finished"
}

source [file join $::env(SCRIPT_DIR) test.inc]
