package oraclejdbc;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.OracleStruct;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.AttributeDescriptor;
import oracle.sql.Datum;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

public class ComplexArrayDeluxe {

    static final Logger logger = Logger.getGlobal();

    public static class A_record {

        // a number,b varchar2(32000),c date,d int
        BigDecimal a;
        String b;
        Date c;
        int d;

        @Override
        public String toString() {
            return "<a=" + a
                    + ", b=" + b
                    + ", c=" + c
                    + ", d=" + d + ">";
        }
    }

    static String ml(String... a) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < a.length; i++) {
            b.append(a[i]);
            b.append("\n");
        }
        return b.toString();
    }

    static StructDescriptor sd2;

    public static void run(OracleConnection oc) throws SQLException {
        mkTypes(oc);
        //ArrayDescriptor ad = ArrayDescriptor.createDescriptor("A_ARRAY", oc, true, true);
        StructDescriptor sd = StructDescriptor.createDescriptor("A_RECORD", oc);
        sd2 = sd;
        String sql = ml(" declare aa a_array; ",
                " ar a_record;",
                " begin ",
                " aa := new a_array(); ",
                " ar := new a_record(null,null,null,null); ",
                " for i in 1 .. 10 loop",
                "    ar.a :=  i*4.23;",
                "    ar.b := 'string '||i;",
                "    ar.c :=  trunc(sysdate)+ i*10;",
                "    ar.d := i;",
                " aa.extend();",
                " aa(aa.last) := ar;",
                " end loop;",
                " ? := aa;",
                "end;");
        ArrayList<Map<String, Object>> a;//arrayToMapsARRAY xa;
        try (CallableStatement cs = oc.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.ARRAY, "A_ARRAY");
            cs.execute();
            a = arrayToMaps((ARRAY) cs.getArray(1));
        }

        for (Map<String, Object> m : a) {
            A_record ar = new A_record();
            ar.a = (BigDecimal) m.get("A");
            ar.b = (String) m.get("B");
            ar.c = (java.util.Date) m.get("C");
            ar.d = ((BigDecimal) m.get("D")).intValue();
            System.out.println(ar);
        }
    }

    static void mkTypes(OracleConnection oc) throws SQLException {
        Ddl.createType(oc, "create type a_record as object(a number,b varchar2(32000),c date,d int)");
        Ddl.createType(oc, "create type A_ARRAY as table of a_record");
    }

    public static Map<String, Object> structToMap(STRUCT s) throws SQLException {
        HashMap<String, Object> res = new HashMap<>();
        Object[] o = s.getAttributes();
        StructDescriptor d = s.getDescriptor();
        d.initMetadataRecursively();
        AttributeDescriptor[] ad = d.getAttributesDescriptor();
        // ad is always null! even with d.initMetadataRecursively();
        // in oracle 12 jdbc this methood is deprecated
        // it seems the whole class StructDescriptor is deprecated
        ResultSetMetaData md = d.getMetaData();
        if (md.getColumnCount() != o.length) {
            throw new RuntimeException("length of meta data and values do not match");
        }
        for (int i = 0; i < o.length; i++) {
            res.put(md.getColumnName(i + 1), o[i]);
        }
        return res;
    }

    public static ArrayList<Map<String, Object>> arrayToMaps(ARRAY a) throws SQLException {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        Datum[] das = a.getOracleArray();
        for (int i = 0; i < das.length; i++) {
            res.add(structToMap((STRUCT) das[i]));
        }
        return res;
    }
}
