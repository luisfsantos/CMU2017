package ist.meic.cmu.locmess_client.messages.create;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.request_builders.create.NewMessageRequestBuilder;
import ist.meic.cmu.locmess_client.network.sync.SyncUtils;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;
import ist.meic.cmu.locmess_client.utils.DateUtils;

public class NewMessageActivity extends AppCompatActivity {

    private static final String TAG = "NewMessageActivity";
    public static final String INTENT_LOCATION_ID = "location";
    public static final boolean BLACKLIST_CHECKED = true;

    EditText mTitle;
    EditText mMessageContent;
    TextView mFromDate;
    TextView mFromTime;
    TextView mToDate;
    TextView mToTime;
    Spinner mLocation;

    private Calendar chosenFromDate = Calendar.getInstance();
    private Calendar chosenToDate = Calendar.getInstance();

    private ViewGroup mFiltersGroup;

    //HACK
    private List<FilterInfo> mFilterInfo = new ArrayList<>();
    private Cursor mLocationCursor;
    private Cursor mKeysCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        // create account if necessary
        SyncUtils.CreateSyncAccount(this);

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
        outState.putInt("selected_location", mLocation.getSelectedItemPosition());
        outState.putSerializable("from", chosenFromDate);
        outState.putSerializable("to", chosenToDate);

        //HACK
        saveFiltersState();
        outState.putParcelableArrayList("filters", (ArrayList<FilterInfo>)mFilterInfo);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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

        mLocationCursor = populateLocationSpinner();
        mKeysCursor = initFiltersCursor();
        if (savedInstanceState == null) {
            initDateTimeSpinners();
            initLocationSpinner();
        } else {
            int index = savedInstanceState.getInt("selected_location");
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
        String date = DateUtils.formatDate(calendar);

        mFromDate.setText(date);
        mToDate.setText(date);

        // time
        String nowTime = DateUtils.formatTime(calendar);
        calendar.add(Calendar.MINUTE, 1);
        chosenToDate.setTime(calendar.getTime());
        String nowTimePlusOneHour = DateUtils.formatTime(calendar);

        mFromTime.setText(nowTime);
        mToTime.setText(nowTimePlusOneHour);
    }

    private void initLocationSpinner() {
        if (mLocationCursor == null || mLocationCursor.getCount() < 1) {
            return;
        }
        int location_id = getIntent().getIntExtra(INTENT_LOCATION_ID, -1);
        if (location_id > 0) {
            Log.d(TAG, "location_id: " + location_id);
            int index = 0;
            for (int i = 0; i < mLocationCursor.getCount(); i++) {
                mLocationCursor.moveToPosition(i);
                if (mLocationCursor.getInt(mLocationCursor.getColumnIndexOrThrow(LocMessDBContract.Location._ID)) == location_id) {
                    index = i;
                    break;
                }
            }
            mLocation.setSelection(index);
        }
    }

