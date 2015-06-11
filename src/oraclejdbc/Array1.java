package oraclejdbc;

import java.sql.SQLException;
import java.util.logging.Logger;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;

public class Array1 {
    
    static final Logger logger = Logger.getGlobal();
    
    public static void run(OracleConnection oc) throws SQLException {
        StopWatch w = new StopWatch();
        mkTypes(oc);
        w.mark("ty da");
        String s = "declare a zvarray; b zvarray; \n"
                +" begin \n"
                +" b:=  zvarray();\n " 
                +"  a:=?; \n" 
                +" for i in 1 .. a.last loop \n"
                +" b.extend(); \n"
                + " b(b.last) := a(a.last - i +1);\n" 
                + " end loop;\n"
                +" ?:= b;\n"
                +"end;\n";
        System.out.println(s);
        String [] arg = new String[100000];
        for(int i=0;i<arg.length;i++) {
            arg[i] = "kjdsfjhldshflksjdhflksjhflsjahfdlkjakjshdlkjshflksf "+ i;
        }
        w.mark("before prepare");
        OracleCallableStatement ocs = (OracleCallableStatement) oc.prepareCall(s);
        oracle.sql.ARRAY a =   (oracle.sql.ARRAY) oc.createARRAY("ZVARRAY", arg);
        w.mark("before set");
        ocs.setARRAY(1, a);
        ocs.registerOutParameter(2, OracleTypes.ARRAY,"ZVARRAY");
         w.mark("before call");
        ocs.execute();
         w.mark("after call");
        ARRAY b = ocs.getARRAY(2);
       Object o = b.getArray();
        w.mark("fertig");
    }
    
    
    
    public static void mkTypes(OracleConnection oc) throws SQLException {
        OracleStatement s = (OracleStatement)oc.createStatement();
        try {
          s.execute("drop type ZVARRAY");
        } catch (SQLException x) {
            logger.info(x.getMessage());
        }
        s.execute("create type ZVARRAY as table of varchar2(32000)");
        
    }
    
}
