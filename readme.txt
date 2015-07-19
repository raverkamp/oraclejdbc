This is an example/experiments of working with Oracle arrays / tables and JDBC.
Note that Oracle calls PL/SQL arrays "table".

Made with Netbeans.

* Array1
Array1 is an example of a VARCHAR2 (32000) array.
This is passed to the DB, the DB reverses this array and returns it.

* ArrayX

ArrayX does the same, but with a number array, varchar2 Array and a date array.

* ComplexArray
This time a array of records is passed to the database and returned under 
another name (well a structurally equivalent array of records)

* SuperComplexArray

Return a array of records, the records contains an array of records.
Some tools for extracting the data.

* ComplexArrayDeluxe
Return an array of records, some support to extract the data from the
oracle specific JDBC datatypes.

* WithRecords
create a package with record type and a table type based on that record
create a procedure which has an in parameter of this type and an out parameter of this
type.
Now call this procedure from java by packaging up the parameters in three array, 
one for each of the base type number, varchar2 and date. 
The procedure is called in an sql block, there we unpack the data and call the
procedure. The return data is packaged up inti the three arrays and returned to java.
ava unpacks this data into the right data structure.
This is a lot of work.

* Record, DBUtil
Simple classes for accesing Oracle database. Support for cursor columns.

* DbmsOutput
Class for retrieving dbms_output from database

* HtpOutput
Class for retrieving htp.p (and similar calls) output from database
