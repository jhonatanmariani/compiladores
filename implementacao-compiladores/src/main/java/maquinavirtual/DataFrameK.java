package maquinavirtual;

public class DataFrameK {
    public final Object content;
    public final DataTypeK type;

    public DataFrameK(DataTypeK type, Object content) {
        this.content = content;
        this.type = type;
    }

    @Override
    public String toString() {
        String contentStr = null;
        switch (type) {
            case FLOAT -> {
                contentStr = (content).toString();
            }
            case INTEGER, ADDRESS -> {
                contentStr = ((Integer) content).toString();
            }
            case BOOLEAN -> {
                contentStr = ((Boolean) content).toString();
            }
            case LITERAL -> {
                contentStr = String.valueOf(content);
            }
        }
        if (contentStr == null) {
            return "0";
        }
        return String.format("%s", contentStr);
    }

    public String toDebugString() {
        return String.format("%s T: %s", this.toString(), type);
    }
}