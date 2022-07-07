package maquinavirtual;

import java.util.*;

public class VirtualMachineK {
    private final List<InstructionK> instructions;
    private final Stack<DataFrameK> stack = new Stack<>();
    private int instructionPointer = 0;
    private VirtualMachineStatusK status = VirtualMachineStatusK.NOT_STARTED;
    private DataTypeK syscallDataTypeK = null;
    private Object syscallData = null;

    public VirtualMachineK(List<InstructionK> instructions) {
        this.instructions = instructions;
    }

    public Stack<DataFrameK> getStack() {
        return stack;
    }

    public VirtualMachineStatusK getStatus() {
        return status;
    }

    public List<InstructionK> getInstructions() {
        return instructions;
    }

    public int getInstructionPointer() {
        return instructionPointer;
    }

    public void setStatus(VirtualMachineStatusK status) {
        this.status = status;
    }

    public String printStack() {
        int stackPos = 0;
        StringBuilder sb = new StringBuilder("-- BOTTOM --\n");
        for (DataFrameK se : stack) {
            sb.append(stackPos).append(" - ").append(se.toDebugString()).append("\n");
            stackPos++;
        }
        sb.append("-- STACK TOP --");
        return sb.toString();
    }

    public void resumeExecution() {
        if (status == VirtualMachineStatusK.SYSCALL_IO_READ) {
            syscallData = syscallData.toString().trim();
            try {
                switch (syscallDataTypeK) {
                    case INTEGER -> syscallData = Integer.parseInt((String) syscallData);
                    case FLOAT -> syscallData = Float.parseFloat((String) syscallData);
                }
                stack.push(new DataFrameK(syscallDataTypeK, syscallData));
                System.out.println(printStack());
            } catch (NumberFormatException e) {
                throw new RuntimeException(String.format("Leitura de entrada invalida, nao é possivel interpretar %s as %s\n", syscallData.toString(), this.syscallDataTypeK.toString()));
            }
        }
        this.syscallData = null;
        this.syscallDataTypeK = null;
    }

    public DataTypeK getSyscallDataTypeK() {
        return syscallDataTypeK;
    }

    public void setSyscallDataTypeK(DataTypeK syscallDataTypeK) {
        this.syscallDataTypeK = syscallDataTypeK;
    }

    public Object getSyscallData() {
        return syscallData;
    }

    public void setSyscallData(Object syscallData) {
        this.syscallData = syscallData;
    }

    public void executeAll() {
        while (this.status != VirtualMachineStatusK.HALTED) {
            // IF we returned from a syscall/IO operation
            if (status == VirtualMachineStatusK.SYSCALL_IO_READ || status == VirtualMachineStatusK.SYSCALL_IO_WRITE) {
                resumeExecution();
            }
            executeStep();
            // If the last instruction was a syscall, pause execution until it completes
            if (status == VirtualMachineStatusK.SYSCALL_IO_READ || status == VirtualMachineStatusK.SYSCALL_IO_WRITE) {
                break;
            }
        }
    }

    public void executeStep() {
        status = VirtualMachineStatusK.RUNNING;
        InstructionK ins = instructions.get(instructionPointer);
        System.out.printf("Instruction Pointer: %d\n", instructionPointer+1);
        System.out.println(ins);
        switch (ins.mnemonic) {
            case ADD -> add(ins);
            case DIV -> divide(ins);
            case MUL -> multiply(ins);
            case SUB -> subtract(ins);
            case DVW -> divideWhole(ins);
            case MOD -> modulo(ins);
            case PWR -> potentiation(ins);
            case ALB -> allocateBoolean(ins);
            case ALI -> allocateInteger(ins);
            case ALR -> allocateFloat(ins);
            case ALS -> allocateLiteralValue(ins);
            case LDB -> loadBoolean(ins);
            case LDI -> loadInteger(ins);
            case LDR -> loadFloat(ins);
            case LDS -> loadLiteral(ins);
            case LDV -> loadValueAt(ins);
            case STR -> storeValueAt(ins);
            case AND -> logicalAnd(ins);
            case NOT -> logicalNOT(ins);
            case OR  -> logicalOr(ins);
            case BGE -> relationalGreaterOrEquals(ins);
            case BGR -> relationalGreater(ins);
            case DIF -> relationalDifferent(ins);
            case EQL -> relationalEquals(ins);
            case SME -> relationalLessOrEquals(ins);
            case SMR -> relationalLess(ins);
            case JMF -> jumpFalseToAddress(ins);
            case JMP -> jumpToAddress(ins);
            case JMT -> jumpTrueToAddress(ins);
            case STP -> {
                this.status = VirtualMachineStatusK.HALTED;
                return;
            }
            case REA -> read(ins);
            case WRT -> write(ins);
            case STC -> stackCopyToPositions(ins);
        }
        if (this.status != VirtualMachineStatusK.SYSCALL_IO_READ) {
            System.out.println(this.printStack());
        }
        instructionPointer++;
    }

