import java.io.File

class CodeWriter(outputFile: File){
    private val writer = outputFile.printWriter()
    private var compJumpLabelNum = 0
    private var currentFileName = outputFile.name.substringAfter('.')

    fun setFileName(fileName: String){
        currentFileName = fileName
    }

    fun writeArithmetic(command: String){
        when(command) {
            "add" -> {
                writer.println("""
                    @SP
                    AM=M-1
                    D=M
                    A=A-1
                    M=D+M
                    D=A+1
                """.trimIndent())
            }
            "sub" -> {
                writer.println("""
                    @SP
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
                    @SP
                    A=M-1
                    M=-M
                """.trimIndent())
            }
            "eq" -> {
                writer.println("""
                    @SP
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
                    @SP
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
                    @SP
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
                    @SP
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
                    @SP
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
                    @SP
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
                            @$index
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
                            @LCL
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
                            @ARG
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
                            @THIS
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
                            @THAT
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
                            @3
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
                            @5
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
                            @$currentFileName.$index
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
                            @LCL
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
                            @ARG
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
                            @THIS
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
                            @THAT
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
                            @3
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
                            @5
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
                            @SP
                            AM=M-1
                            D=M
                            @$currentFileName.$index
                            M=D
                        """.trimIndent())
                }
            }
        }
    }

    fun close(){
        writer.close()
    }
}