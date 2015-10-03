# Introduction
## Reason for existence
CSV is an unreasonably popular data exchange format - I find myself spending a surprising amount of time working with
large datasets provided as CSV files, either because the person putting it together is not technical, or is but cannot
be bothered to write an exporter to a better specified format.

This project is my attempt at streamlining the whole process. I use it myself and it's a good fit for my use cases, but
bear in mind that:

* I don't pretend it fits all use cases (but would love to hear about scenarii where it just didn't work out).
* I can't fix the un-fixable - the lack of standardised column separator or charset being the worst offenders.
  

## Standards compliance
This library is, to the best of my knowledge, [RFC](https://www.ietf.org/rfc/rfc4180.txt) compliant. Should you find out
it's not and violates some rules, please file an issue (ideally with some sample data) and I'll look into it.

Note that while parsing is RFC compliant, it relaxes a few constraints:
* the column separator is customisable (mostly because Microsoft Word uses an environment dependent separator).
* double-quotes *can* occur in fields that are not enclosed with double-quotes.

On top of being RFC compliant, both parsing and serialisation should be compatible with what most software expects.
Should you encounter valid CSV files that cannot be parsed, or software that cannot open a file serialised through
this project, please file an issue with the offending data.