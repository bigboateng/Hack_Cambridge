package go.hackcambridge.com.go.api;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import go.hackcambridge.com.go.models.Hotel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by boatengyeboah on 29/01/2017.
 */

public interface SkyScannerApi {


    @Headers("Content-Type:application/json")
    @GET("/apiservices/hotels/liveprices/v2/UK/EUR/en-GB/27539698/2017-01-29/2017-01-31/2/1")
    Call<ArrayList<Hotel>> getData(@Query("apiKey") String apiKey);
}
