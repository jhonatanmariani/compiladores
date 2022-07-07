package classes;

public class ErrorStruct {
    private ParseException error = null;
    private String msg = null;
    public ErrorStruct(String msg, ParseException error){
        this.error = error;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public ParseException getError() {
        return error;
    }

    public String expected(){
        String expectedMsg = "";
        for (int i=0; i < this.error.expectedTokenSequences.length; i++){
            expectedMsg += " ( ";
            for (int j=0; j < this.error.expectedTokenSequences[i].length; j++){
                expectedMsg += Language20221Constants.tokenImage[this.error.expectedTokenSequences[i][j]] + ", ";
            }
            expectedMsg += ") ";
        }
        return expectedMsg;
    }


    /*
    @Override
    public String toString() {
        return "ErrorStruct{" +
                "error=" + error+
                ", msg='" + msg + '\'' +
                '}';
                //error.getMessage()+ msg;
    }
    */
    /*
    @Override
    public String toString() {
        String expectedMsg = "";

        for (int i=0; i < this.error.expectedTokenSequences.length; i++){
            expectedMsg += " ( ";
            for (int j=0; j < this.error.expectedTokenSequences[i].length; j++){
                expectedMsg += Language20221Constants.tokenImage[this.error.expectedTokenSequences[i][j]] + ", ";
            }
            expectedMsg += ") ";
        }
        return expectedMsg;
    }*/

    @Override
    public String toString() {
        String expectedMsg = "";

        for (int i=0; i < this.error.expectedTokenSequences.length; i++){
            expectedMsg += " ( ";
            for (int j=0; j < this.error.expectedTokenSequences[i].length; j++){
                expectedMsg += Language20221Constants.tokenImage[this.error.expectedTokenSequences[i][j]] + ", ";
            }
            expectedMsg += ") \n";
        }
        return msg + "Esperado(s): " + expectedMsg ;
    }

}
