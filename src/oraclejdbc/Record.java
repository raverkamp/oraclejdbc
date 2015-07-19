package oraclejdbc;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.List;

// a simple class for materializing resultsets for Oracle
// support for cursor columns, these are materialized as List of Records, onyl for oracle

public class Record {

    List<String> fields;
    Object[] vals;

    public Record(List<String> fields) {
        this.fields = fields;
        vals = new Object[this.fields.size()];
    }

    public int size() {
        return vals.length;
    }

    public void set(String name, Object o) {
        int pos = fields.indexOf(name);
        if (pos < 0) {
            throw new RuntimeException("field not found: " + name);
        }
        vals[pos] = o;
    }

    public Object get(String name) {
        int pos = fields.indexOf(name);
        if (pos < 0) {
            throw new RuntimeException("field not found: " + name);
        }
        return vals[pos];
    }

    public Object get(int pos) {
        if (pos < 0) {
            throw new RuntimeException("position smaller than 0");
        }
        return vals[pos];
    }

    public String getString(String name) {
        return (String) get(name);
    }

    public Integer getInteger(String name) {
        Object o = get(name);
        if (o == null) {
            return null;
        } else {
            return ((BigDecimal) o).intValueExact();
        }
    }

    // write the record to some output
    // if a value is a list, they will printed as separate table
    public void dump(String prefix, Appendable s) throws IOException {
        ArrayList<List> lists = new ArrayList<>();
        ArrayList<String> lnams = new ArrayList<>();
        s.append(prefix);
        for (int i = 0; i < this.size(); i++) {
            Object o = this.get(i);
            if (o == null || !(o instanceof List)) {
                if (i > 0) {
                    s.append(", ");
                }
                s.append(this.fields.get(i));
                s.append(": " + o);
            } else {
                lists.add((List) o);
                lnams.add(fields.get(i));
            }
        }
        s.append("\n");
        for (int i = 0; i < lists.size(); i++) {
            s.append(prefix + "> " + lnams.get(i)+"\n");
            String newprefix = prefix + "  ";
            for (Object o2 : lists.get(i)) {
                if (o2 == null) {
                    s.append(newprefix + " null");
                    s.append("\n");
                } else if (o2 instanceof Record) {
                    ((Record) o2).dump(newprefix, s);
                } else {
                    s.append(newprefix + o2.toString());
                    s.append("\n");
                }
            }
        }
    }
}
