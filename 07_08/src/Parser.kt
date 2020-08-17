import java.io.File

enum class CommandType{
    C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_RETURN, C_CALL
}

class Parser (inputFile: File){
    private val lines = inputFile.readLines()
    private var index = 0
    private lateinit var currentLine: String

    fun hasMoreCommands(): Boolean{
        return lines.size > index
    }

    fun advance(){
        while(true) {
            val line = lines[index].replace(Regex("""//.*"""), "").trim()
            index++
            if(line.isNotEmpty()){
                currentLine = line
                break
            }
        }
    }

    fun commandType(): CommandType{
        val lineSegment = currentLine.split(Regex("""\s+"""))
        return when (lineSegment[0]) {
            in setOf("add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not") -> CommandType.C_ARITHMETIC
            "pop" -> CommandType.C_POP
            "push" -> CommandType.C_PUSH
            "label" -> CommandType.C_LABEL
            "goto" -> CommandType.C_GOTO
            "if-goto" -> CommandType.C_IF
            "function" -> CommandType.C_FUNCTION
            "call" -> CommandType.C_CALL
            "return" -> CommandType.C_RETURN
            else -> CommandType.C_RETURN
        }
    }

    fun arg1(): String{
        val commandType = commandType()
        val lineSegment = currentLine.split(Regex("""\s+"""))
        return if(commandType == CommandType.C_ARITHMETIC){
            lineSegment[0]
        }else{
            lineSegment[1]
        }
    }

    fun arg2(): Int{
        val lineSegment = currentLine.split(Regex("""\s+"""))
        return lineSegment[2].toInt()
    }
}