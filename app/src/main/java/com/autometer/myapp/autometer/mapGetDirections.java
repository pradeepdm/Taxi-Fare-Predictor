package com.autometer.myapp.autometer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class mapGetDirections extends FragmentActivity implements
        LocationListener, Runnable {
    static LatLng ME = null;
    static LatLng toLocation = null;
    static LatLng fromLocation = null;

    static LatLng Destination = null;
    private static GoogleMap map = null;
    private double latituteField;
    private double longitudeField;
    private LocationManager locationManager;
    private String provider;
    private static ArrayList<LatLng> directionPoint;
    private static final String TAG_GSH = "AutoMeter";
    private static Context context = null;
    private static PolylineOptions rectLine;
    public boolean isFinished;
    private SensorManager m1SensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_get_directions);
        Bundle extras = getIntent().getExtras();
        String toLoc = null;
        String fromLoc=null;isFinished=false;
        boolean isCurr;
        context = this;
        m1SensorManager = (SensorManager) getSystemService(context.SENSOR_SERVICE);
        m1SensorManager.registerListener(mSensorListener, m1SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        Log.d("msensor manager",""+m1SensorManager.toString());

        if (extras != null) {
            toLoc = extras.getString("toLocation");
            fromLoc = extras.getString("fromLocation");
            isCurr = extras.getBoolean("cur_location");
            //isnight=extras.getBoolean("Itsnight");
            //Log.d(TAG_GSH, "ToLocation:    " + toLocation+" "+isnight);

            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            List<Address> addresses1;
            try {
                addresses = gcd.getFromLocationName(toLoc, 2);

                if (addresses.size() > 0) {
                    toLocation = new LatLng(addresses.get(0).getLatitude(),
                            addresses.get(0).getLongitude());
                }

                if(isCurr==false) {
                    addresses1 = gcd.getFromLocationName(fromLoc, 2);

                    if (addresses1.size() > 0) {
                        fromLocation = new LatLng(addresses1.get(0).getLatitude(),
                                addresses1.get(0).getLongitude());
                        Log.d("insdie_from_loca_check_me",""+fromLoc);
                 ME=fromLocation;
                    }
                }
                Log.d("Checking_me",""+fromLoc);

                map = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.map)).getMap();
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                List<String> providers = locationManager.getAllProviders();
                provider = locationManager.getBestProvider(criteria, true);
                //locationManager.requestSingleUpdate(provider, this, null);
//                boolean isGPSEnabled = locationManager
//                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//                // getting network status
//                boolean isNetworkEnabled = locationManager
//                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//                if (isNetworkEnabled) {
//                    provider = LocationManager.NETWORK_PROVIDER;
//                } else {
//                    provider = LocationManager.GPS_PROVIDER;
//                }
                Location location = locationManager.getLastKnownLocation(provider);
                Log.d(TAG_GSH, "Provider " + provider + " has been selected.");
                Log.d("here we get the location",""+location);
                if (location != null) {
                    System.out.println("Provider " + provider
                            + " has been selected.");
                    onLocationChanged(location);
                }
                if(isCurr==true)
                ME = new LatLng(latituteField, longitudeField);
                //Destination = new LatLng(43.044884, -76.129557);

                Log.d("Checking_me",""+ME+" "+isCurr+location);

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    (new Thread(new mapGetDirections())).start();
                }


                locationManager.requestLocationUpdates(provider, 20000, 0, this);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // Marker Rohit = map.addMarker(new MarkerOptions().position(ME)
        // .title("Rohit"));

        // Move the camera instantly to hamburg with a zoom of 15.
        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(ME, 1000));
        // Zoom in, animating the camera.
        // map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }


    @Override
    public void onStop(){
       //appMsg.setDuration(0);
     //appMsg.setDuration(0);
        //finish();
    isFinished=true;
        super.onStop();
    }

    public void onLocationChanged(Location location) {
        Log.d("On location called",""+location);
        double lat = (double) (location.getLatitude());
        double lng = (double) (location.getLongitude());
        latituteField = lat;
        longitudeField = lng;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_get_directions, menu);
        return true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

