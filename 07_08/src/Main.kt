import java.io.File

fun main(args: Array<String>) {
    val inputFilePath = args[0]
    val inputFile = File(inputFilePath)

    if(inputFile.isDirectory){
        val outputFilePath = "$inputFilePath/${inputFile.name}.asm"
        val writer = CodeWriter(File(outputFilePath))
        writer.writeInit()
        inputFile.listFiles{ file ->
            file.extension == "vm"
        }?.forEach {
            writer.setFileName(it.name)
            parse(it, writer)
        }
        writer.close()
    }else{
        val outputFilePath = inputFilePath.substring(0, inputFilePath.length-3) + ".asm"
        val writer = CodeWriter(File(outputFilePath))
        writer.writeInit()
        parse(inputFile, writer)
        writer.close()
    }
}

fun parse(inputFile: File, writer: CodeWriter){
    val parser = Parser(inputFile)
    while(parser.hasMoreCommands()){
        parser.advance()
        when(val commandType = parser.commandType()){
            CommandType.C_ARITHMETIC -> {
                writer.writeArithmetic(parser.arg1())
            }
            CommandType.C_PUSH -> {
                writer.writePushPop(commandType, parser.arg1(), parser.arg2())
            }
            CommandType.C_POP -> {
                writer.writePushPop(commandType, parser.arg1(), parser.arg2())
            }
            CommandType.C_LABEL -> {
                writer.writeLabel(parser.arg1())
            }
            CommandType.C_GOTO -> {
                writer.writeGoto(parser.arg1())
            }
            CommandType.C_IF -> {
                writer.writeIf(parser.arg1())
            }
            CommandType.C_CALL -> {
                writer.writeCall(parser.arg1(), parser.arg2())
            }
            CommandType.C_FUNCTION -> {
                writer.writeFunction(parser.arg1(), parser.arg2())
            }
            CommandType.C_RETURN -> {
                writer.writeReturn()
            }
        }
    }
}
