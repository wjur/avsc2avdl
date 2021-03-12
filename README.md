# avsc2avdl
Avro Schema to Avro IDL converter

Usage: avsc2avdl [*.avsc]

The result schema, in Avro IDL format, will be printed to the standard output.

The result schema can be verified by converting the schema back to AVSC format using official Avro Tools. [Download avro-tools-1.10.1.jar](https://downloads.apache.org/avro/avro-1.10.1/java/avro-tools-1.10.1.jar). For example:

```bash
$ ./avsc2avdl original/some.namespace.RecordName.avsc > some.namespace.RecordName.avdl
$ java -jar avro-tools-1.10.1.jar idl2schemata some.namespace.RecordName.avdl
$ diff <(jq . original/some.namespace.RecordName.avsc) <(jq . RecordName.avsc)
```
The diff should be empty.
