package oraclejdbc;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;


// oracle supports having a cursor as a column value
// the function resultSetToList expands column values that are cursors to lists
public class CursorInResultSet {
    public static void run(OracleConnection con) throws SQLException, IOException {
        try (PreparedStatement pstm = DBUtil.prepareSelect(con, "select view_name,"
                + " cursor(select column_name,data_type from all_tab_columns tc where tc.owner = t.owner and tc.table_name = t.view_name)"
                + " from all_views t where t.view_name like ?", new Object[]{"USER_TAB%"})) {
            try (OracleResultSet rs = (OracleResultSet) pstm.executeQuery()) {
                while (rs.next()) {
                    String tn = rs.getString(1);
                    OracleResultSet rs2 = (OracleResultSet) rs.getCursor(2);
                    System.out.println(tn);
                    List<Record> l = DBUtil.resultSetToList(rs2);
                    for (Record r : l) {
                        System.out.println("      " + r.getString("COLUMN_NAME") + " : " + r.getString("DATA_TYPE"));
                    }
                }
            }
        }
        
        try (PreparedStatement pstm = DBUtil.prepareSelect(con, "select view_name,"
                + " cursor(select column_name,data_type from all_tab_columns tc where tc.owner = t.owner and tc.table_name = t.view_name) as columns"
                + " from all_views t where t.view_name like ?", new Object[]{"USER_TAB%"})) {
            List<Record> l = DBUtil.resultSetToList(pstm.executeQuery());
            for(Record r : l) {
                r.dump("",System.out);
            }
        }
    }

}
