package oraclejdbc;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;

// a helper class to create an obejct type without body
public class Ddl {

    public static void createType(OracleConnection c, String s) {
        OracleStatement stm;
        try {
            stm = (OracleStatement) c.createStatement();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        StringTokenizer st = new StringTokenizer(s);
        String create = st.nextToken();
        String type = st.nextToken();
        String name = st.nextToken();

        try {
            stm.execute("drop type " + name + " force");
        } catch (SQLException x) {
            System.err.printf(x.getMessage());
        }

        try {
            stm.execute(s);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void call(OracleConnection con, String... s) throws SQLException {
        try (Statement stm = con.createStatement()) {
            stm.execute(String.join("\n", s));
        }
    }
}
