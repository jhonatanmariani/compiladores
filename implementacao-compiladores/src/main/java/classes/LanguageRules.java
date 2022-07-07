package classes;

import maquinavirtual.*;

import java.util.ArrayList;
import java.util.List;

public class LanguageRules { // AcoesSemanticas
    private String contexto = "";
    private int VT = 0; // contador para número total de constantes e de variáveis
    private int VP = 0; // contador para número de constantes ou variáveis de um determinado tipo;
    private int VIT = 0; // contador para o tamanho das variáveis indexadas de um determinado tipo;
    private int tipo; // 1 variável natural, 2 variável real, 3 variável char, 4 variável boolean, 5 constante natural, 6 constante real, 7 constante char;
    private int ponteiro = 1;  // indicador da posição onde será gerada a próxima instrução na área de instruções;
    boolean variavelIndexada = false;
    private List<Integer> pilhaDeDesvios = new ArrayList<>(); //Lembrar de controlar para trabalhar como LIFO
    private List<Simbolo> tabelaDeSimbolos = new ArrayList<>();
    private static List<InstructionK> instructionList = new ArrayList<>();
    private String identificadorReconhecido;
    private int constanteInteira;
    private List<Object> listaAtributos = new ArrayList<>();
    private static List<String> listaErros = new ArrayList<>();

    public List<InstructionK> getInstructionKList() {
        // Ugly hack: figure out why do is this list blank after the .JJ has run.
        var tmp =  instructionList;
        instructionList = new ArrayList<>();
        return tmp;
    }

    public List<String> getListaErros() {
        // Ugly hack: figure out why do is this list blank after the .JJ has run.
        var tmp = listaErros;
        listaErros = new ArrayList<>();
        return tmp;
    }

/////// REGRAS DAS AÇÕES SEMANTICAS

    public void acao1(Token token){
        System.out.println("reconhecimento do identificador do programa (identificador, 0, -, -)");
        Simbolo simbolo = new Simbolo(token.image, 0);
        tabelaDeSimbolos.add(simbolo);
    }

    public void acao2(){
        System.out.println("reconhecimento de fim de programa (ponteiro, STP, 0)");
        InstructionK instruction = new InstructionK(InstructionK.Mnemonic.STP, new DataFrameK(DataTypeK.INTEGER, 0));
        instructionList.add(instruction);
        InstructionK.enumerateInstructions(instructionList);
        System.out.println("Lista de Erros: " + listaErros.toString());
        System.out.println("Lista de Instruções:");
        for (InstructionK i : instructionList) {
            System.out.println(i.toString());
        }
    }

    public void acao3(){
        System.out.println("reconhecimento de identificador do tipo enumerado");
        // TODO implementar depois
    }

    public void acao4() {
        System.out.println("reconhecimento de identificador de constante de tipo enumerado");
        // TODO implementar
    }

    public void acao5() {
        System.out.println("reconhecimento das palavras reservadas as constant");
        // TODO implementar
    }

