package androidsamples.java.journalapp;

import static android.app.PendingIntent.getActivity;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Matches;
import org.mockito.internal.matchers.Null;

import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.mockito.internal.matchers.text.ValuePrinter.print;


import static java.util.EnumSet.allOf;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.IBinder;
import android.service.autofill.FieldClassification;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;


/**
 * Instrumented tests for the {@link EntryDetailsFragment}.
 */
@RunWith(AndroidJUnit4.class)
public class EntryDetailsFragmentTest {

  @Rule
  public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
  //  public FragmentScenario<Fragment> scenario = launchFragmentInContainer(EntryDetailsFragment.class);
  @Rule
  public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);



  @BeforeClass
  public static void enableAccessibilityChecks() {

    AccessibilityChecks.enable();
    Fragment contactListFragment = new EntryListFragment();

  }

  public static View decorView;

  public static Activity activity;
  private CountingIdlingResource idlingResource;

  @Before
  public void setUp() {
    activityRule.getScenario().onActivity(activity -> {
      idlingResource = new CountingIdlingResource("ToastIdlingResource");
      this.decorView = activity.getWindow().getDecorView();
      this.activity = activity;
    });
//    decorView = onView(withId(R.id.recyclerView));
//    activityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
//      @Override
//      public void perform(MainActivity activity) {
//        decorView = activity.getWindow().getDecorView();
//      }
//    });
  }
  @Before
  public void stubAllExternalIntents() {
    // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
    // every test run. In this case all external Intents will be blocked.
    intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
  }
  public int getCurrentRecyclerItemCount(){
    System.out.println("decorView = " + decorView);
    RecyclerView recyclerView = (RecyclerView) decorView.findViewById(R.id.recyclerView);
    RecyclerView.Adapter adapter = recyclerView.getAdapter();
    return Objects.requireNonNull(adapter).getItemCount();
  }

