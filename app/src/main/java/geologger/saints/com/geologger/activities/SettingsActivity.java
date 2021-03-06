package geologger.saints.com.geologger.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.Toast;

import org.androidannotations.annotations.EActivity;

import geologger.saints.com.geologger.R;
import geologger.saints.com.geologger.utils.UserId;

@EActivity
public class SettingsActivity extends PreferenceActivity {

    public static final String POSITIONINGINTERVAL = "positioning_interval";
    public static final String POSITIONINGDISTANCE = "positioning_distance";
    public static final String LOGGINGINTERVAL = "logging_interval";
    public static final String POICOUNT = "poi_result_count";
    public static final String USERID = "user_id";
    public static final String SECONDURL = "second_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ParameterSettingFragment()).commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    public static class ParameterSettingFragment extends PreferenceFragment  {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            ListPreference positioningIntervalPreference = (ListPreference)findPreference(POSITIONINGINTERVAL);
            bindPreferenceSummaryToValue(positioningIntervalPreference);

            ListPreference positioningDistancePreference = (ListPreference)findPreference(POSITIONINGDISTANCE);
            bindPreferenceSummaryToValue(positioningDistancePreference);

            ListPreference loggingIntervalPreference = (ListPreference)findPreference(LOGGINGINTERVAL);
            bindPreferenceSummaryToValue(loggingIntervalPreference);

            ListPreference poiCountPreference = (ListPreference)findPreference(POICOUNT);
            bindPreferenceSummaryToValue(poiCountPreference);

            EditTextPreference secondUrlPreference = (EditTextPreference)findPreference(SECONDURL);
            bindPreferenceSummaryToValue(secondUrlPreference);

            Preference userIdPreference = findPreference(USERID);
            String userId = UserId.getUserId(getActivity().getApplicationContext());
            if (userId != null) {
                userIdPreference.setTitle(userId);
            }

            userIdPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String title = preference.getTitle().toString();
                    if (title != null) {
                        Toast.makeText(getActivity(), title, Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
        }

    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    //region Helpers


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
    }

    //endregion
}
