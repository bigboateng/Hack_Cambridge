package go.hackcambridge.com.go;

/**
 * Created by boatengyeboah on 28/01/2017.
 */

public class CLogTag {

    static String makeLogTag(Class clazz) {
        return clazz.getSimpleName();
    }

}
