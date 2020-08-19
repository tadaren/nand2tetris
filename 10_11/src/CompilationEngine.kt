import java.io.File

class CompilationEngine(inputFile: File, outputFile: File){

    private val tokenizer = JackTokenizer(inputFile)
    private val writer = outputFile.printWriter()

    init {
        tokenizer.advance()
        if(tokenizer.tokenType() != TokenType.KEYWORD || tokenizer.keyWord() != KeyWord.CLASS){
            throw CompileError("""expect keyword "class" """)
        }
        compileClass()
        writer.close()
    }

    private fun compileTerminal(){
        when(tokenizer.tokenType()){
            TokenType.KEYWORD -> {
                writer.println("<keyword> ${tokenizer.keyWord().toString().toLowerCase()} </keyword>")
            }
            TokenType.SYMBOL -> {
                writer.println("<symbol> ${tokenizer.symbol().xmlEscape()} </symbol>")
            }
            TokenType.IDENTIFIER -> {
                writer.println("<identifier> ${tokenizer.identifier()} </identifier>")
            }
            TokenType.INT_CONST -> {
                writer.println("<integerConstant> ${tokenizer.intVal()} </integerConstant>")
            }
            TokenType.STRING_CONST -> {
                writer.println("<stringConstant> ${tokenizer.stringVal()} </stringConstant>")
            }
        }
        if(tokenizer.hasMoreTokens()) {
            tokenizer.advance()
        }
    }

