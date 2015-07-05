package oraclejdbc;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

import oracle.jdbc.*;

// the driver for all the examples
public class Main {

    static OracleConnection getConnection() throws SQLException {
        return (OracleConnection) DriverManager.getConnection(props.getProperty("url"),
                props.getProperty("user"), props.getProperty("pw"));
    }

    static Properties props;
    static Logger l = Logger.getGlobal();

    public static void main(String[] args) throws SQLException, Exception {

        String s = args[0];
        props = new java.util.Properties();
        props.load(new FileInputStream(s));
        DriverManager.registerDriver(new OracleDriver());
        try (OracleConnection oc = getConnection()) {
            ComplexArrayDeluxe.run(oc);
        }

        try (OracleConnection oc = getConnection()) {
            SuperComplexArray.run(oc);
        }

        try (OracleConnection oc = getConnection()) {
            ComplexArray.run(oc);
        }

        try (OracleConnection oc = getConnection()) {
            Array1.run(oc);
        }

        try (OracleConnection oc = getConnection()) {
            ArrayX.run(oc);
        }

        try (OracleConnection oc = getConnection()) {
            DbmsOutput.run(oc);
        }

        try (OracleConnection oc = getConnection()) {
            WithRecords.run(oc);
        }

    }

}
