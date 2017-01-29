package go.hackcambridge.com.go;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mGuideMode, mTourMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUi();
    }

    private void setupUi() {
        mGuideMode = (Button)findViewById(R.id.btnGuide);
        mTourMode = (Button)findViewById(R.id.btnTour);
        mGuideMode.setOnClickListener(this);
        mTourMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGuide:
                // guide mode
                Intent guideModeIntent = new Intent(MainActivity.this, MapsActivity.class);
                guideModeIntent.putExtra(IConstants.MODE, IConstants.MODE_TOUR);
                startActivity(guideModeIntent);
                break;
            case R.id.btnTour:
                // tour mode
                Intent tourModeIntent = new Intent(MainActivity.this, MapsActivity.class);
                tourModeIntent.putExtra(IConstants.MODE, IConstants.MODE_GUIDE);
                startActivity(tourModeIntent);
                break;
        }
    }
}