    public void acao6(){
        System.out.println("reconhecimento do término da declaração de constantes ou variáveis de um determinado tipo");
        this.VP = this.VP + this.VIT;
        switch (this.tipo){
            case 1: case 5: {
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.ALI, new DataFrameK(DataTypeK.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
            case 2:
            case 6:
            {
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.ALR, new DataFrameK(DataTypeK.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
            case 3: case 7: {
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.ALS, new DataFrameK(DataTypeK.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
            case 4:{
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.ALB, new DataFrameK(DataTypeK.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
        }
        if(this.tipo == 1 || this.tipo == 2 || this.tipo == 3 || this.tipo == 4){
            this.VP = 0;
            this.VIT = 0;
        }
    }

    public void acao7(String valor){
        System.out.println("reconhecimento de valor na declaração de constante");
        switch (this.tipo){
            case 5: {
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.LDI, new DataFrameK(DataTypeK.INTEGER, Integer.parseInt(valor)));
                instructionList.add(instruction);
                break;
            }
            case 6: {
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.LDR, new DataFrameK(DataTypeK.FLOAT, Float.parseFloat(valor)));
                instructionList.add(instruction);
                break;
            }
            case 7: {
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.LDS, new DataFrameK(DataTypeK.LITERAL, valor));
                instructionList.add(instruction);
                break;
            }
        }
        InstructionK instruction = new InstructionK(InstructionK.Mnemonic.STC, new DataFrameK(DataTypeK.INTEGER, this.VP));
        instructionList.add(instruction);
        this.ponteiro = this.ponteiro + 1;
        this.VP = 0;
    }

    public void acao8(){
        System.out.println("reconhecimento das palavras reservadas as variable");
        this.contexto = "variável";
    }

    public void acao9() {
        System.out.println("reconhecimento de identificador de constante");
        // TODO implementar depois
    }

    public void acao10() {
        System.out.println("reconhecimento de identificador de variável");
        // TODO implementar depois
    }

    public void acao11(){
        System.out.println("reconhecimento de identificador de variável e tamanho da variável indexada");
        // TODO implementar depois
    }

    public void acao12(Token token){
        System.out.println(" reconhecimento de constante inteira como tamanho da variável indexada");
        this.constanteInteira = Integer.parseInt(token.image);
        this.variavelIndexada = true;
    }

//    public void acao13k(Token token){ // TODO implementar depois
//        System.out.println("reconhecimento de identificador de variável e tamanho da variável indexada");
//        switch (this.contexto){
//            case "variável": {
//                if(!this.variavelIndexada){
//                    this.VT= this.VT + 1;
//                    this.VP= this.VP + 1;
//                    Simbolo simbolo = new Simbolo(this.identificadorReconhecido, this.tipo, this.VT);
//                    tabelaDeSimbolos.add(simbolo);
//                }else{
//                    this.VIT = this.VIT + this.constanteInteira;
//                    Simbolo simbolo = new Simbolo(this.identificadorReconhecido, this.tipo, this.VT+1, this.constanteInteira);
//                    tabelaDeSimbolos.add(simbolo);
//                    this.VT = this.VT + this.constanteInteira;
//                }
//                break;
//            }
//            case "atribuição": {
//                Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> this.identificadorReconhecido.equals(simb.getIdentificador())).findAny().orElse(null);
//                if(!(exist == null) && (exist.getCategoria() == 1 || exist.getCategoria() == 2 || exist.getCategoria() == 3 || exist.getCategoria() == 4)) {
//                    if(exist.getAtributo2() == 0){
//                        if(!this.variavelIndexada){
//                            this.listaAtributos.add(exist.getAtributo1());
//                        }else{
//                            this.listaErros.add("13 - Identifier of non-indexed variable: '" + this.identificadorReconhecido + "' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                        }
//                    } else{
//                        if(this.variavelIndexada){
//                            this.listaAtributos.add(exist.getAtributo1() + this.constanteInteira -1);
//                        }else{
//                            this.listaErros.add("13 - Indexed variables require an index - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                        }
//                    }
//                }else{
//                    this.listaErros.add("13 - Use of undeclared variable or constant - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                }
//                break;
//            }
//            case "entrada dados": {
//                Simbolo exist = this.tabelaDeSimbolos.stream().filter(simb -> this.identificadorReconhecido.equals(simb.getIdentificador())).findAny().orElse(null);
//                if(!(exist == null) && (exist.getCategoria() == 1 || exist.getCategoria() == 2 || exist.getCategoria() == 3 || exist.getCategoria() == 4)) {
//                    if(exist.getAtributo2() == 0){
//                        if(!this.variavelIndexada){
//                            instructionList.add(new InstructionK(InstructionK.Mnemonic.REA, new DataFrameK(DataTypeK.get(exist.getCategoria()), exist.getCategoria())));
//                            this.ponteiro = this.ponteiro + 1;
//                            instructionList.add(new InstructionK(InstructionK.Mnemonic.STR, new DataFrameK(DataTypeK.ADDRESS, exist.getAtributo1())));
//                            this.ponteiro = this.ponteiro + 1;
//                        }else{
//                            this.listaErros.add("13 - Identifier of non-indexed variable - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                        }
//                    } else{
//                        if(this.variavelIndexada){
//                            instructionList.add(new InstructionK(InstructionK.Mnemonic.REA, new DataFrameK(DataTypeK.get(exist.getCategoria()), exist.getCategoria())));
//                            this.ponteiro = this.ponteiro + 1;
//                            instructionList.add(new InstructionK(InstructionK.Mnemonic.STR, new DataFrameK(DataTypeK.ADDRESS, exist.getAtributo1() + this.constanteInteira -1)));
//                            this.ponteiro = this.ponteiro + 1;
//                        }else{
//                            this.listaErros.add("13 - Indexed variables requires an index - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                        }
//                    }
//                }else{
//                    this.listaErros.add("13 - Use of undeclared variable or constant: '"+this.identificadorReconhecido+"' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                }
//                break;
//            }
//        }
//    }

    public void acao13(){
        System.out.println(": reconhecimento da palavra reservada integer");
        if(this.contexto.equals("variável")){
            this.tipo = 1;
        }else{
            this.tipo = 5;
        }
    }

    public void acao14(){
        System.out.println("reconhecimento da palavra reservada float");
        if(this.contexto.equals("variável")){
            this.tipo = 2;
        }else{
            this.tipo = 6;
        }
    }

    public void acao15(){
        System.out.println(": reconhecimento da palavra reservada string");
        if(this.contexto.equals("variável")){
            this.tipo = 3;
        }else{
            this.tipo = 7;
        }
    }

    public void acao16(Token token){
        System.out.println(" reconhecimento da palavra reservada boolean"+ token.image);
        if(this.contexto.equals("variável")){
            this.tipo = 4;
        }else{
            //Verificar posteriormente se precisa adicionar em um array de erros para prosseguir ou não;
            this.listaErros.add("10 - Invalid type for constant - Line/Column: "+token.beginLine+"/"+token.beginColumn);
        }
    }

    public void acao17(){
        System.out.println("reconhecimento de identificador do tipo enumerado");
        // TODO implementar depois
    }


    public void acao18(){
        System.out.println("reconhecimento do início do comando de atribuição");
        this.contexto = "atribuição";
    }

    public void acao19(){
        System.out.println(": reconhecimento do fim do comando de atribuição");
        for(int i=0; i< this.listaAtributos.size(); i++){
            instructionList.add(new InstructionK(InstructionK.Mnemonic.STR, new DataFrameK(DataTypeK.ADDRESS, this.listaAtributos.get(i))));
            this.ponteiro = this.ponteiro + 1;
        }
    }

    public void acao20(){
        System.out.println("reconhecimento do comando de entrada de dados");
        this.contexto = "entrada dados";
    }

    public void acao21() {
        System.out.println("reconhecimento das palavras reservadas write all this");
        // TODO implementar depois
    }

    public void acao22() {
        System.out.println("reconhecimento das palavras reservadas write all");
        // TODO implementar depois
    }

    public void acao23(){
        System.out.println("reconhecimento de mensagem em comando de saída de dados");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.WRT, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao24(Token token){
        System.out.println("reconhecimento de identificador em comando de saída ou em expressão");
        Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> token.image.equals(simb.getIdentificador())).findAny().orElse(null);
        if(!(exist == null)){
            this.variavelIndexada = false;
            this.identificadorReconhecido = token.image;
        }else{
            this.listaErros.add("19 - Use of non-declared identifier: '"+token.image+"' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
        }
    }

    public void acao25() {
        System.out.println("reconhecimento de identificador de constante ou de variável e tamanho de variável indexada em comando de saída");
        // TODO implementar depois
    }

    public void acao26(Integer constInt){
        System.out.println("reconhecimento de constante inteira em comando de saída ou em expressão");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDI, new DataFrameK(DataTypeK.INTEGER, constInt)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao27(Float constReal){
        System.out.println("reconhecimento de constante real em comando de saída ou em expressão");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDR, new DataFrameK(DataTypeK.FLOAT, constReal)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao28(String constLiteral){
        System.out.println("reconhecimento de constante literal em comando de saída ou em expressão");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDS, new DataFrameK(DataTypeK.LITERAL, constLiteral)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao29(){
        //ACHO que é isso
        System.out.println("reconhecimento de fim de comando de seleção");
        this.pilhaDeDesvios.set(this.pilhaDeDesvios.size()-1, ponteiro);
    }

    public void acao30(){
        System.out.println(" reconhecimento da palavra reservada true");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMF, new DataFrameK(DataTypeK.NONE, '?')));
        this.ponteiro = this.ponteiro + 1;
        this.pilhaDeDesvios.add(this.ponteiro -1);
    }

    public void acao31(){
        System.out.println(" reconhecimento da palavra reservada untrue");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMT, new DataFrameK(DataTypeK.ADDRESS, '?')));
        this.ponteiro = this.ponteiro + 1;
        this.pilhaDeDesvios.add(this.ponteiro -1);
    }

    public void acao32(){
        System.out.println("reconhecimento da palavra reservada untrue (ou true)");
        this.pilhaDeDesvios.set(this.pilhaDeDesvios.size()-1, ponteiro+1);
        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMP, new DataFrameK(DataTypeK.NONE, '?')));
        this.ponteiro = this.ponteiro + 1;
        this.pilhaDeDesvios.add(this.ponteiro -1);
    }

    public void acao33(){
        System.out.println("reconhecimento do início de expressão em comando de repetição");
        this.pilhaDeDesvios.add(this.ponteiro);
    }

    public void acao34(){
        System.out.println("reconhecimento de expressão em comando de repetição");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMF, new DataFrameK(DataTypeK.INTEGER, '?')));
        this.ponteiro = this.ponteiro + 1;
        this.pilhaDeDesvios.add(this.ponteiro-1);
    }

//    //rever - Não entendi bem o que é para fazer nessa função;
//    public void acao35(){
//        System.out.println("reconhecimento do fim do comando de repetição");
//        Integer p = this.pilhaDeDesvios.get(this.pilhaDeDesvios.size()-1);
//        this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
//        p = ponteiro + 1;
//        this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
//        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMP, new DataFrameK(DataTypeK.INTEGER, p)));
//    }

// essa aqui deve ser a correta
    public void acao35(){
        System.out.println("reconhecimento do fim do comando de repetição");
        Integer desvioAcao31 = this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
        instructionList.get(desvioAcao31).setParameter(new DataFrameK(DataTypeK.ADDRESS, this.ponteiro + 1));
        Integer desvioAcao30 = this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMP, new DataFrameK(DataTypeK.ADDRESS, desvioAcao30)));
        this.ponteiro++;
    }

    public void acao36(){
        System.out.println("reconhecimento de operação relacional igual");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.EQL, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao37(){
        System.out.println("reconhecimento de operação relacional diferente");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.DIF, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao38(){
        System.out.println("reconhecimento de operação relacional menor");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.SMR, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao39(){
        System.out.println("reconhecimento de operação relacional maior");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.BGR, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao40(){
        System.out.println("reconhecimento de operação relacional menor igual");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.SME, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao41(){
        System.out.println("reconhecimento de operação relacional maior igual");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.BGE, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao42(){
        System.out.println("reconhecimento de operação aritmética adição");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.ADD, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao43(){
        System.out.println("reconhecimento de operação aritmética subtração");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.SUB, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao44(){
        System.out.println("reconhecimento de operação lógica OU ( | )");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.OR, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao45(){
        System.out.println("reconhecimento de operação aritmética multiplicação");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.MUL, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao46(){
        System.out.println("reconhecimento de operação aritmética divisão real");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.DIV, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao47(){
        System.out.println("reconhecimento de operação aritmética divisão inteira");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.DVW, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao48(){
        System.out.println("reconhecimento de operação aritmética resto da divisão inteira");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.MOD, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao49(){
        System.out.println("reconhecimento de operação lógica E (&)\n");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.AND, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao50(){
        System.out.println(" reconhecimento de operação aritmética potenciação");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.PWR, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = ponteiro + 1;
    }

    public void acao51(Token token){
        System.out.println(" reconhecimento de identificador de constante ou de variável e tamanho de variável indexada em expressão");
        Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> this.identificadorReconhecido.equals(simb.getIdentificador())).findAny().orElse(null);
        if(!this.variavelIndexada){
            if(exist.getAtributo2() == 0){
                instructionList.add(new InstructionK(InstructionK.Mnemonic.LDV, new DataFrameK(DataTypeK.ADDRESS, exist.getAtributo1())));
                this.ponteiro = this.ponteiro + 1;
            }else{
                this.listaErros.add("20 - Indexed variables requires an index: '"+this.identificadorReconhecido+"' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
            }
        }else{
            if(exist.getAtributo2() != 0){
                instructionList.add(new InstructionK(InstructionK.Mnemonic.LDV, new DataFrameK(DataTypeK.ADDRESS, exist.getAtributo1() + this.constanteInteira -1)));
                this.ponteiro = this.ponteiro + 1;
            }else{
                this.listaErros.add("20 - Identifier of non-indexed constant or variable: '"+this.identificadorReconhecido+"' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
            }
        }
    }

    public void acao52(){
        System.out.println("reconhecimento de constante lógica true");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDB, new DataFrameK(DataTypeK.BOOLEAN, true)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao53(){
        System.out.println("reconhecimento de constante lógica untrue");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDB, new DataFrameK(DataTypeK.BOOLEAN, false)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao54(){
        System.out.println("reconhecimento de operação lógica não ( ! )");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.NOT, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

}