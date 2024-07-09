package androidsamples.java.journalapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements EntryListFragment.Callbacks{
  static final String KEY_ENTRY_ID = "KEY_ENTRY_ID";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    if (currentFragment == null) {
      Fragment fragment = new EntryListFragment();
      getSupportFragmentManager()
              .beginTransaction()
              .add(R.id.nav_host_fragment, fragment)
              .commit();
    }
  }

  @Override
  public void onEntrySelected(UUID entryId) {
    Bundle args = new Bundle();
    args.putSerializable(KEY_ENTRY_ID, entryId);
    Fragment fragment = new EntryDetailsFragment();
    fragment.setArguments(args);
    getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit();
  }
}