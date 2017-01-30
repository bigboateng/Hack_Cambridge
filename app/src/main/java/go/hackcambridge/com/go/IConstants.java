package go.hackcambridge.com.go;

/**
 * Created by boatengyeboah on 28/01/2017.
 */

public interface IConstants {
    int REQUEST_RECORD_ACTIVITY = 100;
    int REQUEST_COMMENTS_ACTIVITY = 101;

    // api url
    String API_URL = "https://go-hackcambridge.herokuapp.com";
    String SKYSCANNER_URL = "http://partners.api.skyscanner.net/";
    String SKYSCANNER_API_KEY = "prtl6749387986743898559646983194";

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
