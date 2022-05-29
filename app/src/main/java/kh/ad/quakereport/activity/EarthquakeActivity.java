package kh.ad.quakereport.activity;

import static android.os.Build.VERSION.SDK_INT;
import static kh.ad.quakereport.tools.HelperMethods.dateFormat;
import static kh.ad.quakereport.tools.HelperMethods.placeFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import kh.ad.quakereport.R;
import kh.ad.quakereport.custom.EmptyRecyclerView;
import kh.ad.quakereport.databinding.ActivityEarthquakeBinding;
import kh.ad.quakereport.model.DataItem;
import kh.ad.quakereport.model.Earthquake;
import kh.ad.quakereport.model.JsonModel;
import kh.ad.quakereport.tools.DataModelInterface;
import kh.ad.quakereport.tools.EarthquakeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EarthquakeActivity extends AppCompatActivity {


    /**
     * URL to query the USGS dataset for earthquake information
     */
    //"https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=1";
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/";

    ActivityEarthquakeBinding binding;

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */

    EarthquakeAdapter adapter;
    EmptyRecyclerView earthquakeListView;
    TextView empty;
    ProgressBar wait;
    SwipeRefreshLayout swipe;
    FloatingActionButton contactForm;

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(USGS_REQUEST_URL)
            .addConverterFactory(GsonConverterFactory.create());

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEarthquakeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the app language to english
        Configuration config;
        config = new Configuration(getResources().getConfiguration());
        config.locale = Locale.ENGLISH;
        config.setLayoutDirection(new Locale("en"));
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        earthquakeListView = binding.list;
        empty = binding.emptyList;
        wait = binding.w8Internet;
        swipe = binding.refreshOnScroll;
        contactForm = binding.floatActionContactForm;

        wait.setVisibility(View.VISIBLE);
        empty.setVisibility(View.INVISIBLE);

        if (isNetworkConnected()) {
            String[] data = getSharedPreferencesData();
            if (SDK_INT >= 21) {
                setRetrofitCall(data[2], data[3], data[0], data[1]);
            } else {
                setVolleyCall(data[2], data[3], data[0], data[1]);
            }
        } else {
            wait.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
            empty.setText(R.string.no_internet_connection);
        }

        swipe.setOnRefreshListener(() -> {
            if (isNetworkConnected()) {
                String[] data = getSharedPreferencesData();
                if (SDK_INT >= 21) {
                    setRetrofitCall(data[2], data[3], data[0], data[1]);
                } else {
                    setVolleyCall(data[2], data[3], data[0], data[1]);
                }
            } else {
                Toast.makeText(EarthquakeActivity.this, "No Internet Connection, please reconnect internet and try again", Toast.LENGTH_LONG).show();
                swipe.setRefreshing(false);
            }
        });

        contactForm.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://answers.usgs.gov"));
            startActivity(browserIntent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_contactInfo) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.usgs.gov/about/congressional/contacts"));
            startActivity(browserIntent);
            return true;
        } else if (id == R.id.action_contactForm) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://answers.usgs.gov"));
            startActivity(browserIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isNetworkConnected()) {
            wait.setVisibility(View.VISIBLE);
            empty.setVisibility(View.INVISIBLE);
            String[] data = getSharedPreferencesData();
            if (SDK_INT >= 21) {
                setRetrofitCall(data[2], data[3], data[0], data[1]);
            } else {
                setVolleyCall(data[2], data[3], data[0], data[1]);
            }
        } else {
            if (earthquakeListView.getAdapter() != null && earthquakeListView.getAdapter().getItemCount() <= 0) {
                wait.setVisibility(View.INVISIBLE);
                empty.setVisibility(View.VISIBLE);
                empty.setText(R.string.no_internet_connection);
            } else {
                Toast.makeText(this, "No Internet Connection, please reconnect internet and try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String[] getSharedPreferencesData() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String startTime = sharedPrefs.getString(
                getString(R.string.settings_start_time_key),
                getString(R.string.settings_start_time_default)
        );

        String endTime = sharedPrefs.getString(
                getString(R.string.settings_end_time_key),
                getString(R.string.settings_end_time_default)
        );


        return new String[]{minMagnitude, orderBy, startTime, endTime};
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null)
            return info.isConnected();
        return false;
    }

    private void setVolleyCall(String startTime, String endTime, String minMagnitude, String orderBy) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String bUrl = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=";
        if (!startTime.equals("N/A") && !endTime.equals("N/A"))
            bUrl += orderBy + "&minmag=" + Float.parseFloat(minMagnitude) + "&starttime=" + getDate(startTime) + "&endtime=" + getDate(endTime);
        else
            bUrl += orderBy + "&minmag=" + Float.parseFloat(minMagnitude);
        JsonObjectRequest data = new JsonObjectRequest(Request.Method.GET, bUrl, null, response -> {
            ArrayList<Earthquake> earthquakes = new ArrayList<>();
            JSONArray json;
            try {
                json = response.getJSONArray("features");
                for (int i = 0; i < json.length(); i++) {
                    JSONObject element = json.getJSONObject(i);
                    double mag = element.getJSONObject("properties").getDouble("mag");
                    String place = element.getJSONObject("properties").getString("place");
                    long time = element.getJSONObject("properties").getLong("time");
                    String url = element.getJSONObject("properties").getString("url");
                    Earthquake item = new Earthquake((float) mag, placeFormat(place), dateFormat(time), url);
                    earthquakes.add(item);

                }
                onLoadFinished(earthquakes);
                swipe.setRefreshing(false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(EarthquakeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show());
        mRequestQueue.add(data);
    }

    private void setRetrofitCall(String startTime, String endTime, String minMagnitude, String orderBy) {
        Retrofit retrofit = builder.build();
        DataModelInterface modelInterface = retrofit.create(DataModelInterface.class);
        Call<JsonModel> call;
        if (!startTime.equals("N/A") && !endTime.equals("N/A"))
            call = modelInterface.getData("geojson",
                    orderBy, Float.parseFloat(minMagnitude), getDate(startTime), getDate(endTime));
        else
            call = modelInterface.getData("geojson",
                    orderBy, Float.parseFloat(minMagnitude));
        call.enqueue(new Callback<JsonModel>() {
            @Override
            public void onResponse(@NonNull Call<JsonModel> call,
                                   @NonNull Response<JsonModel> response) {
                ArrayList<Earthquake> earthquakes = new ArrayList<>();
                assert response.body() != null;
                DataItem[] data = response.body().getFeatures();
                for (DataItem item : data) {
                    String plc = item.getProp().getPlace();
                    if (plc == null)
                        plc = "/null";
                    else
                        plc = placeFormat(plc);
                    Earthquake i = new Earthquake(
                            item.getProp().getMag(),
                            plc,
                            dateFormat(item.getProp().getTime()),
                            item.getProp().getUrl()
                    );
                    earthquakes.add(i);
                }
                onLoadFinished(earthquakes);
                swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<JsonModel> call, @NonNull Throwable t) {
                Toast.makeText(EarthquakeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getDate(String date) {
        String[] data = date.split(" ");
        String day = data[1];
        String month = getMonth(data[0]);
        String year = data[2];
        return year + "-" + month + "-" + day;
    }

    private String getMonth(String m) {
        switch (m) {
            case "Jan":
                return "01";
            case "Feb":
                return "02";
            case "Mar":
                return "03";
            case "Apr":
                return "04";
            case "May":
                return "05";
            case "Jun":
                return "06";
            case "Jul":
                return "07";
            case "Aug":
                return "08";
            case "Sep":
                return "09";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
            default:
                return "";
        }
    }

    private void setUI(ArrayList<Earthquake> data) {
        // Create a new {@link RecyclerView adapter} of earthquakes
        adapter = new EarthquakeAdapter(data, this);

        // set linear layout orientation to vertical ar recycler view and set divider line
        LinearLayoutManager mLinearLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        earthquakeListView.setLayoutManager(mLinearLayout);
        earthquakeListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Set the adapter on the {@link RecyclerView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setEmptyView(empty);
    }

    private void onLoadFinished(ArrayList<Earthquake> data) {
        wait.setVisibility(View.GONE);
        if (adapter != null) {
            // Clear the adapter of previous earthquake data
            adapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                adapter.addAll(data);
            }
        } else {
            setUI(data);
        }
    }
}