package oraclejdbc;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Logger;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.Datum;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

/**
 *
 * @author rav
 */
public class ComplexArray {

    static final Logger logger = Logger.getGlobal();

    public static void run(OracleConnection oc) throws SQLException {
        mkTypes(oc);
        ArrayDescriptor ad = ArrayDescriptor.createDescriptor("A_ARRAY", oc, true, true);
        StructDescriptor sd = StructDescriptor.createDescriptor("A_RECORD", oc);
        STRUCT s1 = new STRUCT(sd, oc, new Object[]{4711, "blax", new Date(2001, 12, 7)});
        STRUCT s2 = new STRUCT(sd, oc, new Object[]{447712, "asasblax", new Date(2012, 12, 7)});
        Object[] ol = new Object[]{s1, s2};
        ARRAY a = new ARRAY(ad, oc, ol);
        CallableStatement cs = oc.prepareCall(
                " declare aa a_array; "
                + " xa x_array; "
                + " xr x_record;"
                + " begin "
                + " aa:=?;"
                + " xa := new x_array(); "
                + " xr := new x_record(null,null,null); "
                + " for i in aa.first .. aa.last loop"
                + "    xr.x := aa(i).a;"
                + "    xr.y := aa(i).b;"
                + "    xr.z := aa(i).c;"
                + " xa.extend();"
                + " xa(xa.last) := xr;"
                + " end loop;"
                + " ? := xa;"
                + "end;");
        cs.setArray(1, a);
        cs.registerOutParameter(2, Types.ARRAY, "X_ARRAY");
        cs.execute();
        ARRAY xa = (ARRAY) cs.getArray(2);
        cs.close();
        Datum[] das = xa.getOracleArray();
        for (Datum da : das) {
            System.out.println(da);
            STRUCT s = (STRUCT) da;
            Object[] os = s.getAttributes();
            for (Object o : os) {
                System.out.print("  " + o.getClass() + ":" + o.toString());
            }
        }
    }

    static void mkTypes(OracleConnection oc) throws SQLException {
        OracleStatement s = (OracleStatement) oc.createStatement();
        for (String ty : new String[]{"a_array", "x_array", "a_record", "x_record"}) {
            try {
                s.execute("drop type " + ty);
            } catch (SQLException x) {
                logger.info(x.getMessage());
            }
        }
        s.execute("create type a_record as object(a number,b varchar2(32000),c date)");
        s.execute("create type x_record as object(x number,y varchar2(32000),z date)");
        s.execute("create type A_ARRAY as table of a_record");
        s.execute("create type x_ARRAY as table of x_record");
    }
}
