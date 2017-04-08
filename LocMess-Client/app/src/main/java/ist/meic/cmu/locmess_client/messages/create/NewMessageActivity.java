package ist.meic.cmu.locmess_client.messages.create;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.KeyPair;

public class NewMessageActivity extends AppCompatActivity {

    private static final String TAG = "NewMessageActivity";
    public static final String INTENT_LOCATION = "location";
    public static final boolean BACKLIST_CHECKED = true;

    EditText mTitle;
    EditText mMessageContent;
    TextView mFromDate;
    TextView mFromTime;
    TextView mToDate;
    TextView mToTime;
    Spinner mLocation;

    private Calendar chosenFromDate = Calendar.getInstance();
    private Calendar chosenToDate = Calendar.getInstance();

    private List<String> mLocationsList = new ArrayList<>();
    private ViewGroup mFiltersGroup;
    List<String> mKeysList;

    //HACK
    private List<FilterInfo> mFilterInfo = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        //FIXME: this is dummy data
        mKeysList = new ArrayList<>();
        mKeysList.add("Favourite food");
        mKeysList.add("Sport");
        mKeysList.add("Job");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable close = AppCompatResources.getDrawable(this, R.drawable.ic_close);
        close.setColorFilter(ContextCompat.getColor(this, R.color.light_text), PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(close);

        mTitle = (EditText) findViewById(R.id.message_title);
        mMessageContent = (EditText) findViewById(R.id.message_content);
        mFromDate = (TextView)findViewById(R.id.spinner_from_date);
        mToDate = (TextView)findViewById(R.id.spinner_to_date);
        mFromTime = (TextView)findViewById(R.id.spinner_from_time);
        mToTime = (TextView)findViewById(R.id.spinner_to_time);
        mLocation = (Spinner)findViewById(R.id.spinner_location);
        mFiltersGroup = (LinearLayout) findViewById(R.id.filters_group);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("locations", (ArrayList<String>)mLocationsList);
        outState.putString("selected_location", mLocation.getSelectedItem().toString());
        outState.putSerializable("from", chosenFromDate);
        outState.putSerializable("to", chosenToDate);

        //HACK
        saveFiltersState();
        outState.putParcelableArrayList("filters", (ArrayList<FilterInfo>)mFilterInfo);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLocationsList = savedInstanceState.getStringArrayList("locations");
        chosenFromDate = (Calendar)savedInstanceState.getSerializable("from");
        chosenToDate = (Calendar)savedInstanceState.getSerializable("to");
        updateFromView();
        updateToView();

        //HACK
        mFilterInfo = savedInstanceState.getParcelableArrayList("filters");
        inflateSavedFilters();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mLocationsList = generateDummyData(5);
            initDateTimeSpinners();

            Intent intent = getIntent();
            String intent_location = intent.getStringExtra(INTENT_LOCATION);
            Log.d(TAG, "intent_location: "+intent_location);
            populateLocationSpinner(mLocationsList);
            if (intent_location != null) {
                int index = mLocationsList.indexOf(intent_location);
                mLocation.setSelection(index);
            }
        } else {
            populateLocationSpinner(mLocationsList);
            String location = savedInstanceState.getString("selected_location");
            int index = mLocationsList.indexOf(location);
            mLocation.setSelection(index);
        }
        OnDateClickListener dateListener = new OnDateClickListener();
        OnTimeClickListener timeListener = new OnTimeClickListener();
        mFromDate.setOnClickListener(dateListener);
        mToDate.setOnClickListener(dateListener);
        mFromTime.setOnClickListener(timeListener);
        mToTime.setOnClickListener(timeListener);
    }

    private void initDateTimeSpinners() {
        // date
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        chosenFromDate.setTime(calendar.getTime());
        String date = formatDate(calendar);

        mFromDate.setText(date);
        mToDate.setText(date);

        // time
        String nowTime = formatTime(calendar);
        calendar.add(Calendar.MINUTE, 1);
        chosenToDate.setTime(calendar.getTime());
        String nowTimePlusOneHour = formatTime(calendar);

        mFromTime.setText(nowTime);
        mToTime.setText(nowTimePlusOneHour);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_message_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.post:
                Log.i(TAG, "Posting message");
                postMessage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void postMessage() {
        String location = mLocation.getSelectedItem().toString();
        String title = mTitle.getText().toString().trim();
        String content = mMessageContent.getText().toString().trim();
        if (title.isEmpty()) {
            mTitle.setError(getString(R.string.title_missing));
            return;
        }
        if (content.isEmpty()) {
            mMessageContent.setError(getString(R.string.msg_body_missing));
            return;
        }
        Log.i(TAG, "Location: " + location);
        Log.i(TAG, "Title: " + title);
        Log.i(TAG, "Content: " + content);
        Log.i(TAG, String.format("From: %s %s", formatDate(chosenFromDate), formatTime(chosenFromDate)));
        Log.i(TAG, String.format("To: %s %s: ", formatDate(chosenToDate), formatTime(chosenToDate)));

        saveFiltersState();
        List<KeyPair> blacklist = new ArrayList<>();
        List<KeyPair> whitelist = new ArrayList<>();
        for (int i = 0; i < mFilterInfo.size(); i++){
            FilterInfo info = mFilterInfo.get(i);
            String value = info.editTextInput.trim();
            if (value.isEmpty()) continue;
            String key = mKeysList.get(info.spinnerSelectedItem);
            if (info.switchChecked == BACKLIST_CHECKED) {
                blacklist.add(new KeyPair(key, value));
            } else {
                whitelist.add(new KeyPair(key, value));
            }
        }
        Log.d(TAG, "Blacklist: " + Arrays.toString(blacklist.toArray(new KeyPair[blacklist.size()])));
        Log.d(TAG, "Whitelist: " + Arrays.toString(whitelist.toArray(new KeyPair[whitelist.size()])));
        //TODO actually post message
        finish();
    }

    private void populateLocationSpinner(List<String> list) {
        mLocation.setPrompt(getString(R.string.choose_location_prompt));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocation.setAdapter(dataAdapter);
    }

    private ViewGroup inflateNewFilter() {
        ViewGroup viewGroup = (ViewGroup)getLayoutInflater().inflate(R.layout.item_filter, mFiltersGroup, false);
        mFiltersGroup.addView(viewGroup);
        Spinner spinner = (Spinner)viewGroup.getChildAt(1);
        ArrayAdapter<String> keysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mKeysList);
        keysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(keysAdapter);
        return viewGroup;
    }

    /* HACK
    *  Needed because Android doesn't handle restore state of views inflated
    *  programmatically unless they're given a unique (and constant) ID every time
    *  the activity is created. IDs given to views in 'item_filter.xml' layout wouldn't
    *  work since we're inflating the same layout multiple times, and thus the IDs of the
    *  layout's children will be repeated. Instead of making sure all views' IDs are
    *  unique and constant, it's easier to save and restore the filters' state by hand.
    * */
    private void saveFiltersState() {
        mFilterInfo.clear();
        for (int i = 0; i < mFiltersGroup.getChildCount(); i++) {
            FilterInfo info = new FilterInfo();
            ViewGroup filter = (ViewGroup) mFiltersGroup.getChildAt(i);
            Switch aSwitch = (Switch)filter.getChildAt(0);
            info.switchChecked = aSwitch.isChecked();
            Spinner spinner = (Spinner)filter.getChildAt(1);
            info.spinnerSelectedItem = spinner.getSelectedItemPosition();
            EditText editText = (EditText)filter.getChildAt(2);
            info.editTextInput = editText.getText().toString();
            mFilterInfo.add(info);
        }
    }
    /* HACK
    *  hack continues (see saveFiltersState)
    * */
    private void inflateSavedFilters() {
        for (int i = 0; i < mFilterInfo.size(); i++) {
            FilterInfo info = mFilterInfo.get(i);
            ViewGroup filter = inflateNewFilter();
            Switch aSwitch = (Switch)filter.getChildAt(0);
            aSwitch.setChecked(info.switchChecked);
            Spinner spinner = (Spinner)filter.getChildAt(1);
            spinner.setSelection(info.spinnerSelectedItem);
            EditText editText = (EditText)filter.getChildAt(2);
            editText.setText(info.editTextInput);
        }
    }

    public void onAddNewFilterClicked(View view) {
        inflateNewFilter();
    }

    public void onRemoveFilterClicked(View view) {
        View filter = (View)view.getParent();
        mFiltersGroup.removeView(filter);
    }

    private List<String> generateDummyData(int size){
        List<String> list= new ArrayList<>();
        for (int i=0; i < size; i++){
            list.add(String.format("location %d",i));
            if (i == (size-1)) {
                list.add("Arco do Cego");
            }
        }
        return list;
    }

    private String formatTime(Calendar calendar) {
        return SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
    }
    private String formatDate(Calendar calendar) {
        return SimpleDateFormat.getDateInstance().format(calendar.getTime());
    }

    private void updateFromView() {
        mFromDate.setText(formatDate(chosenFromDate));
        mFromTime.setText(formatTime(chosenFromDate));
    }

    private void updateToView() {
        mToDate.setText(formatDate(chosenToDate));
        mToTime.setText(formatTime(chosenToDate));
    }

    class OnDateClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View view) {
            Calendar calendar = Calendar.getInstance();
            switch (view.getId()) {
                case R.id.spinner_from_date:
                    calendar.setTime(chosenFromDate.getTime());
                    break;
                case R.id.spinner_to_date:
                    calendar.setTime(chosenToDate.getTime());
            }

            new DatePickerDialog(NewMessageActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    //i = year; i1 = month of the year; i2 = day of the month
                    updateDate(view, i2, i1, i);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            .show();
        }

        private void updateDate(View view, int day, int month, int year) {
            switch (view.getId()) {
                case R.id.spinner_from_date:
                    Log.d(TAG, "'From:Date' clicked.");
                    chosenFromDate.set(year, month, day);
                    updateFromView();

                    // if new From is after current To, then To updated
                    // same date, time of From + 1 minute
                    if (chosenFromDate.after(chosenToDate)) {
                        chosenToDate.setTime(chosenFromDate.getTime());
                        chosenToDate.add(Calendar.MINUTE, 1);
                        updateToView();

                    }
                    break;
                case R.id.spinner_to_date:
                    Log.d(TAG, "'To:Date' clicked.");
                    Calendar picked = Calendar.getInstance();
                    picked.setTime(chosenToDate.getTime());
                    picked.set(year, month, day);

                    if (picked.after(chosenFromDate)) {
                        chosenToDate.set(year, month, day);
                        updateToView();
                    } else {
                        chosenToDate.setTime(chosenFromDate.getTime());
                        updateToView();
                    }
                    break;
            }
            Log.d(TAG, String.format("From: %s %s", formatDate(chosenFromDate), formatTime(chosenFromDate)));
            Log.d(TAG, String.format("To: %s %s", formatDate(chosenToDate), formatTime(chosenToDate)));
        }
    }

    class OnTimeClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View view) {
            Calendar calendar;
            switch (view.getId()) {
                case R.id.spinner_from_time:
                    calendar = chosenFromDate;
                    break;
                case R.id.spinner_to_time:
                    calendar = chosenToDate;
                    break;
                default:
                    //this should never happen though
                    calendar = Calendar.getInstance();
            }

            new TimePickerDialog(NewMessageActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    // i = hour of day ; i1 = minute
                    updateTime(view, i, i1);
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                    android.text.format.DateFormat.is24HourFormat(NewMessageActivity.this))
            .show();
        }

        private void updateTime(View view, int hour, int minute) {
            switch (view.getId()){
                case R.id.spinner_from_time:
                    Log.d(TAG, "'From:Time' clicked.");
                    chosenFromDate.set(Calendar.HOUR_OF_DAY, hour);
                    chosenFromDate.set(Calendar.MINUTE, minute);
                    updateFromView();
                    // if new From is after current To, then To updated
                    if (chosenFromDate.after(chosenToDate)) {
                        chosenToDate.set(Calendar.HOUR_OF_DAY, hour);
                        chosenToDate.set(Calendar.MINUTE, minute);
                        chosenToDate.add(Calendar.MINUTE, 1);
                        updateToView();
                    }
                    break;
                case R.id.spinner_to_time:
                    Log.d(TAG, "'To:Time' clicked.");
                    Calendar picked = Calendar.getInstance();
                    picked.setTime(chosenToDate.getTime());
                    picked.set(Calendar.HOUR_OF_DAY, hour);
                    picked.set(Calendar.MINUTE, minute);

                    // if To (picked) is before From, one day is added to To
                    if (picked.before(chosenFromDate)) {
                        chosenToDate.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    chosenToDate.set(Calendar.HOUR_OF_DAY, hour);
                    chosenToDate.set(Calendar.MINUTE, minute);
                    updateToView();
                    break;
            }
            Log.d(TAG, String.format("From: %s %s", formatDate(chosenFromDate), formatTime(chosenFromDate)));
            Log.d(TAG, String.format("To: %s %s", formatDate(chosenToDate), formatTime(chosenToDate)));
        }
    }
}

class FilterInfo implements Parcelable {
    boolean switchChecked;
    int spinnerSelectedItem;
    String editTextInput;

    FilterInfo() {}

    protected FilterInfo(Parcel in) {
        switchChecked = in.readByte() != 0;
        spinnerSelectedItem = in.readInt();
        editTextInput = in.readString();
    }

    public static final Creator<FilterInfo> CREATOR = new Creator<FilterInfo>() {
        @Override
        public FilterInfo createFromParcel(Parcel in) {
            return new FilterInfo(in);
        }

        @Override
        public FilterInfo[] newArray(int size) {
            return new FilterInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (switchChecked ? 1 : 0));
        parcel.writeInt(spinnerSelectedItem);
        parcel.writeString(editTextInput);
    }
}