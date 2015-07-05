package oraclejdbc;

import java.sql.SQLException;
import oracle.jdbc.*;
import oracle.sql.*;

public class Array1 {

    /* send an array of string into the database, the database rverts the order
     of the array and returns it in an out parameter
     */
    public static void run(OracleConnection oc) throws SQLException {

        Ddl.createType(oc, "create type ZVARRAY as table of varchar2(32000)");

        String sql = "declare a zvarray; b zvarray; \n"
                + " begin \n"
                + " b:=  zvarray();\n "
                + "  a:=?; \n"
                + " for i in 1 .. a.last loop \n"
                + " b.extend(); \n"
                + " b(b.last) := a(a.last - i +1);\n"
                + " end loop;\n"
                + " ?:= b;\n"
                + "end;\n";

        String prefix = "kjdsfjhldshflksjdhflksjhflsjahfdlkjakjshdlkjshflksf ";
        String[] arg = new String[100000];
        for (int i = 0; i < arg.length; i++) {
            arg[i] = prefix + i;
        }

        OracleCallableStatement ocs = (OracleCallableStatement) oc.prepareCall(sql);
        oracle.sql.ARRAY a = (oracle.sql.ARRAY) oc.createARRAY("ZVARRAY", arg);

        ocs.setARRAY(1, a);
        ocs.registerOutParameter(2, OracleTypes.ARRAY, "ZVARRAY");

        ocs.execute();

        ARRAY b = ocs.getARRAY(2);
        Object[] oa = (Object[]) b.getArray();

        for (int i = 0; i < oa.length; i++) {
            if (!oa[i].equals(arg[oa.length - i - 1])) {
                throw new RuntimeException("fail");
            }
        }

    }

}
