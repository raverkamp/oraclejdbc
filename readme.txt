
Dies ist ein Beispiel für das Arbeiten mit Oracle Arrays/Tables und JDBC.

Gemacht mit Netbeans.

Array1 ist ein Beispiel mit einem Varchar2(32000) array.
Dieser wird an die DB übergeben, vo dieser kopiert und wieder zurück
gegeben.

ArrayX macht das gleiche , aber mit einem Number Array, Varchar2 Array und Date Array.

Warum das ganze:

Komplexe Datentypen als Stored Procedure Parameter.
Es gibt keine möglichkeit diese direkt runter zu schicken,
Aber man könnte sie in die Arrays zerlegen und dann runterschicken.

Ich habe mir JPublisher angeschaut, JPublisher generiert Zugriff
Klassen auch für PL/SQL Packages.

Es werden Wrapper Typen und Wrapper Packages erzeugt.
Für einen PL/SQL Record wird ein Object-Type erzeugt, in dem Wrapper Package
wird dann umgewandelt.

ComplexArray:
Mit den Object Typen funktioniert es.
Mit dem Default Type Mapping kann man leben. Number -> BigDecimal, Date sqltimestamp


SuperComplexArray:

Datenstruktur ist:
Array(Record(Array(Record(number,string ,date)),Record(number,string ,date), number))

Daten kommen richtig zurück.


Data Dictionary Views
select * from user_type_attrs;
select * from user_coll_types


* Automatisch
ich will nicht mit den Oracle Structs und etc. rummachen.
Wenn ich einen Objecttypen bla(x integer,y varchar2(200),z date) habe
will ich in Java die Klasse bla {BigInteger x,String y,Date z) definieren
und der Rest läuft automatisch. 

Automatisches Mapping
Number <-> BigDecimal
Varchar2 <-> String
Date <-> Date
Collection <-> ArrayList

Was ist mit Subclassing?

API: new converter(connection,oraclename,javaclass)
converter.toDatum
converter.fromDatum

Von einer ArrayList kann man nicht die Elementklasse auslesen.

Structs:
was wird gemapped: mapping anhand des Namens?
die Klasse hat Felder und der struct hat Felder
Mappen anhand des Namens? Unabhängig Gross/klein
Annotations?

Class to Struct
Erzeuge Object Array der richtigen Länge.
Liste mit Attributen
 (java field,converter nach pl/sql,converter nach java,struct pos)

ArrayList?

arrayList,converter ist einfach,  den Struct Converter rein

Eigene Typemap?








