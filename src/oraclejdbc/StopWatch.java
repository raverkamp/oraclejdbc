package oraclejdbc;

public class StopWatch {

    final long start;
    long last;

    public StopWatch() {
        start = System.currentTimeMillis();
        last = start;
    }

    public void mark(String s) {
        long a = System.currentTimeMillis();
        System.out.println(s + ": " + (a - start) + " " + (a - last));
        last = a;
    }

}
