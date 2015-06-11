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
        OracleConnection oc = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@localhost:11521:xe", "bdms", "bdms");
        w.mark("con");
        ComplexArrayDeluxe.run(oc);
        oc = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@localhost:11521:xe", "bdms", "bdms");
        SuperComplexArray.run(oc);
       
        w.mark("fertig");
        w.mark("con");
        // it seems the types are cached in the connection, if the connection is not refreshed
        // there is a type error in the  ComplexArray.run 
        oc = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@localhost:11521:xe", "bdms", "bdms");
        ComplexArray.run(oc);
        w.mark("fertig");
        w.mark("con");
        Array1.run(oc);
        w.mark("total");
        ArrayX.run(oc);
        w.mark("total2");
        oc.close();

    }

}
