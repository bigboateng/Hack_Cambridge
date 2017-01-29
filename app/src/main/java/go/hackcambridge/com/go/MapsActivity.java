package go.hackcambridge.com.go;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import go.hackcambridge.com.go.api.TourApi;
import go.hackcambridge.com.go.models.Tour;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.orientation;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        SensorEventListener{


    private final String TAG = CLogTag.makeLogTag(MapsActivity.class);
    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    // user current location
    Location mCurrentLocation, mLastLocation;

    // camera position
    CameraPosition mCameraPosition;
    // ui
    private TextView mLatTv;
    private TextView mLongTv;
    FloatingActionButton mAddRecord;


    // Retrofit
    Retrofit mRetrofit;
    TourApi mTourApi;

    // tours
    ArrayList<Tour> mTours;

    // info window
    ImageButton mInfoButton;
    CardView mInfoCard;
    int mSelectedMarkerPos;

    MediaPlayer mMediaPlayer;
    boolean mIsPlayingAudio = false;
    boolean mIsNewTrack = true;
    float mDeclination;
    private float[] mRotationMatrix = new float[16];


    // languages spinner
    String[] mLanguageEntries = new String[]{"English", "French", "Spanish"};
    Spinner mLanguagesSpinner;

    // mode
    String mMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);
        mMode = getIntent().getStringExtra(IConstants.MODE);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(IConstants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mTourApi = mRetrofit.create(TourApi.class);
        mTours = new ArrayList();
        mapFragment.getMapAsync(this);
        createLocationRequest();
        setupCameraPosition();
        setupUi();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mInfoButton.setImageResource(R.drawable.ic_media_play_symbol);
                mInfoCard.setVisibility(View.GONE);
            }
        });

        // hide record button if tour mode

    }

    private void setupCameraPosition() {


    }

    private void setupUi() {
//        mLatTv = (TextView) findViewById(R.id.tv_Lat);
//        mLongTv = (TextView) findViewById(R.id.tv_Long);
        mLanguagesSpinner = (Spinner)findViewById(R.id.languagesSpinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mLanguageEntries);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguagesSpinner.setAdapter(spinnerArrayAdapter);
        //
        mAddRecord = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mAddRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ActivityRecord.class);
                intent.putExtra("lat", mCurrentLocation.getLatitude());
                intent.putExtra("long", mCurrentLocation.getLongitude());
                startActivityForResult(intent, IConstants.REQUEST_RECORD_ACTIVITY);
            }
        });
        mInfoCard = (CardView) findViewById(R.id.infoWindowCard);
        mInfoCard.setVisibility(View.GONE);
        mInfoButton = (ImageButton) findViewById(R.id.infoPlay);
        mInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlayingAudio) {
                    mIsPlayingAudio = false;
                    mMediaPlayer.pause();
                    mInfoButton.setImageResource(R.drawable.ic_media_play_symbol);
                } else {
                    mIsPlayingAudio = true;
                    if (mIsNewTrack) {
                        Tour t = mTours.get(mSelectedMarkerPos);
                        setMediaUrl(t.getAudioUrl());
                        mIsNewTrack = false;
                    }
                    mMediaPlayer.start();
                    mInfoButton.setImageResource(R.drawable.ic_pause_sign);
                }

            }
        });
    }

    private void setMediaUrl(String url) {
        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a marker in Sydney and move the camera

        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION")
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
//            mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
            getTours();

            //createLocationRequest();
        } else {
            // Show rationale and request permission.
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mIsNewTrack = true;
                mInfoCard.setVisibility(View.VISIBLE);
                int pos = (int) marker.getTag() - 1;
                mSelectedMarkerPos = pos;
//                Toast.makeText(getApplicationContext(), "Pos = " + pos, Toast.LENGTH_LONG).show();
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!mIsPlayingAudio) {
                    mInfoCard.setVisibility(View.GONE);
                }
            }
        });
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void getTours() {
        final Call<ArrayList<Tour>> call = mTourApi.getTours();
        call.enqueue(new Callback<ArrayList<Tour>>() {
            @Override
            public void onResponse(Call<ArrayList<Tour>> call, Response<ArrayList<Tour>> response) {
                if (!response.body().isEmpty()) {
                    for (Tour t : response.body()) {
                        mTours.add(t);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(t.getLati(), t.getLongi()))
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.ic_pin))));
                        marker.setTag(mTours.size());
                        marker.setZIndex(1.0f);
                    }
                } else {
                    Log.d(TAG, "NO DATA");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Tour>> call, Throwable t) {
                Log.d(TAG, "Error", t);
            }
        });

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void updateUi() {
        //mLatTv.setText(String.format("Lat: %s", mCurrentLocation.getLatitude()));
        //mLongTv.setText(String.format("Long %s", mCurrentLocation.getLongitude()));
        Log.d(TAG, "Update");
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                .zoom(18)
                .tilt(67.5f)
                .bearing(mCurrentLocation.getBearing())
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
//            SensorManager.getRotationMatrixFromVector(
//                    mRotationMatrix , event.values);
//            float[] orientation = new float[3];
//            SensorManager.getOrientation(mRotationMatrix, orientation);
//            float bearing = (float) (Math.toDegrees(orientation[0]) + mDeclination);
//            updateCamera(bearing);
//        }
    }

    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mCurrentLocation = location;
            GeomagneticField field = new GeomagneticField(
                    (float)location.getLatitude(),
                    (float)location.getLongitude(),
                    (float)location.getAltitude(),
                    System.currentTimeMillis()
            );

            // getDeclination returns degrees
            mDeclination = field.getDeclination();

            updateUi();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mCurrentLocation = mLastLocation;
//        if (mLastLocation != null) {
        updateUi();
//            mLatTv.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongTv.setText(String.valueOf(mLastLocation.getLongitude()));
//        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
