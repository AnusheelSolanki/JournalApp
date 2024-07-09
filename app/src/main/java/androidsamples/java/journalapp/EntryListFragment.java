package androidsamples.java.journalapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.UUID;

/**
 * A fragment representing a list of Items.
 */
public class EntryListFragment extends Fragment {
  private EntryListViewModel mEntryListViewModel;

  interface Callbacks {
    void onEntrySelected(UUID id);
  }
  Callbacks mCallbacks;
  private JournalEntry mEntry;


  @NonNull
  public static EntryListFragment newInstance() {
    EntryListFragment fragment = new EntryListFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mEntryListViewModel = new ViewModelProvider(this).get(EntryListViewModel.class);
    setHasOptionsMenu(true);
  }

  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
          container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_entry_list, container, false);
    view.findViewById(R.id.btn_add_entry).setOnClickListener(this::addNewEntry);
    RecyclerView entriesList = view.findViewById(R.id.recyclerView);
    entriesList.setLayoutManager(new LinearLayoutManager(getActivity()));
    JournalEntryListAdapter adapter = new JournalEntryListAdapter(getActivity());
    entriesList.setAdapter(adapter);
    mEntryListViewModel.getAllEntries()
            .observe(requireActivity(), adapter::setEntries);
    adapter.setOnItemClickListener((itemView, position) -> {
      mEntry = adapter.mEntries.get(position);
      launchJournalEntryFragment(itemView);
    });
    return view;
  }

  private void addNewEntry(View view) {
    Fragment fragment = new EntryDetailsFragment();
    getFragmentManager()
            .beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit();
  }

  private void launchJournalEntryFragment(View v) {
    mCallbacks.onEntrySelected(mEntry.getUid());
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    try{
      mCallbacks = (Callbacks) context;
    }
    catch(ClassCastException ignored){
    }
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    inflater.inflate(R.menu.l_menu, menu);
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if(item.getItemId()==R.id.info) {
      //Toast.makeText(getActivity(), "Info Button", Toast.LENGTH_SHORT).show();
      Fragment info_frag = new InfoFragment();
      getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,info_frag).addToBackStack(null).commit();
      return true;
    }
    else{
      return super.onOptionsItemSelected(item);
    }
  }
}