Chisel3 Typed-AXI4
=======================

## What it is?

A group of typed definition of a subset of AXI4 written in Chisel3.

## Disadvantages?

- Some bad names.
- Only definition of the interface family, i.e. no auxiliary functions so far.

## Advantages?

- Clean output, i.e.optional wires won't appear in the generated codes unless declared explicitly.
- The existence and width of optional wires is shown in type.

## TODO

- Make data field generic.
- Add testbenches.
- Add some auxiliary functions.
- Rename signals to make it easier to corporate with vendors' IP templates.
