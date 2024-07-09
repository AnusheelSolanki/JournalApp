package androidsamples.java.journalapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.UUID;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntryDetailsFragment # newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntryDetailsFragment extends Fragment implements DatePickerFragment.DateSelected, TimePickerFragment.TimeSelected{

  UUID entryId;
  private JournalEntry mEntry;
  EntryDetailsViewModel mEntryDetailsViewModel;
  EditText mEditTitle;
  Button bDate,bStart,bEnd,bSave;
  boolean check=true,timer;

  @Override
  public void sendDate(String date){
    bDate.setText(date);
  }
  @Override
  public  void sendTime(String time){
    if(!timer)
      bStart.setText(time);
    else
      bEnd.setText(time);

  }
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    updateUI();
    setHasOptionsMenu(true);
    setRetainInstance(true);
    mEntryDetailsViewModel = new ViewModelProvider(this).get(EntryDetailsViewModel.class);
    if(getArguments() != null){
      check=false;
      entryId = (UUID) getArguments().getSerializable(MainActivity.KEY_ENTRY_ID);
      mEntryDetailsViewModel.loadEntry(entryId);
    }
  }


  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_entry_details, container, false);
    mEditTitle = view.findViewById(R.id.edit_title);
    bDate = view.findViewById(R.id.btn_entry_date);
    bStart=view.findViewById(R.id.btn_start_time);
    bEnd=view.findViewById(R.id.btn_end_time);
    bSave=view.findViewById(R.id.btn_save);
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if(!check) {
      mEntryDetailsViewModel.getEntryLiveData().observe(getActivity(),
              entry -> {
                this.mEntry = entry;
                updateUI();
              });
    }

    bDate.setOnClickListener(v -> {
      DialogFragment newFragment = new DatePickerFragment();
      newFragment.setTargetFragment(EntryDetailsFragment.this, 1);
      newFragment.show(getFragmentManager(), "datePicker");
    });

    bStart.setOnClickListener(v -> {
      timer = false;
      DialogFragment t1 = new TimePickerFragment();
      t1.setTargetFragment(EntryDetailsFragment.this, 1);
      t1.show(getFragmentManager(), "timePicker");
    });
    bEnd.setOnClickListener(v -> {
      timer = true;
      DialogFragment t2 = new TimePickerFragment();
      t2.setTargetFragment(EntryDetailsFragment.this, 1);
      t2.show(getFragmentManager(), "timePicker");
    });
    bSave.setOnClickListener(v -> {
      String sTime = bStart.getText().toString();
      String eTime = bEnd.getText().toString();
      String date = bDate.getText().toString();
      String title = mEditTitle.getText().toString();

      if (title.equals("") || date.equals("DATE") || sTime.equals("Start Time") || eTime.equals("End Time"))
        Toast.makeText(getActivity(), "All Details must be filled for saving entry.", Toast.LENGTH_SHORT).show();
      else {
        int shr,sm,ehr,em;
        shr = Integer.parseInt(sTime.substring(0, sTime.indexOf(":")));
        sm = Integer.parseInt(sTime.substring(sTime.indexOf(":")+1));

        ehr = Integer.parseInt(eTime.substring(0, eTime.indexOf(":")));
        em = Integer.parseInt(eTime.substring(eTime.indexOf(":")+1));
        if (shr > ehr || ((shr == ehr) && (sm > em)))
          Toast.makeText(getActivity(), "End Time must be greater than Start Time", Toast.LENGTH_SHORT).show();
        else {
          if (check) {
            mEntry = new JournalEntry(mEditTitle.getText().toString(), bDate.getText().toString(),
                    bStart.getText().toString(), bEnd.getText().toString());
            mEntryDetailsViewModel.insertEntry(mEntry);
          } else {
            mEntry.setTitle(mEditTitle.getText().toString());
            mEntry.setDate(bDate.getText().toString());
            mEntry.setStartTime(bStart.getText().toString());
            mEntry.setEndTime(bEnd.getText().toString());
            mEntryDetailsViewModel.saveEntry(mEntry);
          }
          getActivity().onBackPressed();
        }
      }
    });
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    inflater.inflate(R.menu.d_menu, menu);
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.share:  {
        if(check){
          Toast.makeText(getActivity(), "Please save the entry first", Toast.LENGTH_SHORT).show();
        }
        else {
          Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
          sharingIntent.setType("text/plain");
          String shareBody = "Look what I have been up to: " + mEntry.getTitle() + " on " + mEntry.getDate() + " from "+mEntry.getStartTime() + " to " + mEntry.getEndTime();
          sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
          startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        return true;
      }
      case R.id.delete: {
        if(check){
          Toast.makeText(getActivity(), "Please save the entry first", Toast.LENGTH_SHORT).show();
        }
        else {
          new AlertDialog.Builder(getContext())
                  .setTitle("Delete entry")
                  .setMessage("Are you sure you want to delete this entry?")
                  .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    mEntryDetailsViewModel.deleteEntry(mEntry);
                    Toast.makeText(getActivity(), "Entry Deleted successfully", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                  })
                  .setNegativeButton(android.R.string.no, null)
                  .setIcon(R.drawable.ic_delete)
                  .show();
        }
        return true;
      }
      default:
        return super.onOptionsItemSelected(item);
    }
  }


  private void updateUI() {
    try {
      mEditTitle.setText(mEntry.getTitle());
      bDate.setText(mEntry.getDate());
      bStart.setText(mEntry.getStartTime());
      bEnd.setText(mEntry.getEndTime());
    }
    catch (NullPointerException ignored) {}
  }

}