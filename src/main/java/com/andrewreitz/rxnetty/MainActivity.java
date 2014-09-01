package com.andrewreitz.rxnetty;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity implements ActionBar.OnNavigationListener {
  @Override public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.activity_main);

    // Set up the action bar to show a dropdown list.
    final ActionBar actionBar = getActionBar();
    //noinspection ConstantConditions
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

    final String[] dropdownValues = getResources().getStringArray(R.array.drop_down_menu);

    // Specify a SpinnerAdapter to populate the dropdown list.
    ArrayAdapter<String> adapter = new ArrayAdapter<>(actionBar.getThemedContext(),
        android.R.layout.simple_spinner_item, android.R.id.text1,
        dropdownValues);

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    // Set up the dropdown list navigation in the action bar.
    actionBar.setListNavigationCallbacks(adapter, this);
  }

  @Override public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    return false;
  }
}
