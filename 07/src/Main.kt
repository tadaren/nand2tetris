import java.io.File

fun main(args: Array<String>) {
    val inputFilePath = args[0]
    val inputFile = File(inputFilePath)
    val outputFilePath = inputFilePath.substring(0, inputFilePath.length-3) + ".asm"
    val writer = CodeWriter(File(outputFilePath))
    if(inputFile.isDirectory){
        inputFile.listFiles().forEach {
            writer.setFileName(it.name)
            val parser = Parser(it)
            while(parser.hasMoreCommands()){
                parser.advance()
                when (val commandType = parser.commandType()) {
                    CommandType.C_ARITHMETIC -> {
                        writer.writeArithmetic(parser.arg1())
                    }
                    CommandType.C_PUSH -> {
                        writer.writePushPop(commandType, parser.arg1(), parser.arg2())
                    }
                    CommandType.C_POP -> {
                        writer.writePushPop(commandType, parser.arg1(), parser.arg2())
                    }
                }
            }
        }
    }else{
        val parser = Parser(inputFile)
        while(parser.hasMoreCommands()){
            parser.advance()
            when (val commandType = parser.commandType()) {
                CommandType.C_ARITHMETIC -> {
                    writer.writeArithmetic(parser.arg1())
                }
                CommandType.C_PUSH -> {
                    writer.writePushPop(commandType, parser.arg1(), parser.arg2())
                }
                CommandType.C_POP -> {
                    writer.writePushPop(commandType, parser.arg1(), parser.arg2())
                }
            }
        }
    }

    writer.close()
}