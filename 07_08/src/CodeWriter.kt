import java.io.File

class CodeWriter(outputFile: File){
    private val writer = outputFile.printWriter()
    private var compJumpLabelNum = 0
    private var currentFileName = outputFile.name.substringAfter('.')
    private var currentFunctionName: String = ""
    private var returnSymbolNum = 0

    fun setFileName(fileName: String){
        currentFileName = fileName
    }

    fun writeArithmetic(command: String){
        when(command) {
            "add" -> {
                writer.println("""
                    @SP     // add
                    AM=M-1
                    D=M
                    A=A-1
                    M=D+M
                    D=A+1
                """.trimIndent())
            }
            "sub" -> {
                writer.println("""
                    @SP     // sub
                    A=M-1
                    D=M
                    A=A-1
                    M=M-D
                    D=A+1
                    @SP
                    M=D
                """.trimIndent())
            }
            "neg" -> {
                writer.println("""
                    @SP     // neg
                    A=M-1
                    M=-M
                """.trimIndent())
            }
            "eq" -> {
                writer.println("""
                    @SP     // eq
                    AM=M-1
                    D=M
                    A=A-1
                    D=M-D
                    @COMP_LABEL0_${compJumpLabelNum}
                    D;JEQ
                    @SP
                    A=M-1
                    M=0
                    @COMP_LABEL1_${compJumpLabelNum}
                    0;JMP
                    (COMP_LABEL0_${compJumpLabelNum})
                    @SP
                    A=M-1
                    M=-1
                    (COMP_LABEL1_${compJumpLabelNum})
                """.trimIndent())
                compJumpLabelNum++
            }
            "gt" -> {
                writer.println("""
                    @SP     // gt
                    AM=M-1
                    D=M
                    A=A-1
                    D=M-D
                    @COMP_LABEL0_${compJumpLabelNum}
                    D;JGT
                    @SP
                    A=M-1
                    M=0
                    @COM_LABEL1_${compJumpLabelNum}
                    0;JMP
                    (COMP_LABEL0_${compJumpLabelNum})
                    @SP
                    A=M-1
                    M=-1
                    (COM_LABEL1_${compJumpLabelNum})
                """.trimIndent())
                compJumpLabelNum++
            }
            "lt" -> {
                writer.println("""
                    @SP     // lt
                    AM=M-1
                    D=M
                    A=A-1
                    D=M-D
                    @COMP_LABEL0_${compJumpLabelNum}
                    D;JLT
                    @SP
                    A=M-1
                    M=0
                    @COM_LABEL1_${compJumpLabelNum}
                    0;JMP
                    (COMP_LABEL0_${compJumpLabelNum})
                    @SP
                    A=M-1
                    M=-1
                    (COM_LABEL1_${compJumpLabelNum})
                """.trimIndent())
                compJumpLabelNum++
            }
            "and" -> {
                writer.println("""
                    @SP     // and
                    A=M-1
                    D=M
                    A=A-1
                    M=M&D
                    D=A+1
                    @SP
                    M=D
                """.trimIndent())
            }
            "or" -> {
                writer.println("""
                    @SP     // or
                    A=M-1
                    D=M
                    A=A-1
                    M=M|D
                    D=A+1
                    @SP
                    M=D
                """.trimIndent())
            }
            "not" -> {
                writer.println("""
                    @SP     // not
                    A=M-1
                    M=!M
                """.trimIndent())
            }
        }
    }

