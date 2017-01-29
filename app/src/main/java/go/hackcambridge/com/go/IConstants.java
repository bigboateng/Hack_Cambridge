package go.hackcambridge.com.go;

/**
 * Created by boatengyeboah on 28/01/2017.
 */

public interface IConstants {
    static int REQUEST_RECORD_ACTIVITY = 100;

    // api url
    String API_URL = "https://go-hackcambridge.herokuapp.com";

    // modes
    String MODE = "mode";
    String MODE_TOUR = "tour";
    String MODE_GUIDE = "guide";

    // language entries
    static String[] LANGUAGES = new String[]{"English", "French", "Spanish"};
    String ENGLISH = "English";
    String FRENCH = "French";
    String Spanish = "Spanish";
}
