package oraclejdbc;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import oracle.sql.AttributeDescriptor;
import oracle.sql.Datum;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import static oraclejdbc.ComplexArray.logger;

/**
 *
 * @author rav
 */
public class Automatic {
    /*
     static class a_record {
     a_record(){}
     BigDecimal a;
     String b;
     Date c;
     }
     
     static a_record fromDatum() {
         
     return new a_record();
     }
     
     public abstract static class ToDatum {
     public abstract Datum convert(Object o);
     }
     
     public abstract static class FromDatum {
     public abstract Datum convert(Object o);
     }
     
     static class Feld {
     public Field javaField;
     public ToDatum toDatum;
     public FromDatum fromDatum;
     public int structPos;   
     }
     
     static class a_record_mananger {
         
     Feld[] felder;
         
     public a_record_mananger(OracleConnection oc) throws SQLException {
     StructDescriptor sd = StructDescriptor.createDescriptor("A_RECORD", oc);
     AttributeDescriptor[] as =  sd.getAttributesDescriptor();
     Field[] fields = a_record.class.getFields();
     ArrayList<Feld> felder = new ArrayList();
     for(Field f : fields) {
     for(AttributeDescriptor a:as) {
     if (a.getAttributeName().equalsIgnoreCase(f.getName())) {
     a.getTypeDescriptor().
     }
     }
     }
     }
         
     public a_record fromDatum(Datum d) throws SQLException, 
     IllegalArgumentException, 
     IllegalAccessException {
     STRUCT s = (STRUCT) d;
     Object[] o = s.getOracleAttributes();
     a_record res = new a_record();
     for(Feld f:felder) {
     f.javaField.set(res, f.fromDatum.convert(o[f.structPos]));
     }
             
     return res;
     }
     }
     
     static SuperComplexArray.ARecord fromDatum(Datum d) throws Exception {
     STRUCT s = (STRUCT) d;
     Object[] os = s.getAttributes();
     SuperComplexArray.ARecord res = new SuperComplexArray.ARecord();
     res.a = (BigDecimal) os[0];
     res.b = (String) os[1];
     res.c = (Timestamp) os[2];
     return res;
     }
     
     
    
     public static void run(OracleConnection oc) throws SQLException {
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
     */
}
