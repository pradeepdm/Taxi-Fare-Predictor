package com.autometer.myapp.autometer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@linfactory method to
 * create an instance of this fragment.
 *
 */
public class SearchFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener {
    // Store instance variables
    private String title;
    private int page;
    private AutoCompleteTextView autoCompViewto;
    private AutoCompleteTextView autoCompViewfrom;
    private Button curLoc;
    private CircleButton search;
    private boolean isCurr;
    private Context context;
    private CheckBox Night_fare;
    public static boolean isNight,isCitySelected;
    private AlertDialog levelDialog;
    private TextView citySelected;
    private Button citychanged;

    // newInstance constructor for creating fragment with arguments
    public static SearchFragment newInstance(int page, String title) {
        SearchFragment fragmentSearch = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentSearch.setArguments(args);
        return fragmentSearch;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        context=getActivity();
        isCitySelected=false;
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        autoCompViewto = (AutoCompleteTextView) view.findViewById(R.id.autoComplete2);
        citychanged=(Button) view.findViewById(R.id.citychange);
        citySelected=(TextView) view.findViewById(R.id.city);
        //citySelected.setText("City Selected \n Default (Bangalore)");
        citychanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                displayCitylist();
            }
        });
        autoCompViewto.setAdapter(new PlacesAutoCompleteAdapter(context, R.layout.list_item));
        autoCompViewto.setOnItemClickListener(this);

        autoCompViewto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCitySelected) {
                    isCitySelected=true;
                }
            }
        });


        autoCompViewfrom = (AutoCompleteTextView) view.findViewById(R.id.autoComplete1);
        autoCompViewfrom.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isCitySelected) {
                displayCitylist();
                isCitySelected=true;
            }
        }
    });
        autoCompViewfrom.setAdapter(new PlacesAutoCompleteAdapter(context, R.layout.list_item));
        autoCompViewfrom.setSelection(0);
        autoCompViewfrom.setOnItemClickListener(this);
        isCurr=false;
        Night_fare=(CheckBox) view.findViewById(R.id.NightFare);
        //Night_fare.isChecked(false);
        isNight=false;
        search= (CircleButton) view.findViewById(R.id.search_btn);
        curLoc=(Button) view.findViewById(R.id.Cur_button);
        curLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCurr=true;
                autoCompViewfrom.setText("My Location");
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoCompViewto.getText() == null || autoCompViewfrom.getText() == null || autoCompViewto.getText().length() == 0 || autoCompViewfrom.getText().length() == 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Please Enter the From and To Address...!!!");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else {

                    isNight = false;
                    if (Night_fare.isChecked()) isNight = true;
                    Log.d("shared_preference", " " + isNight);
                    Intent intent = new Intent(context, mapGetDirections.class);
                    intent.putExtra("toLocation", autoCompViewto.getText().toString());
                    intent.putExtra("fromLocation", autoCompViewfrom.getText().toString());
                    intent.putExtra("cur_location", isCurr);
                    intent.putExtra("Itsnight", isNight);
                    isCurr = false;
                    isCitySelected=false;
                    Night_fare.setChecked(false);
                    startActivity(intent);
                    autoCompViewfrom.getText().clear();
                    autoCompViewto.getText().clear();
                }
            }
        });
        return view;
    }

    public void displayCitylist(){
        // Strings to Show In Dialog with Radio Buttons
        final CharSequence[] items = {" Banglore ", " Hyderabad ", " Chennai ", " Delhi ", " Mumbai ", " Kolkata ", "Ahmedabad"};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Your City");
        //final AlertDialog finalLevelDialog = levelDialog;
        //final AlertDialog finalLevelDialog = levelDialog;
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor1 = preferences.edit();

                switch (item) {
                    case 0: {
                        editor1.putInt("Current_city", preferences.getInt("BLR", 25));
                        editor1.putFloat("nightfare", 0.5f);
                        editor1.putInt("min_distance", 2000);
                        editor1.putInt("perkm", 11);
                        editor1.apply();
                        Log.d("Banglore", "" + preferences.getInt("BLR", 25));
                        Log.d("Cur", "" + preferences.getInt("Current_city", 25));
                        citySelected.setText("City: Bangalore");
                        break;
                    }
                    case 1: {
                        editor1.putInt("Current_city", preferences.getInt("HYD", 20));
                        editor1.putFloat("nightfare", 0.5f);
                        editor1.putInt("min_distance", 1600);
                        editor1.putInt("perkm", 11);
                        editor1.apply();
                        Log.d("HYD", "" + preferences.getInt("HYD", 20));
                        Log.d("Cur", "" + preferences.getInt("Current_city", 20));
                        citySelected.setText("City: Hyderabad");
                        break;
                    }
                    case 2: {
                        editor1.putInt("Current_city", preferences.getInt("CHE", 25));
                        editor1.putFloat("nightfare", 0.5f);
                        editor1.putInt("min_distance", 1800);
                        editor1.putInt("perkm", 12);
                        editor1.apply();
                        Log.d("CHE", "" + preferences.getInt("CHE", 25));
                        Log.d("Cur", "" + preferences.getInt("Current_city", 25));
                        citySelected.setText("City: Chennai");
                        break;
                    }
                    case 3: {
                        editor1.putInt("Current_city", preferences.getInt("DEL", 25));
                        editor1.putFloat("nightfare", 0.25f);
                        editor1.putInt("min_distance", 2000);
                        editor1.putInt("perkm", 8);
                        editor1.apply();
                        citySelected.setText("City: Delhi");
                        break;
                    }
                    case 4: {
                        editor1.putInt("Current_city", preferences.getInt("MUM", 18));
                        editor1.putFloat("nightfare", 0.25f);
                        editor1.putInt("min_distance", 1500);
                        editor1.putInt("perkm", 11);
                        editor1.apply();
                        Log.d("MUM", "" + preferences.getInt("MUM", 25));
                        Log.d("Cur", "" + preferences.getInt("Current_city", 25));
                        citySelected.setText("City: Mumbai");
                        break;
                    }
                    case 5: {
                        editor1.putInt("Current_city", preferences.getInt("KOL", 25));
                        editor1.putFloat("nightfare", 0.0f);
                        editor1.putInt("min_distance", 2000);
                        editor1.putInt("perkm", 12);
                        editor1.apply();
                        Log.d("KOL", "" + preferences.getInt("KOL", 25));
                        Log.d("Cur", "" + preferences.getInt("Current_city", 25));
                        citySelected.setText("City: Kolkata");
                        break;
                    }
                    case 6: {
                        editor1.putInt("Current_city", preferences.getInt("AMD", 11));
                        editor1.putFloat("nightfare", 0.25f);
                        editor1.putInt("min_distance", 1400);
                        editor1.putInt("perkm", 8);
                        editor1.apply();
                        Log.d("AMD", "" + preferences.getInt("AMD", 11));
                        Log.d("Cur", "" + preferences.getInt("Current_city", 11));
                        citySelected.setText("City: Ahmedabad");
                        break;
                    }
                    default:
                        editor1.putInt("Current_city", preferences.getInt("BLR", 25));
                        editor1.putInt("min_distance", 2000);
                        editor1.putFloat("nightfare", 0.5f);
                        editor1.putInt("perkm", 11);
                        editor1.apply();
                        citySelected.setText("City: Bangalore");
                }
                levelDialog.dismiss();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}