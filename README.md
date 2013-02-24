[![Build Status](https://travis-ci.org/robertrolandorg/cadence.png)](https://travis-ci.org/robertrolandorg/cadence)

# cadence

A way to define an HBase schema and a simple mapping from a hash to / from rows
within an HBase table.

## Usage

Still in progress. The existing tests are probably your best bet to figure out what's
going on.

## Notes

This uses Java 6 as a source and target, since Hadoop / HBase aren't certified to run
properly under Java 7. As soon as Cloudera certifies CDH under Java 7, I will switch
the source/target to 1.7.

This uses the Cloudera CDH3 version of HBase, only because this is the distribution
of HBase I work with on a daily basis.

## License

Copyright © 2013 Robert Roland

Distributed under the MIT License. See LICENSE.