    fun compileClass(){
        writer.println("<class>")
        compileTerminal()

        if(tokenizer.tokenType() != TokenType.IDENTIFIER){
            throw CompileError("""expect identifier""")
        }
        compileTerminal()

        if(tokenizer.tokenType() != TokenType.SYMBOL){
            throw CompileError("""expect "{"""")
        }
        compileTerminal()

        while(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() in setOf(KeyWord.STATIC, KeyWord.FIELD)){
            compileClassVarDec()
        }

        while(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() in setOf(KeyWord.CONSTRUCTOR, KeyWord.FUNCTION, KeyWord.METHOD)){
            compileSubroutine()
        }

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '}'){
            throw CompileError("""expect "}"""")
        }
        compileTerminal()

        writer.println("</class>")
    }

    private fun isType(): Boolean{
        return tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() in setOf(KeyWord.INT, KeyWord.CHAR, KeyWord.BOOLEAN) || tokenizer.tokenType() == TokenType.IDENTIFIER
    }

    fun compileClassVarDec(){
        writer.println("<classVarDec>")

        compileTerminal()

        if(!isType()){
            throw CompileError("""expect type "int" | "char" | "boolean" | className """)
        }
        compileTerminal()

        if(tokenizer.tokenType() != TokenType.IDENTIFIER){
            throw CompileError("""expect varName""")
        }
        compileTerminal()

        while(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ','){
            compileTerminal()
            if(tokenizer.tokenType() != TokenType.IDENTIFIER){
                throw CompileError("""expect varName""")
            }
            compileTerminal()
        }

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ';'){
            throw CompileError("""expect ";"""")
        }
        compileTerminal()

        writer.println("</classVarDec>")
    }

    fun compileSubroutine(){
        writer.println("<subroutineDec>")

        compileTerminal()

        if(!(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.VOID) && !isType()){
            throw CompileError("expect type")
        }
        compileTerminal()

        if(tokenizer.tokenType() != TokenType.IDENTIFIER) {
            throw CompileError("expect subroutineName")
        }
        compileTerminal()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '('){
            throw CompileError("""expect "("""")
        }
        compileTerminal()

        compileParameterList()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ')'){
            throw CompileError("""expect ")"""")
        }
        compileTerminal()

        writer.println("<subroutineBody>")

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '{'){
            throw CompileError("""expect "{"""")
        }
        compileTerminal()

        while(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.VAR){
            compileVarDec()
        }

        compileStatements()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '}'){
            throw CompileError("""expect "}"""")
        }
        compileTerminal()

        writer.println("</subroutineBody>")
        writer.println("</subroutineDec>")
    }

    fun compileParameterList(){
        writer.println("<parameterList>")
        if(isType()){
            compileTerminal()

            if(tokenizer.tokenType() != TokenType.IDENTIFIER){
                throw CompileError("""expect varName""")
            }
            compileTerminal()

            while(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ','){
                compileTerminal()

                if(!isType()){
                    throw CompileError("expect type")
                }
                compileTerminal()

                if(tokenizer.tokenType() != TokenType.IDENTIFIER){
                    throw CompileError("""expect varName""")
                }
                compileTerminal()
            }
        }
        writer.println("</parameterList>")
    }

    fun compileVarDec(){
        writer.println("<varDec>")
        compileTerminal()

        if(!isType()){
            throw CompileError("""expect type "int" | "char" | "boolean" | className """)
        }
        compileTerminal()

        if(tokenizer.tokenType() != TokenType.IDENTIFIER){
            throw CompileError("""expect varName""")
        }
        compileTerminal()

        while(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ','){
            compileTerminal()
            if(tokenizer.tokenType() != TokenType.IDENTIFIER){
                throw CompileError("""expect varName""")
            }
            compileTerminal()
        }

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ';'){
            throw CompileError("""expect ";"""")
        }
        compileTerminal()

        writer.println("</varDec>")
    }

    fun compileStatements(){
        writer.println("<statements>")

        while(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() in setOf(KeyWord.LET, KeyWord.IF, KeyWord.WHILE, KeyWord.DO, KeyWord.RETURN)){
            when(tokenizer.keyWord()){
                KeyWord.LET -> {
                    compileLet()
                }
                KeyWord.IF -> {
                    compileIf()
                }
                KeyWord.WHILE -> {
                    compileWhile()
                }
                KeyWord.DO -> {
                    compileDo()
                }
                KeyWord.RETURN -> {
                    compileReturn()
                }
                else -> {
                    throw CompileError("expect let, if, while, do, return")
                }
            }
        }

        writer.println("</statements>")
    }

    fun compileDo(){
        writer.println("<doStatement>")

        compileTerminal()

        if(tokenizer.tokenType() != TokenType.IDENTIFIER){
            throw CompileError("expect subroutineName or className or varName")
        }
        compileTerminal()

        // subroutine call
        if(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '('){
            compileTerminal()

            compileExpressionList()

            if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ')'){
                throw CompileError("""expect ")"""")
            }
            compileTerminal()
        }else if(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '.'){
            compileTerminal()

            if(tokenizer.tokenType() != TokenType.IDENTIFIER) {
                throw CompileError("expect subroutineName")
            }
            compileTerminal()

            if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '(') {
                throw CompileError("""expect "("""")
            }
            compileTerminal()

            compileExpressionList()

            if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ')'){
                throw CompileError("""expect ")"""")
            }
            compileTerminal()
        }

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ';'){
            throw CompileError("""expect ";"""")
        }
        compileTerminal()

        writer.println("</doStatement>")
    }

    fun compileLet(){
        writer.println("<letStatement>")

        compileTerminal()

        if(tokenizer.tokenType() != TokenType.IDENTIFIER){
            throw CompileError("""expect varName""")
        }
        compileTerminal()

        if(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '['){
            compileTerminal()

            compileExpression()

            if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ']'){
                throw CompileError("""expect "]"""")
            }
            compileTerminal()
        }

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '='){
            throw CompileError("""expect "="""")
        }
        compileTerminal()

        compileExpression()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ';'){
            throw CompileError("""expect ";"""")
        }
        compileTerminal()

        writer.println("</letStatement>")
    }

    fun compileWhile(){
        writer.println("<whileStatement>")

        compileTerminal()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '(') {
            throw CompileError("""expect "("""")
        }
        compileTerminal()

        compileExpression()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ')'){
            throw CompileError("""expect ")"""")
        }
        compileTerminal()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '{'){
            throw CompileError("""expect "{"""")
        }
        compileTerminal()

        while(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.VAR){
            compileVarDec()
        }

        compileStatements()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '}'){
            throw CompileError("""expect "}"""")
        }
        compileTerminal()

        writer.println("</whileStatement>")
    }

    fun compileReturn(){
        writer.println("<returnStatement>")

        compileTerminal()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ';'){
            compileExpression()
        }

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ';'){
            throw CompileError("""expect ";"""")
        }
        compileTerminal()

        writer.println("</returnStatement>")
    }

