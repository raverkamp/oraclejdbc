package oraclejdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import oracle.jdbc.*;
import oracle.sql.*;

public class Main {

    
    static final Logger logger = Logger.getGlobal();
    
    public static void main(String[] args) throws SQLException, Exception {
      StopWatch w = new StopWatch();
      w.mark("start");
        DriverManager.registerDriver(new OracleDriver());
        Connection c = DriverManager.getConnection("jdbc:oracle:thin:@localhost:11521:xe","bdms","bdms");
        OracleConnection oc = (OracleConnection) c;
        w.mark("con");
        SuperComplexArray.run(oc);
        w.mark("fertig");
        /*w.mark("con");
        Array1.run(oc);
        w.mark("total");
        ArrayX.run(oc);
        w.mark("total2");
          */     
               
    }
    
}
