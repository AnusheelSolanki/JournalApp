package androidsamples.java.journalapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


  private static final String TAG= "TimePickerFragment";

  public interface TimeSelected{
    void sendTime(String input);
  }
  public TimeSelected mTimeSelected;

  @NonNull
  public static TimePickerFragment newInstance(Date time) {
    return null;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    final Calendar calendar = Calendar.getInstance();
    int hr = calendar.get(Calendar.HOUR_OF_DAY);
    int min = calendar.get(Calendar.MINUTE);
    return new TimePickerDialog(getActivity(), this, hr,min,false);
  }


  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    populateSetTime(hourOfDay,minute);
  }

  public void populateSetTime(int hr, int min){
    mTimeSelected.sendTime((hr)+":"+(min));
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    try{
      mTimeSelected = (TimeSelected) getTargetFragment();
    }catch(ClassCastException ignored){

    }
  }
}