    fun compileIf(){
        writer.println("<ifStatement>")
        compileTerminal()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '(') {
            throw CompileError("""expect "("""")
        }
        compileTerminal()

        compileExpression()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ')'){
            throw CompileError("""expect ")"""")
        }
        compileTerminal()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '{'){
            throw CompileError("""expect "{"""")
        }
        compileTerminal()

        while(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.VAR){
            compileVarDec()
        }

        compileStatements()

        if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '}'){
            throw CompileError("""expect "}"""")
        }
        compileTerminal()

        if(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.ELSE){
            compileTerminal()

            if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '{'){
                throw CompileError("""expect "{"""")
            }
            compileTerminal()

            while(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.VAR){
                compileVarDec()
            }

            compileStatements()

            if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '}'){
                throw CompileError("""expect "}"""")
            }
            compileTerminal()
        }

        writer.println("</ifStatement>")
    }

    fun compileExpression(){
        writer.println("<expression>")

        compileTerm()

        while(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() in setOf('+', '-', '*', '/', '&', '|', '<', '>', '=')){
            compileTerminal()

            compileTerm()
        }

        writer.println("</expression>")
    }

    fun compileTerm(){
        writer.println("<term>")

        val type = tokenizer.tokenType()
        if(type in setOf(TokenType.INT_CONST, TokenType.STRING_CONST)){
            compileTerminal()
        }else if(type == TokenType.KEYWORD && tokenizer.keyWord() in setOf(KeyWord.TRUE, KeyWord.FALSE, KeyWord.NULL, KeyWord.THIS)){
            compileTerminal()
        }else if(type == TokenType.IDENTIFIER){
            compileTerminal()

            if(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '['){
                compileTerminal()

                compileExpression()

                if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ']'){
                    throw CompileError("""expect "]"""")
                }

                compileTerminal()
            }else if(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '('){ // subroutine Call
                compileTerminal()

                compileExpressionList()

                if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ')'){
                    throw CompileError("""expect ")"""")
                }
                compileTerminal()
            }else if(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '.'){ // subroutine Call
                compileTerminal()

                if(tokenizer.tokenType() != TokenType.IDENTIFIER) {
                    throw CompileError("expect subroutineName")
                }
                compileTerminal()

                if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != '(') {
                    throw CompileError("""expect "("""")
                }
                compileTerminal()

                compileExpressionList()

                if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ')'){
                    throw CompileError("""expect ")"""")
                }
                compileTerminal()
            }
        }else if(type == TokenType.SYMBOL && tokenizer.symbol() == '('){
            compileTerminal()

            compileExpression()

            if(tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != ')'){
                throw CompileError("""expect ")"""")
            }
            compileTerminal()
        }else if(type == TokenType.SYMBOL && tokenizer.symbol() in setOf('-', '~')){
            compileTerminal()

            compileTerm()
        }else{
            throw CompileError("expect term")
        }

        writer.println("</term>")
    }

    fun compileExpressionList(){
        writer.println("<expressionList>")

        if(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')'){
            writer.println("</expressionList>")
            return
        }

        compileExpression()

        while(tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ','){
            compileTerminal()

            compileExpression()
        }

        writer.println("</expressionList>")
    }
}
