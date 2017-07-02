package fr.smaiz.photouploader;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by sidou on 01/07/17.
 */

public class PrefsFrag extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.prefs);
    }
}
