package oraclejdbc;

import java.sql.*;

import java.util.*;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleTypes;

public class DBUtil {

    public static Connection getDBConnection(String conStr) throws Exception {
        String driverName = "oracle.jdbc.driver.OracleDriver";
        Class.forName(driverName);

        int p = conStr.indexOf("@");
        if (p <= 0) {
            throw new RuntimeException("expecting a connection string in the form \"user/pwd@tnsname\"");
        }
        String tns = conStr.substring(p + 1);
        int p2 = conStr.indexOf("/");
        if (p2 <= 0 || p2 >= p) {
            throw new RuntimeException("expecting a conenction string in the form \"user/pwd@tnsname\"");
        }
        String user = conStr.substring(0, p2);
        String pwd = conStr.substring(p2 + 1, p);
        Connection con;
        if (tns.contains(":")) {
            con = DriverManager.getConnection("jdbc:oracle:thin:@" + tns, user, pwd);
        } else {
            con = DriverManager.getConnection("jdbc:oracle:oci:@" + tns, user, pwd);
        }
        con.setAutoCommit(false);
        return con;
    }

    public static List<Record> resultSetToList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        ArrayList<String> fields = new ArrayList<>();
        for (int i = 0; i < md.getColumnCount(); i++) {
            fields.add(md.getColumnName(i + 1));
        }
        ArrayList<Record> res = new ArrayList<>();
        while (rs.next()) {
            Record r = new Record(fields);
            for (int i = 0; i < md.getColumnCount(); i++) {
                int ct = md.getColumnType(i + 1);
                if (ct == Types.VARCHAR) {
                    r.set(fields.get(i), rs.getString(i + 1));
                } else if (ct == Types.BIGINT || ct == Types.DECIMAL || ct == Types.NUMERIC || ct == Types.INTEGER) {
                    r.set(fields.get(i), rs.getBigDecimal(i + 1));
                } else if (ct== OracleTypes.CURSOR) {
                   ResultSet rs2 = ((OracleResultSet) rs).getCursor(i+1);
                   r.set(fields.get(i),resultSetToList(rs2));
                } else
                {
                    throw new RuntimeException("type not supported: " + ct + " for column " + fields.get(i));
                }

            }
            res.add(r);
        }
        return res;
    }


    public static PreparedStatement prepareSelect(Connection con, String sql, Object[] args) throws SQLException {
        PreparedStatement pstm = con.prepareStatement(sql);
        int i = 0;
        for (Object o : args) {
            if (o == null) {
                pstm.setString(i + 1, null);
            } else if (o instanceof Integer) {
                pstm.setInt(i + 1, (Integer) o);
            } else if (o instanceof String) {
                pstm.setString(i + 1, (String) o);
            } else {
                throw new RuntimeException("not supported paramter type");
            }
            i++;
        }
        return pstm;
    }

    public static List<Record> selectma(Connection con, String sql, Object[] args) throws SQLException {
        System.out.println(sql);
        try (PreparedStatement pstm = prepareSelect(con, sql, args);
                ResultSet rs = pstm.executeQuery()) {
            return resultSetToList(rs);
        }
    }

    public static List<Record> select(Connection con, String sql, Object... args) throws SQLException {
        return selectma(con, sql, args);
    }

    public static Record select01(Connection con, String sql, Object... args) throws SQLException {
        List<Record> l = selectma(con, sql, args);
        if (l.isEmpty()) {
            return null;
        }
        if (l.size() == 1) {
            return l.get(0);
        } else {
            throw new RuntimeException("too many rows with query: " + sql);
        }
    }

    public static Record select1(Connection con, String sql, Object... args) throws SQLException {
        List<Record> l = selectma(con, sql, args);
        if (l.isEmpty()) {
            throw new RuntimeException("no data found");
        }
        if (l.size() == 1) {
            return l.get(0);
        } else {
            throw new RuntimeException("too many rows");
        }
    }

    public static List<List<Record>> groupByma(List<Record> l, String[] cols) {
        ArrayList<List<Record>> res = new ArrayList<>();
        int pos = 0;
        while (pos < l.size()) {
            ArrayList<Record> g = new ArrayList<>();
            res.add(g);
            Object[] vals = new Object[cols.length];
            for (int i = 0; i < cols.length; i++) {
                vals[i] = l.get(pos).get(cols[i]);
            }
            g.add(l.get(pos));
            pos++;
            wloop:
            while (pos < l.size()) {
                for (int i = 0; i < cols.length; i++) {
                    Object o = l.get(pos).get(cols[i]);
                    if (o != null && !o.equals(vals[i]) || o == null && vals[i] != null) {
                        break wloop;
                    }
                }
                g.add(l.get(pos));
                pos++;
            }
        }
        return res;
    }

    public static List<List<Record>> groupBy(List<Record> l, String... cols) {
        return groupByma(l, cols);
    }
}
