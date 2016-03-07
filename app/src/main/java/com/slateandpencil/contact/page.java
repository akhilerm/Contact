package com.slateandpencil.contact;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ListIterator;

public class page extends AppCompatActivity {

    private String name;
    private String category;
    private String mob;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra("name"));
        name=getIntent().getStringExtra("name");
        mob=getIntent().getStringExtra("mob");
        category=getIntent().getStringExtra("cat");
        SQLiteDatabase sb=openOrCreateDatabase("contact", settings.MODE_PRIVATE, null);
        Cursor cursor=sb.rawQuery("select email from details where name='" + name + "' and mob='" + mob + "';", null);
        while(cursor.moveToNext()){
            email=cursor.getString(0);
        }
        sb.close();
        /*TextView textView1=(TextView)findViewById(R.id.category);
        TextView textView2=(TextView)findViewById(R.id.mob);
        TextView textView3=(TextView)findViewById(R.id.email);
        textView1.setText(category);
        textView2.setText(mob);
        textView3.setText(email);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }


}
