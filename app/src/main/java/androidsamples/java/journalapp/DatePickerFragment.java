package androidsamples.java.journalapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


  public interface DateSelected{
    void sendDate(String input);
  }
  public DateSelected mDateSelected;

  @NonNull
  public static DatePickerFragment newInstance(Date date) {

    return null;

  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);
    return new DatePickerDialog(getActivity(), this, yy, mm, dd);
  }

  public void onDateSet(DatePicker view, int yy, int mm, int dd) {
    setter(yy, mm+1, dd);
  }
  public void setter(int year, int month, int day) {
    Calendar c = Calendar.getInstance();
    c.set(year,month-1,day);
    int ans=c.get(Calendar.DAY_OF_WEEK);
    String[] weekDays= {"SUN","MON","TUE","WED","THU","FRI","SAT"};
    String[] months= {"JAN","FEB","MAR","APR","MAY","JUN","JULY","AUG","SEP","OCT","NOV","DEC"};
    mDateSelected.sendDate(weekDays[ans-1]+", "+months[month-1]+" "+day+", "+year);
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    try{
      mDateSelected = (DateSelected) getTargetFragment();
    }catch(ClassCastException ignored){}
  }
}
