package maquinavirtual;

import java.util.List;

public class InstructionK implements Comparable<InstructionK> {
    public Integer number;
    public final Mnemonic mnemonic;
    public DataFrameK parameter;

    public InstructionK (Mnemonic mnemonic, DataFrameK parameter) {
        this.number = 0;
        this.mnemonic = mnemonic;
        this.parameter = parameter;
    }

    public InstructionK(Integer number, Mnemonic mnemonic, DataFrameK parameter) {
        this.number = number;
        this.mnemonic = mnemonic;
        this.parameter = parameter;
    }

    // These are needed for TableView to bind and display the object
    public Integer getNumber() {
        return number;
    }

    public Mnemonic getMnemonic() {
        return mnemonic;
    }

    public DataFrameK getParameter() {
        return parameter;
    }

    public void setParameter(DataFrameK parameter) {
        this.parameter = parameter;
    }

    public static void enumerateInstructions(List<InstructionK> insList) {
        for (int i = 0; i < insList.size(); i++) {
            insList.get(i).number = i + 1;
        }
    }

    @Override
    public String toString() {
        return String.format("InstructionOLD %s -  %s - %s", number, mnemonic, parameter);
    }

    @Override
    public int compareTo(InstructionK o) {
        return this.number.compareTo(o.number);
    }

    public enum Mnemonic {
        ADD,
        DIV,
        MUL,
        SUB,
        DVW,
        MOD,
        PWR,
        ALB,
        ALI,
        ALR,
        ALS,
        LDB,
        LDI,
        LDR,
        LDS,
        LDV,
        STR,
        AND,
        NOT,
        OR,
        BGE,
        BGR,
        DIF,
        EQL,
        SME,
        SMR,
        JMF,
        JMP,
        JMT,
        STP,
        REA,
        WRT,
        STC
    }
}