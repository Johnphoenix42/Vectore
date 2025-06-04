package apputil;

public class AppLogger {

    public static void log(Class<?> c, int line, String log){
        System.out.printf("\n%s %20s %40s \n", c.getName(), "Line "+line, log);
    }
}
