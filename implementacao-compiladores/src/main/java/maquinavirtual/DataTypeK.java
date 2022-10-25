package maquinavirtual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DataTypeK {
    INTEGER(1),
    FLOAT(2),
    LITERAL(3),
    BOOLEAN(4),
    ADDRESS(5),
    NONE(0);

    public final Integer id;

    private DataTypeK(Integer value) {
        this.id = value;
    }

    public static DataTypeK get(int id) {
        for (DataTypeK d : values()) {
            if (d.id.equals(id)) {
                return d;
            }
        }
        return null;
    }

    public static List<DataTypeK> getNumericDataTypes() {
        return new ArrayList<>(Arrays.asList(DataTypeK.FLOAT, DataTypeK.INTEGER));
    }
}