package fr.smaiz.photouploader;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by sidou on 01/07/17.
 */

public class PrefsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.frag_container);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, new PrefsFrag()).commit();
    }
}
