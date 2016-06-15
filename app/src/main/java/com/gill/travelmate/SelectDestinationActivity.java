package com.gill.travelmate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.gill.travelmate.utils.FontHelper;
import com.gill.travelmate.utils.GeneralValues;
import com.gill.travelmate.utils.TinyDB;
import com.gill.travelmate.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

//Select deatination on this activity
public class SelectDestinationActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView cross;
    private AutoCompleteTextView destinationTextView;
    private TextView enterTextView, title;
    private Animation animationFadeIn;
    private Intent i;
    private String userID = "", UNAME = "",EMAIL="";
    private Context mContext;
    private TinyDB tinyDB;
    private double lat=0,lng=0;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        FontHelper.applyFont(this, findViewById(R.id.container_destination), "bauhaus.ttf");

        mContext=SelectDestinationActivity.this;
        tinyDB=new TinyDB(mContext);

        dialog=Utils.getProgressDialog(mContext);

        initializeViews();
        setListener();

        userID = getIntent().getStringExtra("uid");
        UNAME = getIntent().getStringExtra("uname");
        EMAIL = getIntent().getStringExtra("email");
        Utils.show_log("uid = " + userID + " name = " + UNAME);

        destinationTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() > 0 && str.startsWith(" ")) {
                    destinationTextView.setText(str.trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destinationTextView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.search_list_item));
        destinationTextView.setOnItemClickListener(this);
        destinationTextView.setText(tinyDB.getString(GeneralValues.USER_DESTINATION_KEY));
    }

    @Override
    public void onBackPressed() {
    }

    //initialize views
    public void initializeViews() {
        cross = (ImageView) findViewById(R.id.cross);
        destinationTextView = (AutoCompleteTextView) findViewById(R.id.et_destination);
        enterTextView = (TextView) findViewById(R.id.tv_enter);
        title = (TextView) findViewById(R.id.title);

        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        title.setDrawingCacheEnabled(true);
    }

    //set listener on views
    public void setListener() {
        cross.setOnClickListener(this);
        enterTextView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        title.startAnimation(animationFadeIn);
    }

    //set functionality on click of views
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross:
                SelectDestinationActivity.this.finish();
                overridePendingTransition(R.anim.toright_in, R.anim.toright_out);
                break;
            case R.id.tv_enter:
                //Hide keyboard
                Utils.hideKeyboard(mContext,getCurrentFocus());
                if(destinationTextView.getText().toString().length()<=0){
                    Utils.showToast(mContext,getString(R.string.enter_your_destination));
                }else{
                    dialog.show();
                    //get latitude and longitude from selected location
                    final String location = destinationTextView.getText().toString();
                    new AsyncTask<String, String, String>() {
                        @Override
                        protected String doInBackground(String... params) {
                            if(Geocoder.isPresent()){
                                try {
                                    Geocoder gc = new Geocoder(mContext);
                                    List<Address> addresses= gc.getFromLocationName(location, 5); // get the found Address Objects
                                    for(Address a : addresses){
                                        if(a.hasLatitude() && a.hasLongitude()){
                                            lat = a.getLatitude();
                                            lng=a.getLongitude();
                                        }
                                    }
                                } catch (IOException e) {
                                    // handle the exception
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            dialog.dismiss();
                            if(lat==0||lng==0){
                                Utils.showToast(mContext,getString(R.string.can_not_find_coordinates));
                            }else{
                                Utils.show_log("Location : "+lat+","+lng);

                                //save basic values inti local DB and enter into app
                                tinyDB.putDouble(GeneralValues.USER_LAT_KEY,lat);
                                tinyDB.putDouble(GeneralValues.USER_LONG_KEY,lng);
                                tinyDB.putString(GeneralValues.USER_ID_KEY, userID);
                                tinyDB.putString(GeneralValues.USER_NAME_KEY,UNAME);
                                tinyDB.putString(GeneralValues.USER_EMAIL_KEY,EMAIL);
                                tinyDB.putString(GeneralValues.USER_DESTINATION_KEY, destinationTextView.getText().toString());
                                tinyDB.putString(GeneralValues.RESTAURANT_FILTER,"");
                                tinyDB.putString(GeneralValues.PLACES_FILTER,"");
                                i=new Intent(mContext,HomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                SelectDestinationActivity.this.finish();
                                overridePendingTransition(R.anim.to_leftin, R.anim.to_leftout);
                            }
                        }
                    }.execute();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Utils.show_log(""+adapterView.getItemAtPosition(position));
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(GeneralValues.PLACES_API_BASE);
            sb.append("?key=" + GeneralValues.GOOGLE_SERVER_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("Error", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("IO Exp", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e("JSON EXP", "Cannot process JSON results", e);
        }
        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());
                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
