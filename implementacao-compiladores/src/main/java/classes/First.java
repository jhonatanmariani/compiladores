package classes;

public class First {
    static public final RecoverySet main = new RecoverySet();
    static public final RecoverySet body = new RecoverySet();
    static public final RecoverySet enumDeclarations = new RecoverySet();
    static public final RecoverySet list_of_commands = new RecoverySet();
    static public final RecoverySet end_of_file = new RecoverySet();
    static public final RecoverySet selection_command = new RecoverySet();

    static {
        main.add(Language20221Constants.PALAVRA_RESERVADA_DECLARATION);
        main.add(Language20221Constants.PALAVRA_RESERVADA_BODY);

        body.add(Language20221Constants.PALAVRA_RESERVADA_BODY);

        list_of_commands.add(Language20221Constants.PALAVRA_RESERVADA_READ);
        list_of_commands.add(Language20221Constants.PALAVRA_RESERVADA_WRITE);
        list_of_commands.add(Language20221Constants.PALAVRA_RESERVADA_DESIGNATE);
        list_of_commands.add(Language20221Constants.PALAVRA_RESERVADA_AVALIATE);
        list_of_commands.add(Language20221Constants.PALAVRA_RESERVADA_REPEAT);

        end_of_file.add(Language20221Constants.EOF);

        selection_command.add(Language20221Constants.PALAVRA_RESERVADA_TRUE);
        selection_command.add(Language20221Constants.PALAVRA_RESERVADA_UNTRUE);

        enumDeclarations.add(Language20221Constants.PALAVRA_RESERVADA_DECLARATION);
    }
}
