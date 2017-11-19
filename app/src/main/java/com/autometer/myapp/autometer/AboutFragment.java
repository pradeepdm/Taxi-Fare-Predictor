package com.autometer.myapp.autometer;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link interface
 * to handle interaction events.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AboutFragment extends android.support.v4.app.Fragment {
    private String title;
    private int page;
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    // newInstance constructor for creating fragment with arguments
    public static AboutFragment newInstance(int page, String title) {
        AboutFragment fragmentAbout = new AboutFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentAbout.setArguments(args);

        return fragmentAbout;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

        //uiHelper = new UiLifecycleHelper(this, callback);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
//        TextView tvLabel = (TextView) view.findViewById(R.id.tvlabel12);
//        tvLabel.setText(page + " -- " + title);
        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        Log.d("msensor manager",""+mSensorManager.toString());

        TextView tvlabel =(TextView) view.findViewById(R.id.tvlabel12);
        Typeface typ1= Typeface.createFromAsset(getActivity().getAssets(),"fonts/Capture_it.ttf");
        tvlabel.setTypeface(typ1);

        TextView detail =(TextView) view.findViewById(R.id.Details);
        Typeface typ= Typeface.createFromAsset(getActivity().getAssets(),"fonts/Courier_Prime.ttf");
        detail.setTypeface(typ);

        String det="\n \n Thank you for downloading our app. We hope you are happy with your purchase.\n If you would like to rate this app Just shake your phone. \n \n We do thoroughly test our apps, but it is impossible to test every phone combination. If you feel something is not working correctly with your app, please contact us so that we can help you or fix a bug and get a new release out for all to benefit.\n";

        detail.setText( Html.fromHtml(det) );
        detail.setLinksClickable(true);
        detail.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
        detail.setTextColor(Color.BLACK);
        return view;
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
            //Log.d("Inside listener",""+mAccel);
            if(mAccel > 9 && MyActivity.isAbout)
            {
                //getActivity().getPackageName()
                //Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                //Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
                    //startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                    //Toast.makeText(getApplicationContext(), "shaked",	Toast.LENGTH_SHORT).show();
                }
                mAccel=0;
                   mSensorManager.unregisterListener(mSensorListener);
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        //mSensorManager.registerListener(mSensorListener);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();

    }

}