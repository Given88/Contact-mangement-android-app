package com.gtworld.vac1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.view.View;

import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Intent addIntent ;
    // private ContactsCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addIntent = new Intent(this, AddPage.class);

        //link the toolbar as an action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
listView = findViewById(R.id.lstView);
getData();//list available data in the database
 // ListView listView = findViewById(R.id.lstView);
 // adapter =  new ContactsCursorAdapter(this, getData(), 0);
 // listView.setAdapter(adapter);
        //Update the layout if an activity is created
        //use a toast
      //  Snackbar.make(findViewById(R.id.main), "OnCreate", Snackbar.LENGTH_LONG).show();

        listView.setAdapter(getData());


        // Set an OnItemClickListener for row selection
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


               //Get the selected item
                String selectedItem = (String) parent.getItemAtPosition(position);
                //parent.getItemAtPosition(position) - gets the data from the source using the index num(selected)
                int white_space_index = selectedItem.indexOf(" ");
                String id_value = selectedItem.substring(0,white_space_index);
                String name = selectedItem.substring(white_space_index+1);

                //select phone in contacts where name=name_value and id=id_value
                //Cursor cursor = null;
                Cursor cursor = getContentResolver().query(Uri.parse(MyContentProvider.CONTENT_URI+"/"+id_value),new String[]{"phone"}, "name = ?", new String[]{name},null );
              //now retrieve the value
                String phone = "Not found";
                if(cursor !=null) {
                    if (cursor.moveToFirst()) {
                        phone = cursor.getString(cursor.getColumnIndex("phone"));
                    }
                }
           addIntent.putExtra("id", id_value);
           addIntent.putExtra("name", name);
           addIntent.putExtra("phone", phone);
           //Send the clicked data to the editing page
                startActivity(addIntent);


                /*
                String se_Id = selectedItem.substring(0,2);
                Cursor cursor = getContentResolver().query(Uri.parse("content://com.demo.user.provider/contacts/"+se_Id), null, null, null, null);
                @SuppressLint("Range") String row = cursor.getString(cursor.getColumnIndex("name"))+" "+cursor.getString(cursor.getColumnIndex("phone"));
                // Display a Toast with the selected item

                 */
//                Toast.makeText(MainActivity.this, "phone:"+phone , Toast.LENGTH_SHORT).show();


            }
        });


    }


    //link the action bar with the menu items created from out menu xml file
    //we can also create it programmically, but that is bad prommaning
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //when the acticity starts, this method is called

        //inflate the menu, this adds items to the toolbar if present
        getMenuInflater().inflate(R.menu.home_toolbox_menu, menu);
        return true;
    }

    //now we dine actions when menu items are created
    @Override
    public boolean onOptionsItemSelected(MenuItem item){


        if(item.getItemId() == R.id.add_new){
            //navigate to another page to create a new contact
           //addIntent = new Intent(this, AddPage.class);
            startActivity(addIntent);
            return true;//action perfomerd successfully
        }
        return super.onOptionsItemSelected(item);//

    }


    @Override
    public void onResume(){
        super.onResume();
       listView.setAdapter(getData());
    }
@SuppressLint("Range")
public ArrayAdapter<String> getData(){
    ArrayList<String> my_data = new ArrayList<String>();

        //query all the data from the database
        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI, null, null, null,"id");
        String data = "";
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                data =cursor.getString(cursor.getColumnIndex("id"))+" "+cursor.getString(cursor.getColumnIndex("name"));
                cursor.moveToNext();
                my_data.add(data);
            }
           // resTextView.setText(strBuilder.toString());
        }
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, my_data);
return  adapter;
}



}