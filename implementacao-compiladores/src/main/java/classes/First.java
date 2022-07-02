package classes;

public class First {
    static public final RecoverySet main = new RecoverySet();
    static public final RecoverySet body = new RecoverySet();
    static public final RecoverySet enumDeclarations = new RecoverySet();
    static public final RecoverySet list_of_commands = new RecoverySet();
    static public final RecoverySet end_of_file = new RecoverySet();
    static public final RecoverySet selection_command = new RecoverySet();

    static {
        main.add(LanguageParserConstants.DECLARATION);
        main.add(LanguageParserConstants.BODY);

        body.add(LanguageParserConstants.BODY);

        list_of_commands.add(LanguageParserConstants.READ);
        list_of_commands.add(LanguageParserConstants.WRITE);
        list_of_commands.add(LanguageParserConstants.DESIGNATE);
        list_of_commands.add(LanguageParserConstants.AVALIATE);
        list_of_commands.add(LanguageParserConstants.REPEAT);

        end_of_file.add(LanguageParserConstants.EOF);

        selection_command.add(LanguageParserConstants.TRUE);
        selection_command.add(LanguageParserConstants.UNTRUE);

        enumDeclarations.add(LanguageParserConstants.DECLARATION);
    }
}
