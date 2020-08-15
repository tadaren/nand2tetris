import java.io.File

fun main(args: Array<String>) {
    val inputFilePath = args[0]
    val inputFile = File(inputFilePath)

    val outputFilePath = args[0].substring(0, args[0].length-4)+".hack"
    val outputFile = File(outputFilePath)
    val writer = outputFile.printWriter()

    val parser1 = Parser(inputFile)
    val symbolTable = SymbolTable()
    var romAddress = 0
    while(parser1.hasMoreCommands()){
        parser1.advance()
        if(parser1.commandType() == CommandType.L_COMMAND){
            val symbol = parser1.symbol()
            symbolTable.addEntry(symbol, romAddress)
        }else{
            romAddress++
        }
    }

    val parser = Parser(inputFile)
    var ramAddress = 16
    while(parser.hasMoreCommands()){
        parser.advance()
        if(parser.commandType() == CommandType.C_COMMAND){
            val code = "111" + comp(parser.comp()) + dest(parser.dest()) + jump(parser.jump())
            writer.println(code)
        }else if(parser.commandType() == CommandType.A_COMMAND){
            val symbol = parser.symbol()
            if(symbol[0] in setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')){
                val code = "0" + symbol.toInt().toString(2).padStart(15, '0')
                writer.println(code)
            }else{
                if(!symbolTable.contains(symbol)){
                    symbolTable.addEntry(symbol, ramAddress)
                    ramAddress++
                }
                val code = "0" + symbolTable.getAddress(symbol).toString(2).padStart(15, '0')
                writer.println(code)
            }
        }
    }
    writer.close()
}