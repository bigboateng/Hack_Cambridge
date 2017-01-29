package go.hackcambridge.com.go.api;

import java.util.ArrayList;

import go.hackcambridge.com.go.models.AudioResponse;
import go.hackcambridge.com.go.models.Hotel;
import go.hackcambridge.com.go.models.Tour;
import go.hackcambridge.com.go.models.TourPostResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by boatengyeboah on 28/01/2017.
 */

public interface TourApi {

    @GET("/tours")
    Call<ArrayList<Tour>> getTours();

    @Multipart
    @POST("/tours")
    Call<AudioResponse> postTourAudio(@Part MultipartBody.Part file);

    @POST("/cat")
    Call<TourPostResponse> postTourData(@Body Tour tour);

    @GET("/hotel")
    Call<ArrayList<Hotel>> getHotels();
}
