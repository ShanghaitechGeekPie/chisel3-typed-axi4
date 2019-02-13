Chisel3 Typed-AXI4
=======================

## What it is?

A group of typed definition of AXI4 in Chisel3.

## Disadvantages?

- Some bad names.
- Over use of `nat`.
- Only definition of the interface family, i.e. no auxiliary functions so far.

## TODO

- Rename the util classes.
- Maybe replace `nat` with more light-weighted implementation.
- Add testbenches.
- Add some auxiliary functions.
- Rename signals to make it easier to corporate with ISE, Vivado, etc.'s IP templates.