//  protected boolean matchesSafely(RecyclerView view) {
//    RecyclerView.Adapter adapter = view.getAdapter();
//    for (int position = 0; position < adapter.getItemCount(); position++) {
//      int type = adapter.getItemViewType(position);
//      RecyclerView.ViewHolder holder = adapter.createViewHolder(view, type);
//      adapter.onBindViewHolder(holder, position);
//      if (R.id.edit_title.) {
//        return true;
//      }
//    }
//    return false;
//  }

  public class ToastMatcher extends TypeSafeMatcher<Root> {

    @Override
    public void describeTo(Description description) {
      description.appendText("is toast");
    }

    @Override
    public boolean matchesSafely(Root root) {
      int type = root.getWindowLayoutParams().get().type;
      if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
        IBinder windowToken = root.getDecorView().getWindowToken();
        IBinder appToken = root.getDecorView().getApplicationWindowToken();
        if (windowToken == appToken) {
          return true;
        }
      }
      return false;
    }
  }


  public static class RecyclerViewItemCountAssertion implements ViewAssertion {
    private final int expectedCount;

    public RecyclerViewItemCountAssertion(int expectedCount) {
      this.expectedCount = expectedCount;
    }


    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
      if (noViewFoundException != null) {
        throw noViewFoundException;
      }

      RecyclerView recyclerView = (RecyclerView) view;
      RecyclerView.Adapter adapter = recyclerView.getAdapter();
      assert adapter != null;
      assertThat(adapter.getItemCount(), is(expectedCount));
    }

    public static class ToastMatcher extends TypeSafeMatcher<Root> {

      @Override
      public void describeTo(Description description) {
        description.appendText("is toast");
      }

      @Override
      public boolean matchesSafely(Root root) {
        int type = root.getWindowLayoutParams().get().type;
        if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
          IBinder windowToken = root.getDecorView().getWindowToken();
          IBinder appToken = root.getDecorView().getApplicationWindowToken();
          return windowToken == appToken;

        }
        return false;
      }
    }
  }

  public static Matcher<View> hasItem(Matcher<View> matcher) {
    return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {

      @Override
      public void describeTo(Description description) {
        description.appendText("has item: ");
        matcher.describeTo(description);
      }

      @Override
      protected boolean matchesSafely(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        for (int position = 0; position < adapter.getItemCount(); position++) {
          int type = adapter.getItemViewType(position);
          RecyclerView.ViewHolder holder = adapter.createViewHolder(view, type);
          adapter.onBindViewHolder(holder, position);
          if (matcher.matches(holder.itemView)) {
            return true;
          }
        }
        return false;
      }
    };
  }


  @Test
  public void findsColorContrastError() {
    onView(withId(R.id.btn_add_entry)).perform(ViewActions.click());
  }

  //Create a test to add an entry title , choose date , start time and end time but start time should be after the end time. Check if you get the correct toast for it.
  @Test
  public void testAddEntryForStartTimeAfterEndTime() {
    int currentCount = getCurrentRecyclerItemCount();
    onView(withId(R.id.btn_add_entry)).perform(click());
    onView(withId(R.id.edit_title)).perform(clearText()).perform(typeText("Test: start time > end time"));
    onView(withId(R.id.btn_entry_date)).perform(click());
    onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
            .perform(PickerActions.setDate(2021, 10, 10));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_start_time)).perform(click());
    onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(11, 0));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_end_time)).perform(click());
    onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(10, 0));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_save)).perform(click());

    onView(isRoot()).perform(ViewActions.pressBack());

    assertThat(getCurrentRecyclerItemCount(), is(currentCount));

  }

  @Test
  public void verifyInfoText() {
    onView(withId(R.id.info)).perform(click());
    onView(withId(R.id.infotext)).check(matches(withText("This is a Journal Application which tracks records of the tasks done or to be completed which includes title, date, start time, and end time. You can press the plus button which adds a new record to the list. There is also a delete button to delete one of the tasks. There is also an option to share the task via any messaging app installed on the phone.")));
  }

  //Create a test to add an entry title , choose date and start time and end time and then open the top most / recently created one , click on delete and on dialog box press ok
  @Test
  public void testDelete() {
    int currentCount = getCurrentRecyclerItemCount();
    onView(withId(R.id.btn_add_entry)).perform(click());
    onView(withId(R.id.edit_title)).perform(clearText()).perform(typeText("Testing: delete entry"));
    onView(withId(R.id.btn_entry_date)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(DatePicker.class.getName())))
            .perform(PickerActions.setDate(2021, 10, 10));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_start_time)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(10, 0));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_end_time)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(11, 0));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_save)).perform(click());
    onView(anyOf(withText("Testing: delete entry"))).perform(click());
    onView(withId(R.id.delete)).perform(click());
    onView(withText("OK")).perform(click());
    assertThat(getCurrentRecyclerItemCount(), is(currentCount));
  }


  @Test
  public void testUpdate() {
    int currentCount = getCurrentRecyclerItemCount();
    onView(withId(R.id.btn_add_entry)).perform(click());
    onView(withId(R.id.edit_title)).perform(clearText()).perform(typeText("Test: add and update entry"));
    onView(withId(R.id.btn_entry_date)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(DatePicker.class.getName())))
            .perform(PickerActions.setDate(2021, 10, 10));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_start_time)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(10, 0));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_end_time)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(11, 0));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_save)).perform(click());
    onView(anyOf(withText("Test: add and update entry"))).perform(click());
    onView(withId(R.id.edit_title)).perform(clearText()).perform(typeText("Testing add and update is now updated"));
    onView(withId(R.id.btn_save)).perform(click());

    assertThat(getCurrentRecyclerItemCount(), is(currentCount+1));
    onView(anyOf(withText("Testing add and update is now updated"))).check(matches(isDisplayed()));
//    onView(withId(R.id.recyclerView)).check(Objects.requireNonNull(matches(not(hasItem(hasDescendant(withText("Testing add and update is now updated")))))));
  }

  @Test
  public void testAddEntry() {
    int currentCount = getCurrentRecyclerItemCount();
    onView(withId(R.id.btn_add_entry)).perform(click());
    onView(withId(R.id.edit_title)).perform(clearText()).perform(typeText("New Entry for testing"));
    onView(withId(R.id.btn_entry_date)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(DatePicker.class.getName())))
            .perform(PickerActions.setDate(2023, 11, 9)); // Set a date
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_start_time)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(9, 0)); // Set start time
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_end_time)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(10, 0)); // Set end time
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_save)).perform(click());
//    onView(isRoot()).perform(ViewActions.pressBack());
    assertThat(getCurrentRecyclerItemCount(), is(currentCount + 1));
  }


  //Create a test to add an entry title , choose date and start time and end time
  @Test
  public void testAddEntry2() {
    int currentCount = getCurrentRecyclerItemCount();
    onView(withId(R.id.btn_add_entry)).perform(click());
    onView(withId(R.id.edit_title)).perform(clearText()).perform(typeText("Testing"));
    onView(withId(R.id.btn_entry_date)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(DatePicker.class.getName())))
            .perform(PickerActions.setDate(2021, 10, 10));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_start_time)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(10, 0));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_end_time)).perform(click());
    onView(withClassName(org.hamcrest.Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(11, 0));
    onView(withId(android.R.id.button1)).perform(click());
    onView(withId(R.id.btn_save)).perform(click());
//    onView(isRoot()).perform(ViewActions.pressBack());
    assertThat(getCurrentRecyclerItemCount(), is(currentCount + 1));
  }
}