    // VM Instructions
    private void add(InstructionK ins) {
        DataFrameK x = stack.pop();
        DataFrameK y = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            x_val = x_val + y_val;
            stack.push(new DataFrameK(type, x_val));
        } else {
            var x_val = ((Number) x.content).floatValue();
            var y_val = ((Number) y.content).floatValue();
            float result = x_val + y_val;
            stack.push(new DataFrameK(type, result));
        }
    }

    private void divide(InstructionK ins) {
        DataFrameK x = stack.pop();
        DataFrameK y = stack.pop();
        var divideByZeroEx = new RuntimeException(String.format("Divisao por zero na instrucao %s\n ->> top: %s\n --> subTop: %s", ins, x, y));
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            if (x_val.equals(0)) {
                throw divideByZeroEx;
            }
            x_val = y_val / x_val;
            stack.push(new DataFrameK(type, x_val));
        } else {
            var x_val = (Float)((Number) x.content).floatValue();
            var y_val = (Float)((Number) y.content).floatValue();
            if (x_val.equals(0f)) {
                throw divideByZeroEx;
            }
            float result = y_val / x_val;
            stack.push(new DataFrameK(type, result));
        }
    }

    private void multiply(InstructionK ins) {
        DataFrameK x = stack.pop();
        DataFrameK y = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            x_val = x_val * y_val;
            stack.push(new DataFrameK(type, x_val));
        } else {
            var x_val = ((Number) x.content).floatValue();
            var y_val = ((Number) y.content).floatValue();
            float result = x_val * y_val;
            stack.push(new DataFrameK(type, result));
        }
    }

    private void subtract(InstructionK ins) {
        DataFrameK x = stack.pop();
        DataFrameK y = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            x_val = y_val - x_val;
            stack.push(new DataFrameK(type, x_val));
        } else {
            var x_val = ((Number) x.content).floatValue();
            var y_val = ((Number) y.content).floatValue();
            float result = y_val - x_val;
            stack.push(new DataFrameK(type, result));
        }
    }

    private void divideWhole(InstructionK ins) {
        var x = stack.pop();
        var y = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            x_val = y_val / x_val;
            stack.push(new DataFrameK(type, x_val));
        } else {
            var x_val = ((Number) x.content).floatValue();
            var y_val = ((Number) y.content).floatValue();
            float result = y_val / x_val;
            stack.push(new DataFrameK(DataTypeK.INTEGER, (int) result));
        }
    }

    private void modulo(InstructionK ins) {
        var x = stack.pop();
        var y = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            x_val = y_val % x_val;
            stack.push(new DataFrameK(type, x_val));
        } else {
            var x_val = ((Number) x.content).floatValue();
            var y_val = ((Number) y.content).floatValue();
            float result = y_val % x_val;
            stack.push(new DataFrameK(type, result));
        }
    }

    private void potentiation(InstructionK ins) {
        var x = stack.pop();
        var y = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            x_val = (int) Math.pow(y_val, x_val);
            stack.push(new DataFrameK(type, x_val));
        } else {
            var x_val = ((Number) x.content).floatValue();
            var y_val = ((Number) y.content).floatValue();
            float result = (float) Math.pow(y_val,x_val);
            stack.push(new DataFrameK(type, result));
        }
    }

    private void allocateBoolean(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.INTEGER) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.INTEGER), ins.parameter.type);
        }
        for (int i = 0; i < (Integer) ins.parameter.content; i++) {
            stack.push(new DataFrameK(DataTypeK.BOOLEAN, false));
        }
    }

    private void allocateInteger(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.INTEGER) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.INTEGER), ins.parameter.type);
        }
        for (int i = 0; i < (Integer) ins.parameter.content; i++) {
            stack.push(new DataFrameK(DataTypeK.INTEGER, 0));
        }
    }

    private void allocateFloat(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.INTEGER) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.INTEGER), ins.parameter.type);
        }
        for (int i = 0; i < (Integer) ins.parameter.content; i++) {
            stack.push(new DataFrameK(DataTypeK.FLOAT, 0f));
        }
    }

    private void allocateLiteralValue(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.INTEGER) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.INTEGER), ins.parameter.type);
        }
        for (int i = 0; i < (Integer) ins.parameter.content; i++) {
            stack.push(new DataFrameK(DataTypeK.LITERAL, ""));
        }
    }

    private void loadBoolean(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.BOOLEAN) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.BOOLEAN), ins.parameter.type);
        }
        var content = (Boolean) ins.parameter.content;
        stack.push(new DataFrameK(DataTypeK.BOOLEAN, content));
    }

    private void loadInteger(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.INTEGER) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.INTEGER), ins.parameter.type);
        }
        var content = (Integer) ins.parameter.content;
        stack.push(new DataFrameK(DataTypeK.INTEGER, content));
    }

    private void loadFloat(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.FLOAT) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.FLOAT), ins.parameter.type);
        }
        var content = (Float) ins.parameter.content;
        stack.push(new DataFrameK(DataTypeK.FLOAT, content));
    }

    private void loadLiteral(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.LITERAL) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.LITERAL), ins.parameter.type);
        }
        var content = (String) ins.parameter.content;
        stack.push(new DataFrameK(DataTypeK.LITERAL, content));
    }


    private void storeValueAt(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.ADDRESS) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.ADDRESS), ins.parameter.type);
        }
        // stack starts at 1 on the grammar level, so we must offset that with a -1.
        var stackElement = stack.pop();
        stack.set( (Integer)ins.parameter.content - 1,
                new DataFrameK(stackElement.type, stackElement.content)
        );
    }

    private void loadValueAt(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.ADDRESS) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.ADDRESS), ins.parameter.type);
        }
        // stack starts at 1 on the grammar level, so we must offset that with a -1.
        var stackElement = stack.get((Integer) ins.parameter.content - 1);
        stack.push(new DataFrameK(stackElement.type, stackElement.content));
    }

    private void logicalAnd(InstructionK ins) {
        DataFrameK x = stack.pop();
        DataFrameK y = stack.pop();
        var type = checkType(Collections.singletonList(DataTypeK.BOOLEAN), ins, x, y);
        var x_val = (Boolean) x.content;
        var y_val = (Boolean) y.content;
        x_val = x_val & y_val;
        stack.push(new DataFrameK(type, x_val));
    }

    private void logicalNOT(InstructionK ins) {
        DataFrameK x = stack.pop();
        var type = checkType(Collections.singletonList(DataTypeK.BOOLEAN), ins, x, x);
        var x_val = !(Boolean) x.content;
        stack.push(new DataFrameK(type, x_val));
    }

    private void logicalOr(InstructionK ins) {
        DataFrameK x = stack.pop();
        DataFrameK y = stack.pop();
        var type = checkType(Collections.singletonList(DataTypeK.BOOLEAN), ins, x, y);
        var x_val = (Boolean) x.content;
        var y_val = (Boolean) y.content;
        x_val = x_val || y_val;
        stack.push(new DataFrameK(type, x_val));
    }

    private void relationalGreaterOrEquals(InstructionK ins) {
        DataFrameK x = stack.pop();
        DataFrameK y = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            var flag = y_val >= x_val;
            stack.push(new DataFrameK(DataTypeK.BOOLEAN, flag));
        } else {
            var x_val = (Float) x.content;
            var y_val = (Float) y.content;
            var flag = y_val >= x_val;
            stack.push(new DataFrameK(DataTypeK.BOOLEAN, flag));
        }
    }

    private void relationalGreater(InstructionK ins) {
        DataFrameK x = stack.pop();
        DataFrameK y = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            var flag = y_val > x_val;
            stack.push(new DataFrameK(DataTypeK.BOOLEAN, flag));
        } else {
            var x_val = ((Number) x.content).floatValue();
            var y_val = ((Number) y.content).floatValue();
            Boolean result = y_val > x_val;
            stack.push(new DataFrameK(DataTypeK.BOOLEAN, result));
        }
    }

    private void relationalDifferent(InstructionK ins) {
        DataFrameK x = stack.pop();
        DataFrameK y = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        var result = false;
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            result = !y_val.equals(x_val);
        } else {
            var x_val = ((Number) x.content).floatValue();
            var y_val = ((Number) y.content).floatValue();
            result = y_val != x_val;
        }
        stack.push(new DataFrameK(DataTypeK.BOOLEAN, result));
    }

    private void relationalEquals(InstructionK ins) {
        DataFrameK x = stack.pop();
        DataFrameK y = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, x, y);
        var result = false;
        if (type == DataTypeK.INTEGER) {
            var x_val = (Integer) x.content;
            var y_val = (Integer) y.content;
            result = y_val.equals(x_val);
        } else {
            var x_val = ((Number) x.content).floatValue();
            var y_val = ((Number) y.content).floatValue();
            result = y_val == x_val;
        }
        stack.push(new DataFrameK(DataTypeK.BOOLEAN, result));
    }

    private void relationalLessOrEquals(InstructionK ins) {
        DataFrameK top = stack.pop();
        DataFrameK sub = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, top, sub);
        var result = false;
        if (type == DataTypeK.INTEGER) {
            var top_val = (Integer) top.content;
            var sub_val = (Integer) sub.content;
            result = sub_val <= top_val;
        } else {
            var top_val = (Float) top.content;
            var sub_val = (Float) sub.content;
            result = sub_val <= top_val;
        }
        stack.push(new DataFrameK(DataTypeK.BOOLEAN, result));
    }

    private void relationalLess(InstructionK ins) {
        DataFrameK top = stack.pop();
        DataFrameK sub = stack.pop();
        var type = checkType(DataTypeK.getNumericDataTypes(), ins, top, sub);
        var result = false;
        if (type == DataTypeK.INTEGER) {
            var top_val = (Integer) top.content;
            var sub_val = (Integer) sub.content;
            result = sub_val < top_val;
        } else {
            var top_val = (Float) top.content;
            var sub_val = (Float) sub.content;
            result = sub_val < top_val;
        }
        stack.push(new DataFrameK(DataTypeK.BOOLEAN, result));
    }


    private void jumpFalseToAddress(InstructionK ins) {
        checkType(Collections.singletonList(DataTypeK.ADDRESS), ins, ins.parameter, ins.parameter);
        var top = stack.pop();
        checkType(Collections.singletonList(DataTypeK.BOOLEAN), ins, top, top);
        if (!(Boolean) top.content) {
            instructionPointer = (Integer) ins.parameter.content;
            instructionPointer--; // We always add +1 after each instruction, this will revert that
        }
    }

    private void jumpToAddress(InstructionK ins) {
        checkType(Collections.singletonList(DataTypeK.ADDRESS), ins, ins.parameter, ins.parameter);
        instructionPointer = (Integer) ins.parameter.content;
        instructionPointer--; // We always add +1 after each instruction, this will revert that
    }

    private void jumpTrueToAddress(InstructionK ins) {
        checkType(Collections.singletonList(DataTypeK.ADDRESS), ins, ins.parameter, ins.parameter);
        var top = stack.pop();
        checkType(Collections.singletonList(DataTypeK.BOOLEAN), ins, top, top);
        if ((Boolean) top.content) {
            instructionPointer = (Integer) ins.parameter.content;
            instructionPointer--; // We always add +1 after each instruction, this will revert that
        }
    }

    private void read(InstructionK ins) {
        var acceptedTypes = DataTypeK.getNumericDataTypes();
        acceptedTypes.add(DataTypeK.LITERAL);
        if (!acceptedTypes.contains(ins.parameter.type)) {
            invalidInstructionParameter(acceptedTypes, ins.parameter.type);
        }
        this.status = VirtualMachineStatusK.SYSCALL_IO_READ;
        this.syscallDataTypeK = ins.parameter.type;
    }

    private void write(InstructionK ins) {
        var stackElement = stack.pop();
        checkType(Arrays.asList(DataTypeK.INTEGER, DataTypeK.FLOAT, DataTypeK.LITERAL),
                ins, stackElement, stackElement);
        this.status = VirtualMachineStatusK.SYSCALL_IO_WRITE;
        this.syscallDataTypeK = stackElement.type;
        this.syscallData = stackElement.content;
    }

    private void stackCopyToPositions(InstructionK ins) {
        if (ins.parameter.type != DataTypeK.INTEGER) {
            invalidInstructionParameter(Collections.singletonList(DataTypeK.INTEGER), ins.parameter.type);
        }
        var numberPositions = (Integer) ins.parameter.content;
        var stackElement = stack.pop();
        for (int i=stack.size()-numberPositions; i<=stack.size()-1; i++) {
            stack.set(i, stackElement);
        }
    }

    private static DataTypeK checkType(List<DataTypeK> compatibleTypes, InstructionK ins, DataFrameK x, DataFrameK y) {
        var runtimeException = new RuntimeException(String.format("Tipos de pilha de dados incompativeis para instrucao %s!\n --> top: %s\n --> subTop: %s", ins, x.toDebugString(), y.toDebugString()));
        DataTypeK effectiveOutputDataTypeK = null;
        boolean compatibleTypesFlag = !(compatibleTypes.contains(x.type)) && !(compatibleTypes.contains(y.type));
        if (!compatibleTypesFlag) {
            switch (x.type) {
                // ADD, MUL, SUB, DIV
                case FLOAT -> {
                    switch (y.type) {
                        case FLOAT, INTEGER -> effectiveOutputDataTypeK = DataTypeK.FLOAT;
                    }
                }
                // ADD, MUL, SUB, DIV
                case INTEGER -> {
                    switch (y.type) {
                        case FLOAT -> effectiveOutputDataTypeK = DataTypeK.FLOAT;
                        case INTEGER -> effectiveOutputDataTypeK = DataTypeK.INTEGER;
                    }
                }
                // LDB
                case BOOLEAN -> {
                    if (y.type == DataTypeK.BOOLEAN) {
                        effectiveOutputDataTypeK = DataTypeK.BOOLEAN;
                    }
                }
                // JMP
                case ADDRESS -> {
                    if (y.type == DataTypeK.ADDRESS) {
                        effectiveOutputDataTypeK = DataTypeK.ADDRESS;
                    }
                }
                case LITERAL -> {
                    if (y.type == DataTypeK.LITERAL) {
                        effectiveOutputDataTypeK = DataTypeK.LITERAL;
                    }
                }
            }
        } else {
            throw runtimeException;
        }
        if (effectiveOutputDataTypeK == null) {
            throw runtimeException;
        }
        return effectiveOutputDataTypeK;
    }

    private static void invalidInstructionParameter(List<DataTypeK> expected, DataTypeK got) {
        throw new RuntimeException(String.format("Instrucao insvalida, parametro %s esperado, recebido: %s", expected, got));
    }
}
