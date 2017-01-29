package go.hackcambridge.com.go;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by boatengyeboah on 28/01/2017.
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter{

    private View mContentView;
    boolean isPlaying = false;
    Context mContext;
    public CustomInfoWindow(Context ctx) {
        mContext = ctx;
        mContentView = LayoutInflater.from(ctx).inflate(R.layout.info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "toast from marker", Toast.LENGTH_LONG).show();
            }
        });
        return mContentView;
    }



    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
