package oraclejdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import oracle.jdbc.OracleConnection;
import static oraclejdbc.DbmsOutput.disableDbmsOutput;
import static oraclejdbc.DbmsOutput.enableDbmsOutput;
import static oraclejdbc.DbmsOutput.fetchDbmsOutput;

public class HtpOutput {

    static String fetchHTPOutput(Connection c) {
        try {
            StringBuilder b = new StringBuilder();
            try (CallableStatement cs = c.prepareCall("begin ? := htp.get_line(?); end;")) {
                cs.registerOutParameter(1, Types.VARCHAR);
                cs.registerOutParameter(2, Types.INTEGER);

                while (true) {
                    cs.execute();
                    String s = cs.getString(1);
                    if (cs.wasNull() || s == null) {
                        s = "";
                    }
                    b.append(s);
                    int x = cs.getInt(2);
                    if (cs.wasNull() || x == 0) {
                        break;
                    }
                }
            }
            return b.toString();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void run(OracleConnection oc) throws SQLException {

        Ddl.call(oc, "declare",
                "param_val   owa.vc_arr;",
                "begin",
                "--param_val (1) := 1;",
                "owa.init_cgi_env (param_val);",
                "for i in 1 .. 20 loop",
                "htp.p('line '||i);",
                "end loop;",
                "end;");
        String a = fetchHTPOutput(oc);
        System.out.println(a);

    }

}
