package com.example.pc.mapproject1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsProjectActivity extends FragmentActivity implements OnMapReadyCallback {
    GPSTracker gps;
    Button mBtnRestaurant, mBtnHotel, mBtnATM, mBtnFind;
    double latitude = 16.0474325;
    double longitude = 108.1712201;
    private GoogleMap mMap;
    private static final String GOOGLE_API_KEY = "AIzaSyByMcDtsBADcE5VD5rkCVYaumfdQJJ3vTQ";
    private int PROXIMITY_RADIUS = 5000;
    private List<LatLng> markerPoints= null;
    private TextView mTvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_project);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gps = new GPSTracker(MapsProjectActivity.this);
        mapre();
        mBtnRestaurant = (Button) findViewById(R.id.btnRestaurant);
        mBtnHotel = (Button) findViewById(R.id.btnHotel);
        mBtnATM = (Button) findViewById(R.id.btnATM);
        mTvShow = (TextView)findViewById(R.id.tvShow);
        mBtnFind = (Button)findViewById(R.id.btnFind);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("It's me"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
      //  LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        String bestProvider = locationManager.getBestProvider(criteria, true);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
       // Location location = locationManager.getLastKnownLocation(bestProvider);
//        if (location != null) {
//            onLocationChanged(location);
//        }
//        locationManager.requestLocationUpdates(bestProvider, 20000, 0, (LocationListener) this);


        mBtnRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data("Restaurant");
            }
        });
        mBtnATM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data("ATM");
            }
        });
        mBtnHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data("Hotel");
            }
        });


mBtnFind.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                    routes(marker.getPosition());

                return false;
            }
        });
    }
});

    }
    private void Data(String name){
        Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
        //String type = mPlaceType[selectedPosition];
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + name );
        googlePlacesUrl.append("&keyword="+ name);
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = mMap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);

    }
    public void mapre(){
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }
    }
    private String getDirectionsUrl(LatLng origin,LatLng dest){
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String output = "json";
        String url =
                "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        return url;
    }

    /**
     * phương thức routes dùng để tìm đường đi trong lớp MapsActivity.
     */
    private void routes(LatLng latLng) {
        markerPoints = new ArrayList<LatLng>();
//      if (markerPoints.size() > 1) {
//            markerPoints.clear();
//        }
        // mMap.clear();
        Location mylocation = mMap.getMyLocation();
        LatLng mylatlng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
        markerPoints.add(mylatlng);
        markerPoints.add(latLng);
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
       }

        mMap.addMarker(options);
        if (markerPoints.size() == 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);
            String url = getDirectionsUrl(origin, dest);
            DownloadStackDistance downloadTask1 = new DownloadStackDistance(mMap,mTvShow);
            downloadTask1.execute(url);
        }
    }
}
