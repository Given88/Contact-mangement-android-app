package com.gtworld.vac1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class AddPage extends AppCompatActivity {

    private Intent l_intent ;
    private boolean edit = false;//do we edit data?
    private EditText nameTxt ;
    private EditText phoneTxt ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);
         nameTxt = findViewById(R.id.nameTxt);
        phoneTxt = findViewById(R.id.phoneTxt);
        l_intent = getIntent();//get the intent that generated this page
        if(l_intent.hasExtra("id")){
            edit = true;
            nameTxt.setText(l_intent.getStringExtra("name"));
            phoneTxt.setText(l_intent.getStringExtra("phone"));
        }
        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//allows the user to navigate back to the previous screen in the app hierarchy
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back); // Use a back arrow icon, changes the "up" button to an icon

            getSupportActionBar().setTitle("New Contact"); // Optional

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbox_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        //resource ID for "up"/"back button" in the Android Framework of the toolbar is:
        // android.R.id.home
        //finish()- moves to the prevous activity in the stack
        int id = item.getItemId();
        if(id == android.R.id.home){
            //navigate to the home page
            finish();
            return true;
        }
        else if(id == R.id.action_add){
         //Adds into a database then navigates back to home page
           //First validate the input
            EditText nameTxt = findViewById(R.id.nameTxt);
            EditText phoneTxt = findViewById(R.id.phoneTxt);

            if (phoneTxt.getText().toString().equals("")) {
                Snackbar.make(findViewById(R.id.add_main), "Enter a valid phone number", Snackbar.LENGTH_LONG).show();

            } else {
                //add in to the database
                ContentValues values = new ContentValues();

                if (nameTxt.getText().toString().equals("")) {
                    values.put("name", phoneTxt.getText().toString());
                    values.put("phone", phoneTxt.getText().toString());


                } else {
                    values.put("name", nameTxt.getText().toString());
                    values.put("phone", phoneTxt.getText().toString());
                }
                if(edit==false) {
                    getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
                    Snackbar.make(findViewById(R.id.add_main), "Added Successfully", Snackbar.LENGTH_LONG).show();

                }else{
                    String id_value = l_intent.getStringExtra("id");///gets the id tp work on
                    getContentResolver().update(Uri.parse(MyContentProvider.CONTENT_URI+"/"+id_value), values, null,null);
                    Snackbar.make(findViewById(R.id.add_main), "Edit Successfully", Snackbar.LENGTH_LONG).show();

                }
                // finish();//return to the home page

                finish();
            }


            return true;
        }
        else if(id == R.id.action_undo){
             //clears everything the navigates to the home page
            if(edit)
                getContentResolver().delete(Uri.parse(MyContentProvider.CONTENT_URI+"/"+l_intent.getStringExtra("id")),null, null);

           finish();
            return true;
        }

       return super.onOptionsItemSelected(item);
    }
}