//    public void showCustomDialog(){
//        Dialog dialog = new Dialog(context, R.style.ThemeCustomDialog);
//
//        dialog.setContentView(R.layout.custom_dialog);
//        // ...
//
//        dialog.show();
//    }
@Override
public void onBackPressed() {
    super.onBackPressed();
    m1SensorManager.unregisterListener(mSensorListener);
    this.finish();
}
//
    @Override
    public void run() {
        // TODO Auto-generated method stub
        final GMapV2Direction md = new GMapV2Direction();

        final org.w3c.dom.Document doc = md.getDocument(ME, toLocation,
                GMapV2Direction.MODE_DRIVING);

        directionPoint = md.getDirection(doc);
        rectLine = new PolylineOptions().width(6).color(Color.BLUE);

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));
        }
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                        String info = "", dist;
                //int dist=md.getDistanceValue(doc);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                Log.d("Shared_preference_getter",""+preferences.getInt("Current_city",25));
                int base_price=preferences.getInt("Current_city",25);
                int min_distance=preferences.getInt("min_distance",2000);
                int perKm=preferences.getInt("perkm",11);
                float Nfare=preferences.getFloat("nightfare",0.25f);
                Log.d("Shared_preference_getter",""+preferences.getInt("Current_city",25)+ " "+preferences.getFloat("nightfare",0.25f)+" "+preferences.getInt("perkm",11)+" "+preferences.getInt("min_distance",2000));
                boolean isnight=SearchFragment.isNight;
                Log.d("my_location_is ",""+ME);
                int distance=md.getDistanceValue(doc);
                double totalcost=base_price;
                Log.d("shared_preference",""+distance+" "+isnight );
                //dist = "Approximate cost: " + "\u20A8" + " <b>" + totalcost+"<\b>\n";
                if(distance>min_distance && distance<50000){
                totalcost=base_price+((perKm*(distance-min_distance))/1000);
                    if(isnight)totalcost=totalcost+(totalcost*Nfare);
                //    dist = "Approximate cost: " + "\u20A8" + " <b>" + totalcost+"<\b>\n";
                }
                info="Aproximate Cost: <b>"+totalcost+"</b>"+"\n Aproximate Distance: <b>"+md.getDistanceText(doc)+"</b>" +"\n Approximate time to reach:<b> "+md.getDurationText(doc) + "</b>\n <p>Shake your phone to start <b> Navigation </b> using google maps</p>";
                final MediaPlayer mp = MediaPlayer.create(context, R.raw.bell);
                mp.start();
                if(distance>50000){
//                    m1SensorManager.unregisterListener(mSensorListener);
                    info="\n Auto Ride will cost you more than public transit, Its better to find an alternative";}
                if(distance<700){
                    info="\n You can walk "+md.getDistanceText(doc) +" and save Rs. <b>"+totalcost+"</b>\n";
//                m1SensorManager.unregisterListener(mSensorListener);
                }


                    //info += "  ";
                   // info += "\n Approximate time to reach: "+"<b>" + md.getDurationText(doc)+"</b>";

                    Log.d("popup_avlue", info);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage(Html.fromHtml(info));

                builder1.setCancelable(false);
                builder1.setPositiveButton("Go To Map",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                map.addPolyline(rectLine);
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(ME.latitude, ME.longitude,
                            1);
                    String fromAddress = null;
                    if (addresses.size() > 0) {
                        fromAddress = addresses.get(0).getAddressLine(0);
                    }

                    addresses = gcd.getFromLocation(toLocation.latitude,
                            toLocation.longitude, 1);
                    String toAddress = null;
                    if (addresses.size() > 0) {
                        toAddress = addresses.get(0).getAddressLine(0);
                    } else {
                        toLocation = ME;
                    }

                    map.addMarker(
                            new MarkerOptions()
                                    .position(toLocation)
                                    .title(toAddress)
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                            .showInfoWindow();
                    // .flat(false)
                    // .rotation(245));
                    map.addMarker(new MarkerOptions().position(ME).title(
                            fromAddress));
                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(directionPoint.get(0)).zoom(13).bearing(90)
                            .tilt(90).build();

                    // Animate the change in camera view over 2 seconds
                    map.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition), 2000, null);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // your UI code here

        });

    }
    private final SensorEventListener mSensorListener = new SensorEventListener()
    {
        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            Log.d("Inside listener",""+mAccel);
            if(mAccel > 6)
            {
                Intent intent = new  Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ fromLocation.latitude + "," + fromLocation.longitude + "&daddr=" + toLocation.latitude + "," + toLocation.longitude));
                intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                startActivity(intent);
                   m1SensorManager.unregisterListener(mSensorListener);
//
//  Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "sujaysudheendra@gmail.com"));
//                intent.putExtra(Intent.EXTRA_SUBJECT, "Auto Meter FeedBack");
//                startActivity(intent);
//                //Toast.makeText(getApplicationContext(), "shaked",	Toast.LENGTH_SHORT).show();
//                mAccel=0;
//                   mSensorManager.unregisterListener(mSensorListener);
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}

