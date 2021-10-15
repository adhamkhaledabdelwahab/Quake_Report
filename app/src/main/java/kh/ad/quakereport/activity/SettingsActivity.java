package kh.ad.quakereport.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

import kh.ad.quakereport.R;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return onActivityLeave();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!onActivityLeave())
            super.onBackPressed();
    }

    private boolean onActivityLeave() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String startTimeKey = preferences.getString(getString(R.string.settings_start_time_key), "");
        String endTimeKey = preferences.getString(getString(R.string.settings_end_time_key), "");
        if ((!startTimeKey.equals("N/A") && !endTimeKey.equals("N/A")) ||
                (startTimeKey.equals("N/A") && endTimeKey.equals("N/A"))) {
            return false;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private DatePickerDialog pickerDialog;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagnitude);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference startTime = findPreference(getString(R.string.settings_start_time_key));
            getDate(startTime);

            startTime.setOnPreferenceClickListener(preference -> {
                openDatePicker(preference);
                return true;
            });

            Preference endTime = findPreference(getString(R.string.settings_end_time_key));
            getDate(endTime);

            endTime.setOnPreferenceClickListener(preference -> {
                openDatePicker(preference);
                return true;
            });
        }

        private void getDate(Preference preference) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String preferenceString = preferences.getString(preference.getKey(), "");
            if (preferenceString.isEmpty())
                preference.setSummary(getString(R.string.settings_start_time_default));
            else
                preference.setSummary(preferenceString);
        }

        private void setDate(Preference preference, String newValue) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            preference.getEditor().putString(preference.getKey(), newValue).apply();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void initDatePicker(Preference p) {
            DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
                month = month + 1;
                String date = makeDateString(dayOfMonth, month, year);
                p.setSummary(date);
                setDate(p, date);
            };

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            pickerDialog = new DatePickerDialog(getActivity(), THEME_HOLO_LIGHT, listener, year, month, day);
        }

        private String makeDateString(int day, int month, int year) {
            return getMonthFormat(month) + " " + day + " " + year;
        }

        private String getMonthFormat(int month) {
            switch (month) {
                case 2:
                    return "Feb";
                case 3:
                    return "Mar";
                case 4:
                    return "Apr";
                case 5:
                    return "May";
                case 6:
                    return "Jun";
                case 7:
                    return "Jul";
                case 8:
                    return "Aug";
                case 9:
                    return "Sep";
                case 10:
                    return "Oct";
                case 11:
                    return "Nov";
                case 12:
                    return "Dec";
                case 1:
                default:
                    return "Jan";
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void openDatePicker(Preference p) {
            initDatePicker(p);
            pickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            pickerDialog.show();
            pickerDialog.setOnCancelListener(dialog -> {
                p.setSummary(getString(R.string.settings_start_time_default));
                setDate(p, getString(R.string.settings_start_time_default));
            });
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}