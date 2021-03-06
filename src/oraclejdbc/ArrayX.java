package oraclejdbc;

import java.math.BigDecimal;
import java.sql.*;
import oracle.jdbc.*;
import oracle.sql.*;

public class ArrayX {
    /* create three array types, for number, string and date in oracle
     send three arrays to oracle revert then and return them in
     out parameters
     */

    public static void run(OracleConnection oc) throws SQLException {

        Ddl.createType(oc, "create type N_ARRAY as table of number");
        Ddl.createType(oc, "create type V_ARRAY as table of varchar2(32000)");
        Ddl.createType(oc, "create type D_ARRAY as table of date");

        String sql = ml(
                "declare",
                " na N_ARRAY;",
                " va V_ARRAY;",
                " da D_ARRAY;",
                " no N_ARRAY;",
                " vo V_ARRAY;",
                " do D_ARRAY;",
                "begin",
                "  na :=?;",
                "  va :=?;",
                "  da :=?;",
                "  no := N_ARRAY();",
                "  vo := V_ARRAY();",
                "  do := D_ARRAY();",
                "for i in na.first .. na.last loop",
                " no.extend();",
                " no(i) := na(na.last -i +1);",
                " end loop;",
                "for i in va.first .. va.last loop",
                " vo.extend();",
                " vo(i) := va(va.last -i +1);",
                " end loop;",
                "for i in da.first .. da.last loop",
                " do.extend();",
                " do(i) := da(da.last -i +1);",
                " end loop;",
                "?:= no;",
                "?:= vo;",
                "?:= do;",
                "end;");

        OracleCallableStatement ocs = (OracleCallableStatement) oc.prepareCall(sql);

        BigDecimal[] bargs = new BigDecimal[100];
        for (int i = 0; i < bargs.length; i++) {
            bargs[i] = BigDecimal.valueOf(i * 1123 / 32);
        }
        oracle.sql.ARRAY na = (oracle.sql.ARRAY) oc.createARRAY("N_ARRAY", bargs);
        ocs.setARRAY(1, na);

        String[] vargs = new String[108];
        for (int i = 0; i < vargs.length; i++) {
            vargs[i] = "" + BigDecimal.valueOf(i * 1123 / 32);
        }
        oracle.sql.ARRAY va = (oracle.sql.ARRAY) oc.createARRAY("V_ARRAY", vargs);
        ocs.setARRAY(2, va);

        Timestamp[] dargs = new Timestamp[235];
        for (int i = 0; i < dargs.length; i++) {
            // yes ! year -1900
            dargs[i] = new Timestamp(113, 2, 8, 23, 12, 3, 0);
        }
        oracle.sql.ARRAY da = (oracle.sql.ARRAY) oc.createARRAY("D_ARRAY", dargs);
        ocs.setARRAY(3, da);

        ocs.registerOutParameter(4, OracleTypes.ARRAY, "N_ARRAY");
        ocs.registerOutParameter(5, OracleTypes.ARRAY, "V_ARRAY");
        ocs.registerOutParameter(6, OracleTypes.ARRAY, "D_ARRAY");

        ocs.execute();

        ARRAY no = ocs.getARRAY(4);
        ARRAY vo = ocs.getARRAY(5);
        ARRAY do_ = ocs.getARRAY(6);
        Object[] nx = (Object[]) no.getArray();
        Object[] vx = (Object[]) vo.getArray();
        Object[] dx = (Object[]) do_.getArray();
        if (nx.length != bargs.length) {
            throw new RuntimeException("fail");
        }
        if (vx.length != vargs.length) {
            throw new RuntimeException("fail");
        }
        if (dx.length != dargs.length) {
            throw new RuntimeException("fail");
        }
    }

    static String ml(String... args) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                b.append("\n");
            }
            b.append(args[i]);
        }
        return b.toString();
    }
}
