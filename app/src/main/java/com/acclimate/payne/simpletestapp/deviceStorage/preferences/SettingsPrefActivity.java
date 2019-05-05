package com.acclimate.payne.simpletestapp.deviceStorage.preferences;

import android.os.Bundle;

/*
Used to 'extends PreferenceActivity' but it wouldn't have a Toolbar...
 */
public class SettingsPrefActivity extends AppCompatPreferenceActivity {

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsPrefFragment.class.getName().equals(fragmentName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Préférences");

        SettingsPrefFragment fragment = new SettingsPrefFragment();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }


    /*

    Apparently that is the way to ensure the ToolBar is always present.
    see: https://stackoverflow.com/a/27455363/9768291

@SuppressWarnings("deprecation")
@Override
public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    super.onPreferenceTreeClick(preferenceScreen, preference);

    // If the user has clicked on a preference screen, set up the screen
    if (preference instanceof PreferenceScreen) {
        setUpNestedScreen((PreferenceScreen) preference);
    }

    return false;
}

public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
    final Dialog dialog = preferenceScreen.getDialog();

    Toolbar bar;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
        bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
    } else {
        ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
        ListView content = (ListView) root.getChildAt(0);

        root.removeAllViews();

        bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);

        int height;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }else{
            height = bar.getHeight();
        }

        content.setPadding(0, height, 0, 0);

        root.addView(content);
        root.addView(bar);
    }

    bar.setTitle(preferenceScreen.getTitle());

    bar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    });
}
     */

}
