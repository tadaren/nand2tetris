import java.io.File

enum class CommandType{
    A_COMMAND, C_COMMAND, L_COMMAND
}

class Parser(inputFile: File){

    private val lines = inputFile.readLines()
    private var index = 0
    private lateinit var currentLine: String

    fun hasMoreCommands(): Boolean{
        return index < lines.size
    }

    fun advance(){
        while(true) {
            val line = lines[index].replace(Regex("""//.*"""), "").trim()
            index++
            if (line.isNotEmpty()) {
                currentLine = line
                break
            }
        }
    }

    fun commandType(): CommandType{
        val line = currentLine
        return when {
            line[0] == '@' -> {
                CommandType.A_COMMAND
            }
            line[0] == '(' -> {
                CommandType.L_COMMAND
            }
            else -> {
                CommandType.C_COMMAND
            }
        }
    }

    fun symbol(): String{
        val line = currentLine
        return when {
            commandType() == CommandType.A_COMMAND -> {
                line.substring(1)
            }
            commandType() == CommandType.L_COMMAND -> {
                line.substring(1, line.length-1)
            }
            else -> {
                ""
            }
        }
    }

    fun dest(): String{
        val line = currentLine
        return if(line.contains('=')){
            line.split('=')[0].trim()
        }else{
            ""
        }
    }

    fun comp(): String{
        val line = currentLine
        val tmp1 = line.split('=')
        return if(tmp1.size > 1){
            val tmp2 = tmp1[1].split(';')
            tmp2[0].trim()
        }else{
            val tmp2 = tmp1[0].split(';')
            tmp2[0].trim()
        }
    }

    fun jump(): String{
        val line = currentLine
        return if(line.contains(';')){
            line.split(';')[1].trim()
        }else{
            ""
        }
    }
}
