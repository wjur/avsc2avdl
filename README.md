# avsc2avdl
Avro Schema to Avro IDL converter

Usage: `avsc2avdl [*.avsc]`

The result schema, in Avro IDL format, will be printed to the standard output.

The result schema can be verified by converting the schema back to AVSC format using official Avro Tools. [Download avro-tools-1.10.1.jar](https://downloads.apache.org/avro/avro-1.10.1/java/avro-tools-1.10.1.jar). For example:

```bash
$ ./avsc2avdl original/some.namespace.RecordName.avsc > some.namespace.RecordName.avdl
$ java -jar avro-tools-1.10.1.jar idl2schemata some.namespace.RecordName.avdl
$ diff <(jq . original/some.namespace.RecordName.avsc) <(jq . RecordName.avsc)
```
The diff should be empty.

### Building avsc2avdl

There's a gradle task (`assembleDist`) that creates a `zip` or `tar` distribution in the `build/distributions` folder, which you can then add to your PATH.

Another way is to use the `installDist` task. This copies all the required dependencies as well as a startup script to the `build/install/avsc2avdl` folder, which you can then execute directly:

```bash
$ ./gradlew installDist
$ ./build/install/avsc2avdl/bin/avsc2avdl example.avsc
```
