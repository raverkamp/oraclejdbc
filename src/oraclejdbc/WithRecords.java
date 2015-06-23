package oraclejdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.Timestamp;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;

public class WithRecords {
/* create a package with record type and a table type based on that record
    create a procedure which has an in parameter of this type and an out parameter of this
    type.
    No call this procedure from java by packaging up the parameters in three array, 
    one for each of the base type number, varchar2 and date. 
    The procedure is called in an sql block, there we unpack the data and call the
    procedure. The return data is packaged up inti the three arrays and returned to java.
    Java unpacks this data into the right data structure
    */
    
    static class Rec {

        public Double n;
        public String v;
        public Date d;
    }

    public static void run(OracleConnection oc) throws SQLException {

        Ddl.createType(oc, "create type N_ARRAY as table of number");
        Ddl.createType(oc, "create type V_ARRAY as table of varchar2(32000)");
        Ddl.createType(oc, "create type D_ARRAY as table of date");

        Ddl.call(oc, "create or replace package bla as "
                + "type rec is record(n number,v varchar2(200),d date);"
                + "type tab is table of rec;"
                + "procedure p1(rein tab,raus out tab);"
                + "end;");
        Ddl.call(oc, "create or replace package body bla as",
                "procedure p1(rein tab,raus out tab) is",
                "res tab;",
                "begin ",
                "res := tab();",
                "for i in rein.first .. rein.last loop",
                " res.extend();",
                " res(res.last) := rein(i);",
                " end loop;",
                "raus := res;",
                "end p1;",
                "end;");

        String[] lines = new String[]{
            "declare",
            "jn number := 0; ",
            "jv number :=0;",
            " jd number := 0; ",
            " na n_array := ?;",
            " va v_array := ?;",
            " da d_array := ?;",
            "l number;",
            " prein bla.tab;",
            " praus bla.tab;",
            " r bla.rec;",
            "begin",
            " jn := jn+1;",
            " l := na(jn);",
            " prein := bla.tab();",
            "for i1 in 1 .. l loop",
            " prein.extend();",
            " jn := jn+1; r.n := na(jn);",
            " jv := jv+1; r.v := va(jv);",
            " jd := jd +1; r.d := da(jd);",
            " prein(prein.last) := r;",
            " end loop;",
            " na := n_array();",
            " va := v_array();",
            " da := d_array();",
            " bla.p1(prein,praus);",
            " jn :=0;",
            " jv :=0;",
            " jd :=0;",
            " l := praus.last;",
            " jn := jn+1; na.extend;na(jn) := l;",
            " for i1 in praus.first .. praus.last loop",
            " r := praus(i1);",
            " jn := jn+1; na.extend;na(jn) := r.n;",
            " jv := jv+1; va.extend;va(jv) := r.v;",
            " jd := jd +1; da.extend;da(jd) := r.d;",
            " end loop;",
            "? := na;",
            "? := va;",
            "? := da;",
            "end;"};
        String sql = String.join("\n", lines);
        System.out.println(sql);
        ArrayList<Rec> rein = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Rec r = new Rec();
            r.n = i * 0.7;
            r.v = "bla " + i;
            r.d = new Date(12, 3, i);
            rein.add(r);
        }
        ArrayList<Double> an = new ArrayList<>();
        ArrayList<String> av = new ArrayList<>();
        ArrayList<Timestamp> ad = new ArrayList<>();
        int l1 = rein.size();
        an.add(new Double(l1));
        for (Rec r : rein) {
            an.add(r.n);
            av.add(r.v);
            ad.add(new Timestamp(r.d.getTime()));
        }
        oracle.sql.ARRAY na = (oracle.sql.ARRAY) oc.createARRAY("N_ARRAY", an.toArray(new Double[0]));
        oracle.sql.ARRAY va = (oracle.sql.ARRAY) oc.createARRAY("V_ARRAY", av.toArray(new String[0]));
        oracle.sql.ARRAY da = (oracle.sql.ARRAY) oc.createARRAY("D_ARRAY", ad.toArray(new Timestamp[0]));
        OracleCallableStatement stm = (OracleCallableStatement) oc.prepareCall(sql);
        stm.setARRAY(1, na);
        stm.setARRAY(2, va);
        stm.setARRAY(3, da);
        stm.registerOutParameter(4, OracleTypes.ARRAY, "N_ARRAY");
        stm.registerOutParameter(5, OracleTypes.ARRAY, "V_ARRAY");
        stm.registerOutParameter(6, OracleTypes.ARRAY, "D_ARRAY");
        stm.execute();
        ARRAY no = stm.getARRAY(4);
        ARRAY vo = stm.getARRAY(5);
        ARRAY do_ = stm.getARRAY(6);
        Object[] nx = (Object[]) no.getArray();
        Object[] vx = (Object[]) vo.getArray();
        Object[] dx = (Object[]) do_.getArray();
        int ni = -1;
        int vi = -1;
        int di = -1;
        ni++;
        int l2 = ((Number) nx[ni]).intValue();
        ArrayList<Rec> raus = new ArrayList<>();
        for (int i = 0; i < l2; i++) {
            Rec r = new Rec();
            ni++;
            r.n = ((Number) nx[ni]).doubleValue();
            vi++;
            r.v = (String) (vx[vi]);
            di++;
            r.d = new Date(((Timestamp) dx[di]).getTime());
            raus.add(r);
        }
        if (rein.size()!=raus.size()) {
            throw new RuntimeException("fail");
        }
        for(int i=0;i<rein.size();i++){
            Rec r1 = raus.get(i);
            Rec r2 = rein.get(i);
            if (!(r1.d.equals(r2.d) && r1.v.equals(r2.v) && r1.d.equals(r2.d))) {
                throw new RuntimeException("fail");
            }
        }
    }
}
