package oraclejdbc;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import oracle.jdbc.*;
import oracle.sql.*;

public class SuperComplexArray {

    static final Logger logger = Logger.getGlobal();

    static String ml(String... a) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < a.length; i++) {
            b.append(a[i]);
            b.append("\n");
        }
        return b.toString();
    }

    public static void run(OracleConnection oc) throws SQLException, Exception {
        mkTypes(oc);
        //ArrayDescriptor ad = ArrayDescriptor.createDescriptor("X_ARRAY", oc, true, true);
        String te = ml(" declare ",
                " xa x_array; ",
                " xr x_record := new x_record(null,null,null);  ",
                " ar a_record:= new a_record(null,null,null); ",
                " begin ",
                " xa := new x_array(); ",
                " for i in  1 .. 3 loop",
                "    declare aa a_array;",
                "   begin",
                "    aa := new a_array(); ",
                "   for j in 1 .. 2 loop ",
                "      ar.a :=i*10+j;",
                "      ar.b := 'a' ||(i*10+j) ; ",
                "      ar.c :=  sysdate +i*10+j; ",
                "     aa.extend();\n",
                "     aa(aa.last) := ar; ",
                "   end loop; ",
                "     ar.a:=  i; ",
                "     ar.b:=  'a'||i; ",
                "     ar.c:=  sysdate+i; ",
                "     xr.x := aa;  ",
                "     xr.y := ar; ",
                "     xr.z := i; ",
                "    xa.extend();",
                "    xa(xa.last) := xr;",
                "   end;",
                "   end loop; ",
                " ? := xa;",
                " dbms_output.put_line(xa(1).x.last); ",
                "end;");
        System.out.println(te);

        CallableStatement cs = oc.prepareCall(te);

        cs.registerOutParameter(1, OracleTypes.ARRAY, "X_ARRAY");

        cs.execute();

        ARRAY xa = (ARRAY) cs.getArray(1);
        cs.close();
        Datum[] das = xa.getOracleArray();
        for (Datum da : das) {
            XRecord xr = XRecord.fromDatum(da);
            System.out.println(xr);
            /*STRUCT s = (STRUCT) da;
             Object[] os = s.getAttributes();
             for (Object o : os) {
             System.out.print("  "+  o);
             }*/
        }

    }

    static void mkTypes(OracleConnection oc) throws SQLException {
        Ddl.createType(oc, "create type a_record as object(a number,b varchar2(32000),c date)");
        Ddl.createType(oc, "create type A_ARRAY as table of a_record");
        Ddl.createType(oc, "create type x_record as object(x a_array,y a_record,z number)");
        Ddl.createType(oc, "create type x_ARRAY as table of x_record");
    }

    static class ARecord {

        BigDecimal a;
        String b;
        Timestamp c;

        public String toString() {
            return "<" + a + " ," + b + ", " + c + ">";
        }

        static ARecord fromDatum(Datum d) throws Exception {
            STRUCT s = (STRUCT) d;
            Object[] os = s.getAttributes();
            ARecord res = new ARecord();
            res.a = (BigDecimal) os[0];
            res.b = (String) os[1];
            res.c = (Timestamp) os[2];
            return res;
        }

        public Datum toDatum(OracleConnection oc, StructDescriptor sd) throws Exception {
            Object[] os = new Object[]{a, b, c};
            return new STRUCT(sd, oc, os);
        }
    }

    static class XRecord {

        ArrayList<ARecord> x;
        ARecord y;
        BigDecimal z;

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("<");
            b.append("[");
            for (ARecord ar : x) {
                b.append(ar.toString());
                b.append("\n");
            }
            b.append("]\n");
            b.append(y.toString());
            b.append(z);
            b.append(">");
            return b.toString();
        }

        static XRecord fromDatum(Datum d) throws Exception {
            STRUCT s = (STRUCT) d;
            Object[] os = s.getAttributes();
            XRecord res = new XRecord();

            ARRAY xa = (ARRAY) os[0];
            Datum[] xad = xa.getOracleArray();
            ArrayList<ARecord> ala = new ArrayList<>();
            for (Datum v : xad) {
                ala.add(ARecord.fromDatum(v));
            }
            res.x = ala;
            STRUCT yd = (STRUCT) os[1];
            res.y = ARecord.fromDatum(yd);
            res.z = (BigDecimal) os[2];
            return res;
        }

        public Datum toDatum(OracleConnection oc, StructDescriptor sd) throws Exception {
            Object[] ol = new Object[x.size()];
            for (int i = 0; i < ol.length; i++) {
                ol[i] = x.get(i).toDatum(oc, sd);
            }
            ArrayDescriptor ad = (ArrayDescriptor) sd.getAttributesDescriptor()[0].getTypeDescriptor();
            ARRAY a = new ARRAY(ad, oc, ol);
            Datum stru = y.toDatum(oc, (StructDescriptor) sd.getAttributesDescriptor()[1].getTypeDescriptor());
            Object[] os = new Object[]{a, stru, z};
            return new STRUCT(sd, oc, os);
        }
    }
}