    fun writePushPop(command: CommandType, segment: String, index: Int){
        if(command == CommandType.C_PUSH){ // PUSH
            when (segment) {
                "constant" -> {
                    writer.println("""
                            @$index     // push constant $index
                            D=A
                            @SP
                            A=M
                            M=D
                            D=A+1
                            @SP
                            M=D
                        """.trimIndent())
                }
                "local" -> {
                    writer.println("""
                            @LCL        // push local $index
                            D=M
                            @$index
                            A=D+A
                            D=M
                            @SP
                            A=M
                            M=D
                            D=A+1
                            @SP
                            M=D
                        """.trimIndent())
                }
                "argument" -> {
                    writer.println("""
                            @ARG        // push argument $index
                            D=M
                            @$index
                            A=D+A
                            D=M
                            @SP
                            A=M
                            M=D
                            D=A+1
                            @SP
                            M=D
                        """.trimIndent())
                }
                "this" -> {
                    writer.println("""
                            @THIS       // push this $index
                            D=M
                            @$index
                            A=D+A
                            D=M
                            @SP
                            A=M
                            M=D
                            D=A+1
                            @SP
                            M=D
                        """.trimIndent())
                }
                "that" -> {
                    writer.println("""
                            @THAT       // push that $index
                            D=M
                            @$index
                            A=D+A
                            D=M
                            @SP
                            A=M
                            M=D
                            D=A+1
                            @SP
                            M=D
                        """.trimIndent())
                }
                "pointer" -> {
                    writer.println("""
                            @3          // push pointer $index
                            D=A
                            @$index
                            A=A+D
                            D=M
                            @SP
                            A=M
                            M=D
                            D=A+1
                            @SP
                            M=D
                        """.trimIndent())
                }
                "temp" -> {
                    writer.println("""
                            @5          // push temp $index
                            D=A
                            @$index
                            A=A+D
                            D=M
                            @SP
                            A=M
                            M=D
                            D=A+1
                            @SP
                            M=D
                        """.trimIndent())
                }
                "static" -> {
                    writer.println("""
                            @$currentFileName.$index    // push static $index
                            D=M
                            @SP
                            A=M
                            M=D
                            D=A+1
                            @SP
                            M=D
                        """.trimIndent())
                }
            }
        }else{ // POP
            when (segment) {
                "local" -> {
                    writer.println("""
                            @LCL        // pop local $index
                            D=M
                            @$index
                            D=D+A
                            @R13
                            M=D
                            @SP
                            AM=M-1
                            D=M
                            @R13
                            A=M
                            M=D
                        """.trimIndent())
                }
                "argument" -> {
                    writer.println("""
                            @ARG        // pop argument $index
                            D=M
                            @$index
                            D=D+A
                            @R13
                            M=D
                            @SP
                            AM=M-1
                            D=M
                            @R13
                            A=M
                            M=D
                        """.trimIndent())
                }
                "this" -> {
                    writer.println("""
                            @THIS       // pop this $index
                            D=M
                            @$index
                            D=D+A
                            @R13
                            M=D
                            @SP
                            AM=M-1
                            D=M
                            @R13
                            A=M
                            M=D
                        """.trimIndent())
                }
                "that" -> {
                    writer.println("""
                            @THAT       // pop that $index
                            D=M
                            @$index
                            D=D+A
                            @R13
                            M=D
                            @SP
                            AM=M-1
                            D=M
                            @R13
                            A=M
                            M=D
                        """.trimIndent())
                }
                "pointer" -> {
                    writer.println("""
                            @3          // pop pointer $index
                            D=A
                            @$index
                            D=D+A
                            @R13
                            M=D
                            @SP
                            AM=M-1
                            D=M
                            @R13
                            A=M
                            M=D
                        """.trimIndent())
                }
                "temp" -> {
                    writer.println("""
                            @5          // pop temp $index
                            D=A
                            @$index
                            D=D+A
                            @R13
                            M=D
                            @SP
                            AM=M-1
                            D=M
                            @R13
                            A=M
                            M=D
                        """.trimIndent())
                }
                "static" -> {
                    writer.println("""
                            @SP         // pop static $index
                            AM=M-1
                            D=M
                            @$currentFileName.$index
                            M=D
                        """.trimIndent())
                }
            }
        }
    }

    fun writeInit(){
        writer.println("""
            @261        // init
            D=A
            @SP
            M=D
            @Sys.init
            0;JMP
        """.trimIndent())
    }

    fun writeLabel(label: String){
        writer.println("""
            ($currentFunctionName$$label)   // label $label
        """.trimIndent())
    }

    fun writeGoto(label: String){
        writer.println("""
            @$currentFunctionName$$label     // goto $label
            0;JMP
        """.trimIndent())
    }

    fun writeIf(label: String){
        writer.println("""
            @SP         // if-goto $label
            AM=M-1
            D=M
            @$currentFunctionName$$label
            D;JNE
        """.trimIndent())
    }

    fun writeCall(functionName: String, numArgs: Int){
        writer.println("""
            @ReturnSymbol$returnSymbolNum   // call $functionName $numArgs
            D=A
            @SP
            A=M
            M=D
            @SP
            M=M+1
            @LCL
            D=M
            @SP
            A=M
            M=D
            @SP
            M=M+1
            @ARG
            D=M
            @SP
            A=M
            M=D
            @SP
            M=M+1
            @THIS
            D=M
            @SP
            A=M
            M=D
            @SP
            M=M+1
            @THAT
            D=M
            @SP
            A=M
            M=D
            @SP
            MD=M+1
            @$numArgs
            D=D-A
            @5
            D=D-A
            @ARG
            M=D
            @SP
            D=M
            @LCL
            M=D
            @$functionName
            0;JMP
            (ReturnSymbol$returnSymbolNum)
        """.trimIndent())
        returnSymbolNum++
    }

    fun writeReturn(){
        writer.println("""
            @LCL        // return
            D=M
            @R13
            M=D
            @5
            A=D-A
            D=M
            @R14
            M=D
            @SP
            A=M-1
            D=M
            @ARG
            A=M
            M=D
            @ARG
            D=M+1
            @SP
            M=D
            @R13
            AM=M-1
            D=M
            @THAT
            M=D
            @R13
            AM=M-1
            D=M
            @THIS
            M=D
            @R13
            AM=M-1
            D=M
            @ARG
            M=D
            @R13
            A=M-1
            D=M
            @LCL
            M=D
            @R14
            A=M
            0;JMP
        """.trimIndent())
    }

    fun writeFunction(functionName: String, numLocals: Int){
        currentFunctionName = functionName
        writer.println("""
            ($functionName)     // function $functionName $numLocals
        """.trimIndent())
        repeat(numLocals){
            writer.println("""
                @0
                D=A
                @SP
                A=M
                M=D
                @SP
                M=M+1
            """.trimIndent())
        }
    }

    fun close(){
        writer.close()
    }
}