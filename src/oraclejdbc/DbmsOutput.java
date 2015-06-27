package oraclejdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

/*
 fetch dbmsoutput fromdatabase 
 */
public class DbmsOutput {

    public static void run(OracleConnection oc) throws SQLException {
        enableDbmsOutput(oc, 0);
        // just enabled, expect an empty array
        for (String a : fetchDbmsOutput(oc)) {
            throw new RuntimeException("aua");
        }
        Ddl.call(oc, "begin ",
                "for i in 1 .. 20 loop",
                "dbms_output.put_line('line '||i);",
                "end loop;",
                "end;");
        for (String a : fetchDbmsOutput(oc)) {
            System.out.println(a);
        }
        disableDbmsOutput(oc);
        // if disabled we expect a null
        if (null != fetchDbmsOutput(oc)) {
            throw new RuntimeException("aua");
        }

    }

    public static ArrayList<String> fetchDbmsOutput(Connection con) throws SQLException {
        /*        DBMS_OUTPUT.GET_LINES (
         lines       OUT     DBMSOUTPUT_LINESARRAY,
         numlines    IN OUT  INTEGER);*/
        try (OracleCallableStatement cstm = (OracleCallableStatement) con.prepareCall("begin dbms_output.get_lines(lines=> ?,numlines=> ?);end;")) {

            cstm.setInt(2, Integer.MAX_VALUE);
            cstm.registerOutParameter(1, OracleTypes.ARRAY, "DBMSOUTPUT_LINESARRAY");
            cstm.registerOutParameter(2, Types.INTEGER);
            cstm.execute();
            ARRAY arr = cstm.getARRAY(1);
            if (arr == null) {
                return null;
            }
            Object[] lines = (Object[]) arr.getArray();
            int numlines = cstm.getInt(2);
            ArrayList<String> res = new ArrayList<>();
            if (lines != null) {
                for (Object o : lines) {
                    if (o == null) {
                        res.add("");
                    } else {
                        res.add(o.toString());
                    }
                }
            }
            return res;
        }

    }

    public static void enableDbmsOutput(Connection c, int size) throws SQLException {
        try (CallableStatement cstm = c.prepareCall("begin dbms_output.enable(buffer_size => ?);end;")) {
            if (size <= 0) {
                cstm.setNull(1, Types.INTEGER);
            } else {
                cstm.setInt(1, size);
            }
            cstm.execute();
        }
    }

    public static void disableDbmsOutput(Connection c) throws SQLException {
        try (CallableStatement cstm = c.prepareCall("begin dbms_output.disable;end;")) {
            cstm.execute();
        }
    }
}
