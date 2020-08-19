import java.io.File

fun Char.xmlEscape(): String{
    return when(this){
        '<' -> "&lt;"
        '>' -> "&gt;"
        '&' -> "&amp;"
        else -> this.toString()
    }
}

fun String.xmlEscape(): String{
    return this.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}

fun main(args: Array<String>) {
    val inputFilePath = args[0]
    val inputFile = File(inputFilePath)

    if(inputFile.isDirectory){
        inputFile.listFiles{ file -> file.extension == "jack"}?.forEach {
            val outputFilePath = "$inputFilePath/${it.name.substringBeforeLast('.')}_.xml"
            CompilationEngine(it, File(outputFilePath))
        }
    }else{
        val outputFilePath = inputFilePath.substringBeforeLast('.') + "_.xml"
        val outputFile = File(outputFilePath)

        CompilationEngine(inputFile, outputFile)
    }
//    val writer = outputFile.printWriter()
//    writer.println("<tokens>")
//
//    val tokenizer = JackTokenizer(inputFile)
//    while(tokenizer.hasMoreTokens()){
//        tokenizer.advance()
//        val tokenType = tokenizer.tokenType()
//        when(tokenType){
//            TokenType.KEYWORD -> {
//                writer.println("<keyword> ${tokenizer.keyWord().toString().toLowerCase()} </keyword>")
//                println("<keyword> ${tokenizer.keyWord()} </keyword>")
//            }
//            TokenType.SYMBOL -> {
//                writer.println("<symbol> ${tokenizer.symbol().xmlEscape()} </symbol>")
//                println("<symbol> ${tokenizer.symbol()} </symbol>")
//            }
//            TokenType.IDENTIFIER -> {
//                writer.println("<identifier> ${tokenizer.identifier()} </identifier>")
//                println("<identifier> ${tokenizer.identifier()} </identifier>")
//            }
//            TokenType.INT_CONST -> {
//                writer.println("<integerConstant> ${tokenizer.intVal()} </integerConstant>")
//                println("<integerConstant> ${tokenizer.intVal()} </integerConstant>")
//            }
//            TokenType.STRING_CONST -> {
//                writer.println("<stringConstant> ${tokenizer.stringVal()} </stringConstant>")
//                println("<stringConstant> ${tokenizer.stringVal()} </stringConstant>")
//            }
//        }
//    }
//    writer.println("</tokens>")
//    writer.close()
}