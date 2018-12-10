SPLAT: Semantic Properties for Language and Automata Theory
===========================================================

The code in this directory supports the creation of, and correctness
proofs for, filters specified by arithmetic properties extracted from
AADL architectures. A record structure declaration with constraints on
its fields specifies an encoding/decoding pair that maps a record of
the given type into (and back out of) a sequence of bytes. A filter
for such messages is also created, embodied by a regular expression,
which can be further compiled into a DFA that checks that the sequence
obeys the constraints.

Typing

   Holmake

builds an executable named `splat`, which currently takes a json file
representing an AADL architecture. An invocation

   ./splat foo.json

will extract filter properties from the architecture, create
encoders/decoders from the record declaration and accompanying
constraints, create a regexp from the filter properties, and show that
the regexp implements the filter properties. It leaves a HOL theory
fooTheory.{sig,sml} capturing the formalization.

