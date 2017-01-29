package go.hackcambridge.com.go;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import go.hackcambridge.com.go.api.TourApi;
import go.hackcambridge.com.go.models.AudioResponse;
import go.hackcambridge.com.go.models.Tour;
import go.hackcambridge.com.go.models.TourPostResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityRecord extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = CLogTag.makeLogTag(ActivityRecord.class);
    // state of record button
    private boolean isRecording = false;
    // UI
    Button mRecord, mReplay, mUpload;
    TextView recordTimeText;

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private long startTime = 0L;
    private Timer timer;

    // Media recorder
    MediaRecorder mRecorder;
    MediaPlayer mMediaPlayer;
    private String mAudioDir;

    // amazon stuff
    TransferUtility mTrasferUtility;
    TransferObserver mTransferObserver;
    Double mLat, mLong;

    Retrofit mRetrofit;
    TourApi mTourApi;
    EditText mAuthorEditText;

    // language chooser
    Spinner mLanguageChooserSpinner;
    String mChosenLanguage = IConstants.ENGLISH;

    // progress bar when posting
    ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record2);
        setupUi();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setAudioSamplingRate(16000);
        mAudioDir = this.getCacheDir().getPath()+ UUID.randomUUID().toString() + ".3gp";
        mRecorder.setOutputFile(mAudioDir);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // setup media player
        mMediaPlayer = new MediaPlayer();
        mLat = getIntent().getDoubleExtra("lat", 0.0f);
        mLong = getIntent().getDoubleExtra("long", 0.0f);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(IConstants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mTourApi = mRetrofit.create(TourApi.class);
        mProgress = new ProgressDialog(this);
    }

    private void setupUi() {
        mRecord = (Button) findViewById(R.id.record);
        mRecord.setOnClickListener(this);
        mReplay = (Button) findViewById(R.id.replay);
        mReplay.setOnClickListener(this);
        mUpload = (Button) findViewById(R.id.sendtoserver);
        mUpload.setOnClickListener(this);
        recordTimeText = (TextView) findViewById(R.id.recordingTime);
        mLanguageChooserSpinner = (Spinner)findViewById(R.id.languageChooserSpinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, IConstants.LANGUAGES);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguageChooserSpinner.setAdapter(spinnerArrayAdapter);
        mLanguageChooserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mChosenLanguage = IConstants.LANGUAGES[position];
                //Toast.makeText(getApplicationContext(), "Language = " + mChosenLanguage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mAuthorEditText = (EditText)findViewById(R.id.authorEditText);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record:
                if (isRecording) {
                    isRecording = false;
                    mRecord.setText("RECORD");
                    stoprecord();
                } else {
                    isRecording = true;
                    mRecord.setText("STOP");
                    startrecord();
                }
                break;
            case R.id.replay:
                try {
                    mMediaPlayer.setDataSource(mAudioDir);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.sendtoserver:
                // create upload service client
                mProgress.show();
                File file = new File(mAudioDir);
                // create RequestBody instance from file
                final RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(getMimeType(file.getAbsolutePath())),
                                file
                        );
                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("audio", file.getName(), requestFile);



                Call<AudioResponse> call = mTourApi.postTourAudio(body);

                call.enqueue(new Callback<AudioResponse>() {

                    @Override
                    public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
                        Tour tour = new Tour(mLong, mLat, System.currentTimeMillis());
                        tour.setAudioUrl(response.body().getUrl());
                        switch(mChosenLanguage) {
                            case IConstants.ENGLISH:
                                tour.setAudioUrlEng(response.body().getUrl());
                                break;
                            case IConstants.FRENCH:
                                tour.setAudioUrlFre(response.body().getUrl());
                                break;
                            case IConstants.Spanish:
                                tour.setAudioUrl(response.body().getUrl());
                                break;
                        }
                        tour.setAuthor(mAuthorEditText.getText().toString());
                        Call<TourPostResponse> call2 = mTourApi.postTourData(tour);
                        Log.d(TAG, "URL = " + response.body().getUrl());
                        postTourData(call2);
                    }

                    @Override
                    public void onFailure(Call<AudioResponse> call, Throwable t) {
                        Log.d(TAG, "Post error", t);
                    }
                });


                break;
        }
    }

    private void postTourData(Call call) {
        call.enqueue(new Callback<TourPostResponse>() {
            @Override
            public void onResponse(Call<TourPostResponse> call, Response<TourPostResponse> response) {
                if (response.body().isSuccess()) {
                    Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                    mProgress.hide();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error uploading audio", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<TourPostResponse> call, Throwable t) {

            }
        });
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    private void startrecord() {
        // TODO Auto-generated method stub
        mRecorder.start();
        startTime = SystemClock.uptimeMillis();
        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000);
    }

    private void stoprecord() {
        // TODO Auto-generated method stub
        mRecorder.stop();
        mRecorder.release();
        if (timer != null) {
            timer.cancel();
        }
//        if (recordTimeText.getText().toString().equals("00:00")) {
//            return;
//        }
//        recordTimeText.setText("00:00");
    }


    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            final String hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updatedTime)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(updatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(updatedTime)));
            long lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(updatedTime));
            System.out.println(lastsec + " hms " + hms);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (recordTimeText != null)
                            recordTimeText.setText(hms);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            });
        }


    }
}