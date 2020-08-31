enum class VariableKind{
    STATIC, FIELD, ARG, VAR, NONE
}

data class SymbolTableEntry(val name: String, val type: String, val kind: VariableKind, val index: Int)

class SymbolTable {
    private var classIndex = 0
    private var subroutineIndex = 0
    private var classSymbolTable: MutableMap<String, SymbolTableEntry> = mutableMapOf()
    private var subroutineSymbolTable: MutableMap<String, SymbolTableEntry> = mutableMapOf()

    fun startSubroutine(){
        subroutineSymbolTable = mutableMapOf()
        subroutineIndex = 0
    }

    fun define(name: String, type: String, kind: VariableKind){
        when(kind){
            VariableKind.STATIC, VariableKind.FIELD -> {
                classSymbolTable[name] = SymbolTableEntry(name, type, kind, classIndex++)
            }
            VariableKind.ARG, VariableKind.VAR -> {
                subroutineSymbolTable[name] = SymbolTableEntry(name, type, kind, subroutineIndex++)
            }
            else -> {
                throw CompileError("None Type")
            }
        }
    }

    fun varCount(kind: VariableKind): Int{
        return when(kind){
            VariableKind.STATIC, VariableKind.FIELD -> {
                classSymbolTable.filter {
                    it.value.kind == kind
                }.size
            }
            VariableKind.ARG, VariableKind.VAR -> {
                subroutineSymbolTable.filter {
                    it.value.kind == kind
                }.size
            }
            else -> {
                throw CompileError("None Type")
            }
        }
    }

    fun kindOf(name: String): VariableKind{
        val res1 = classSymbolTable[name]
        if(res1 != null){
            return res1.kind
        }
        val res2 = subroutineSymbolTable[name]
        if(res2 != null){
            return res2.kind
        }
        return VariableKind.NONE
    }

    fun typeOf(name: String): String{
        val res1 = classSymbolTable[name]
        if(res1 != null){
            return res1.type
        }
        val res2 = subroutineSymbolTable[name]
        if(res2 != null){
            return res2.type
        }
        return ""
    }

    fun indexOf(name: String): Int{
        val res1 = classSymbolTable[name]
        if(res1 != null){
            return res1.index
        }
        val res2 = subroutineSymbolTable[name]
        if(res2 != null){
            return res2.index
        }
        return -1
    }
}