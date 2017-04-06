package ist.meic.cmu.locmess_client.message;
import java.util.ArrayList;
import java.util.List;

import ist.meic.cmu.locmess_client.*;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MessageActivity extends BaseNavigationActivity {
    //TODO: create object message
    EditText mTitle;
    EditText mMessageContent;


    private static final String TITLE_TAG = "TITLE";
    private String selectedLocation = null;
    private List<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.list=new ArrayList<String>();
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_new_message);
        getLayoutInflater().inflate(R.layout.activity_new_message, frameLayout);
        //Dynamically generate a spinner data
        if(savedInstanceState==null) list= generateDummyData();
        createSpinnerDropDown(this.list);

        mTitle = (EditText) findViewById(R.id.messageTitle);
        Intent intent = getIntent();
        String username = intent.getStringExtra(TITLE_TAG);
        if (username != null) {
            mTitle.setText(username);
            Toast.makeText(this, "Title " + mTitle.toString(), Toast.LENGTH_LONG).show();
        }


    }

 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId()==R.id.mypostcoisa e coisas)
            //tost sucesso
            //finish()

            return super.onOptionsItemSelected(item);

    }*/
    //Add animals into spinner dynamically
    private void createSpinnerDropDown(List<String> list) {

        //get reference to the spinner from the XML layout
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setPrompt("Chose a Location");
        //create an ArrayAdaptar from the String Array
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        //set the view for the Drop down list
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the ArrayAdapter to the spinner
        spinner.setAdapter(dataAdapter);
        //attach the listener to the spinner
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
    }

    private List<String> generateDummyData(){
        List<String> list= new ArrayList<String>();
        for(int i=0;i <10;i++){
            list.add(String.format("location %d",i));

        }
        return list;
    }

    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            String selectedItem = parent.getItemAtPosition(pos).toString();

            //check which spinner triggered the listener
            if (parent.getId()==(R.id.spinner)) {
                    if(selectedLocation != null){
                        Toast.makeText(parent.getContext(), "Location selected is " + selectedItem,
                                Toast.LENGTH_LONG).show();
                    }
                    selectedLocation = selectedItem;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }
}