    private Cursor populateLocationSpinner() {
        Log.d(TAG, "Querying database for locations");
        String[] projection = {
                LocMessDBContract.Location._ID,
                LocMessDBContract.Location.COLUMN_NAME,
                LocMessDBContract.COLUMN_SERVER_ID
        };
        Cursor cursor = getContentResolver().query(
                LocMessDBContract.Location.CONTENT_URI,
                projection,
                null,           // the selection clause
                null,           // the selection args
                LocMessDBContract.Location.COLUMN_NAME + " ASC" // the sort order
        );

        if (cursor == null || cursor.getCount() < 1) {
            String[] obj = new String[] { getString(R.string.no_locations_spinner)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, obj);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mLocation.setAdapter(adapter);
            mLocation.setEnabled(false);
            return null;
        }

        String[] fromColumns = {LocMessDBContract.Location.COLUMN_NAME};
        int[] toViews = {android.R.id.text1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,
                cursor,
                fromColumns,
                toViews,
                0
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocation.setAdapter(adapter);
        return cursor;
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
                postMessage();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (mLocationCursor != null) {
            mLocationCursor.close();
        }
        if (mKeysCursor != null) {
            mKeysCursor.close();
        }
        super.onDestroy();
    }


    private void postMessage() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (mLocationCursor == null || mLocationCursor.getCount() < 1 ) {
                    Toast.makeText(NewMessageActivity.this, R.string.cannot_post_msg_no_locations, Toast.LENGTH_SHORT).show();
                    return;
                }
                mLocationCursor.moveToPosition(mLocation.getSelectedItemPosition());
                String location = mLocationCursor.getString(mLocationCursor.getColumnIndexOrThrow(LocMessDBContract.Location.COLUMN_NAME));
                int locationServerID = mLocationCursor.getInt(mLocationCursor.getColumnIndexOrThrow(LocMessDBContract.COLUMN_SERVER_ID));
                String title = mTitle.getText().toString().trim();
                String content = mMessageContent.getText().toString().trim();
                if (title.isEmpty()) {
                    mTitle.setError(getString(R.string.title_missing));
                    mTitle.requestFocus();
                    return;
                }
                if (content.isEmpty()) {
                    mMessageContent.setError(getString(R.string.msg_body_missing));
                    mMessageContent.requestFocus();
                    return;
                }

                saveFiltersState();
                Map<String, String> blacklist = new HashMap<>();
                Map<String, String> whitelist = new HashMap<>();
                for (int i = 0; i < mFilterInfo.size(); i++){
                    FilterInfo info = mFilterInfo.get(i);
                    String value = info.editTextInput.trim();
                    if (value.isEmpty()) continue;
                    mKeysCursor.moveToPosition(info.spinnerSelectedItem);
                    String key = mKeysCursor.getString(mKeysCursor.getColumnIndexOrThrow(LocMessDBContract.Keys.COLUMN_NAME));
                    if (info.switchChecked == BLACKLIST_CHECKED) {
                        blacklist.put(key, value);
                    } else {
                        whitelist.put(key, value);
                    }
                }

                ContentValues values = new ContentValues();
                values.put(LocMessDBContract.PostedMessages.COLUMN_TITLE, title);
                values.put(LocMessDBContract.PostedMessages.COLUMN_CONTENT, content);
                values.put(LocMessDBContract.PostedMessages.COLUMN_DATE_FROM, DateUtils.formatDateTimeLocaleToDb(chosenFromDate));
                values.put(LocMessDBContract.PostedMessages.COLUMN_DATE_TO, DateUtils.formatDateTimeLocaleToDb(chosenToDate));
                values.put(LocMessDBContract.PostedMessages.COLUMN_LOCATION, location);
                //FIXME possibly store blacklist & whitelist as well (because of p2p delivery)
                Uri uri = getContentResolver().insert(LocMessDBContract.PostedMessages.CONTENT_URI, values);
                Log.d(TAG, "New posted message uri is " + uri);
                try {
                    RequestData request = new NewMessageRequestBuilder(title, content, chosenFromDate.getTime(), chosenToDate.getTime(),
                            locationServerID, whitelist, blacklist).build(LocMessURL.NEW_MESSAGE, RequestData.POST);
                    SyncUtils.push(SyncUtils.CREATE_MESSAGE, request, uri);
                } catch (MalformedURLException e) {
                    Log.wtf(TAG, "Malformed URL: ", e);
                }
                finish();
            }
        });
    }

    private ViewGroup inflateNewFilter() {
        ViewGroup viewGroup = (ViewGroup)getLayoutInflater().inflate(R.layout.item_filter, mFiltersGroup, false);
        mFiltersGroup.addView(viewGroup);
        Spinner spinner = (Spinner)viewGroup.getChildAt(1);
        String[] fromColumns = {LocMessDBContract.Keys.COLUMN_NAME};
        int[] toViews = {android.R.id.text1};
        SimpleCursorAdapter keysAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,
                mKeysCursor,
                fromColumns,
                toViews,
                0
        );
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

    private Cursor initFiltersCursor() {
        Log.d(TAG, "Querying database for keys");
        String[] projection = {
                LocMessDBContract.Keys._ID,
                LocMessDBContract.Keys.COLUMN_NAME
        };
        Cursor cursor = getContentResolver().query(
                LocMessDBContract.Keys.CONTENT_URI,
                projection,
                null,           // the selection clause
                null,           // the selection args
                LocMessDBContract.Keys.COLUMN_NAME + " ASC" // the sort order
        );
        if (cursor == null || cursor.getCount() < 1) {
            return null;
        }
        return cursor;
    }

    public void onAddNewFilterClicked(View view) {
        if (mKeysCursor == null || mKeysCursor.getCount() < 1) {
            Toast.makeText(this, R.string.no_keys_filter, Toast.LENGTH_SHORT).show();
            return;
        }
        inflateNewFilter();
    }

    public void onRemoveFilterClicked(View view) {
        View filter = (View)view.getParent();
        mFiltersGroup.removeView(filter);
    }

    private void updateFromView() {
        mFromDate.setText(DateUtils.formatDate(chosenFromDate));
        mFromTime.setText(DateUtils.formatTime(chosenFromDate));
    }

    private void updateToView() {
        mToDate.setText(DateUtils.formatDate(chosenToDate));
        mToTime.setText(DateUtils.formatTime(chosenToDate));
    }

    private class OnDateClickListener implements View.OnClickListener {

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
            Log.d(TAG, String.format("From: %s %s", DateUtils.formatDate(chosenFromDate), DateUtils.formatTime(chosenFromDate)));
            Log.d(TAG, String.format("To: %s %s", DateUtils.formatDate(chosenToDate), DateUtils.formatTime(chosenToDate)));
        }
    }

    private class OnTimeClickListener implements View.OnClickListener {

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
            Log.d(TAG, String.format("From: %s %s", DateUtils.formatDate(chosenFromDate), DateUtils.formatTime(chosenFromDate)));
            Log.d(TAG, String.format("To: %s %s", DateUtils.formatDate(chosenToDate), DateUtils.formatTime(chosenToDate)));
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