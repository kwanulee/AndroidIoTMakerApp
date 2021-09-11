package com.example.myandroidiotmakerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private LocationCallback mLocationCallback;
    final private int REQUEST_PERMISSIONS_FOR_LOCATION_UPDATES = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button start_location_update_button = (Button) findViewById(R.id.device_button_start);
        start_location_update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationUpdates();
            }
        });

        Button stop_location_update_button = (Button) findViewById(R.id.device_button_stop);
        stop_location_update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocationUpdates();
            }
        });

    }

    private void startLocationUpdates() {
        // 1. 위치 요청 (Location Request) 설정
        LocationRequest locRequest =  LocationRequest.create();
        locRequest.setInterval(10000);
        locRequest.setFastestInterval(5000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // 2. 위치 업데이트 콜백 정의
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mLastLocation  = locationResult.getLastLocation();

                Log.d(TAG, "latitude : " + mLastLocation.getLatitude()+
                        ", longitude : " + mLastLocation.getLongitude());
                updateUI();
            }
        };

        // 3. 위치 접근에 필요한 권한 검사
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,            // MainActivity 액티비티의 객체 인스턴스를 나타냄
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},        // 요청할 권한 목록을 설정한 String 배열
                    REQUEST_PERMISSIONS_FOR_LOCATION_UPDATES    // 사용자 정의 int 상수. 권한 요청 결과를 받을 때
            );
            return;
        }

        // 4. 위치 업데이트 요청
        mFusedLocationClient.requestLocationUpdates(locRequest,
                mLocationCallback,
                null /* Looper */);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        switch (requestCode) {
            case REQUEST_PERMISSIONS_FOR_LOCATION_UPDATES: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationUpdates();

                } else {
                    Toast.makeText(this, "Permission required", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void updateUI() {
        double latitude = 0.0;
        double longitude = 0.0;
        float precision = 0.0f;

        TextView latitudeTextView = (TextView) findViewById(R.id.latitude_text);
        TextView longitudeTextView = (TextView) findViewById(R.id.longitude_text);
        TextView precisionTextView = (TextView) findViewById(R.id.precision_text);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            precision = mLastLocation.getAccuracy();
        }
        latitudeTextView.setText("Latitude: " + latitude);
        longitudeTextView.setText("Longitude: " + longitude);
        precisionTextView.setText("Precision: " + precision);
    }
}