import java.io.File

enum class TokenType{
    KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
}

enum class KeyWord{
    CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR, STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS
}

class JackTokenizer(inputFile: File){

    private val text = inputFile.readText().replace(Regex("""(//.*)|(/\*(.|(\n|\r|(\r\n)))*?\*/)"""), "")
    private var position = 0
    private lateinit var currentTokenType: TokenType
    private lateinit var currentKeyWord: KeyWord
    private var symbol = ' '
    private var intVal = -1
    private lateinit var identifier: String
    private lateinit var stringVal: String

    init {
        skipWhiteSpace()
    }

    fun hasMoreTokens(): Boolean{
        return text.length > position
    }

    private fun skipWhiteSpace(){
        while(text.length > position && text[position] in setOf(' ', '\t', '\r', '\n')) {
            position++
        }
    }

    fun advance(){
        var token = ""
        val char = text[position++]
        if(char.isDigit()) {
            token += char
            var digit = text[position]
            while (digit.isDigit()) {
                token += digit
                digit = text[++position]
            }
            currentTokenType = TokenType.INT_CONST
            intVal = token.toInt()
        }else if(char == '"'){
            var string = ""
            var c = text[position++]
            while(c != '"'){
                string += c
                c = text[position++]
            }
            currentTokenType = TokenType.STRING_CONST
            stringVal = string
        }else if(char in setOf('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~')){
            currentTokenType = TokenType.SYMBOL
            symbol = char
        }else{
            token += char
            var c = text[position]
            while(c !in setOf(' ', '\t', '\r', '\n') && c !in setOf('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~')){
                token += c
                c = text[++position]
            }
            if(token in setOf("class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return")){
                currentTokenType = TokenType.KEYWORD
                currentKeyWord = when(token){
                    "class" -> KeyWord.CLASS
                    "constructor" -> KeyWord.CONSTRUCTOR
                    "function" -> KeyWord.FUNCTION
                    "method" -> KeyWord.METHOD
                    "field" -> KeyWord.FIELD
                    "static" -> KeyWord.STATIC
                    "var" -> KeyWord.VAR
                    "int" -> KeyWord.INT
                    "char" -> KeyWord.CHAR
                    "boolean" -> KeyWord.BOOLEAN
                    "void" -> KeyWord.VOID
                    "true" -> KeyWord.TRUE
                    "false" -> KeyWord.FALSE
                    "null" -> KeyWord.NULL
                    "this" -> KeyWord.THIS
                    "let" -> KeyWord.LET
                    "do" -> KeyWord.DO
                    "if" -> KeyWord.IF
                    "else" -> KeyWord.ELSE
                    "while" -> KeyWord.WHILE
                    "return" -> KeyWord.RETURN
                    else -> KeyWord.CLASS       // 多分来ないはず
                }
            }else{
                currentTokenType = TokenType.IDENTIFIER
                identifier = token
            }
        }
        skipWhiteSpace()
    }

    fun tokenType(): TokenType{
        return currentTokenType
    }

    fun keyWord(): KeyWord{
        return currentKeyWord
    }

    fun symbol(): Char{
        return symbol
    }

    fun identifier(): String{
        return identifier
    }

    fun intVal(): Int{
        return intVal
    }

    fun stringVal(): String{
        return stringVal
    }
}