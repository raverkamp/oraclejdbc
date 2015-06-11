package oraclejdbc;

import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import static oraclejdbc.ArrayX.logger;

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
            logger.info(x.getMessage());
        }

        try {
            stm.execute(s);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }
}
