import io.github.wjur.avsc2avdl.api.Avsc2AvdlFacade

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("avsc2avdl [*.avsc]")
        return
    }
    print(Avsc2AvdlFacade.INSTANCE.convert(args[0]))
}
