SPLAT: Semantic Properties for Language and Automata Theory
===========================================================

The code in this directory supports the creation of, and correctness
proofs for, filters specified by arithmetic properties extracted from
AADL architectures. A record structure declaration with constraints on
its fields is used to create an encoding/decoding pair that maps a
record of the given type into (and back out of) a sequence of bytes. A
filter for such messages is also created, embodied by a regular
expression, which can be further compiled into a DFA that checks that
the sequence obeys the 