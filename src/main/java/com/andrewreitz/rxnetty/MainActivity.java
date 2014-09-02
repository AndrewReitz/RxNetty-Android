package com.andrewreitz.rxnetty;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity implements ActionBar.OnNavigationListener {
  @Override public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.activity_main);

    final ActionBar actionBar = getActionBar();
    //noinspection ConstantConditions
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

    final String[] dropdownValues = getResources().getStringArray(R.array.drop_down_menu);

    ArrayAdapter<String> adapter = new ArrayAdapter<>(actionBar.getThemedContext(),
        android.R.layout.simple_spinner_item, android.R.id.text1,
        dropdownValues);

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    actionBar.setListNavigationCallbacks(adapter, this);
  }

  @Override public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    switch (itemPosition) {
      case 0:
        getFragmentManager().beginTransaction()
            .replace(R.id.main_fragment_content, new GetFragment())
            .commit();
        return true;
      case 1:
        getFragmentManager().beginTransaction()
            .replace(R.id.main_fragment_content, new ServerFragment())
            .commit();
        return true;
    }
    return false;
  }
}
