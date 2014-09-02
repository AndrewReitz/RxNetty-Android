package com.andrewreitz.rxnetty;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity implements ActionBar.OnNavigationListener {
  @Override public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.activity_main);

    addFragment(GetFragment.class);

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
        addFragment(GetFragment.class);
        return true;
      case 1:
        addFragment(ServerFragment.class);
        return true;
    }
    return false;
  }

  private void addFragment(Class<? extends Fragment> fragmentClass) {
    String fragName = fragmentClass.getName();
    FragmentManager fragmentManager = getFragmentManager();
    Fragment fragment = fragmentManager.findFragmentByTag(fragName);
    fragmentManager.beginTransaction()
        .replace(R.id.main_fragment_content, fragment == null
                ? Fragment.instantiate(this, fragName) : fragment,
            fragName)
        .commit();
  }
}
