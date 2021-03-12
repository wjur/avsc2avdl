# avsc2avdl
Avro Schema to Avro IDL converter

Usage: avsc2avdl [*.avsc]

The result schema, in Avro IDL format, will be printed to the standard output.

The result schema can be verified by converting the schema back to AVSC format using official Avro Tools. [Download avro-tools-1.10.1.jar](https://downloads.apache.org/avro/avro-1.10.1/java/avro-tools-1.10.1.jar).

```bash
$ ./avsc2avdl inputschema.avsc > schema.avdl
$ java -jar avro-tools-1.10.1.jar idl schema.avdl > recreated.avsc
$ diff inputschema.avsc recreated.avsc
```
