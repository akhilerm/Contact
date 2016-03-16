package com.slateandpencil.contact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ListIterator;

public class page extends AppCompatActivity {

    private String name;
    private String category;
    private String mob;
    private String email;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Page_Data> myDataset;
    private String call_mob;
    private String send_mail;
    final int MY_PERMISSIONS_REQUEST_CALL_PHONE=20;
    class Page_Data{
        int icon;
        String text;
        Page_Data(int icon,String text){
            this.icon=icon;
            this.text=text;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.fab_contact);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

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
        myDataset=new ArrayList<Page_Data>();
        myDataset.add(new Page_Data(1,category));
        myDataset.add(new Page_Data(2,mob));
        myDataset.add(new Page_Data(3,mob));
        myDataset.add(new Page_Data(4,email));
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL,email)
                        .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, mob)
                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                        .putExtra(ContactsContract.Intents.Insert.NAME,name);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Page_Data> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txtHeader;
            public ImageView imageView;

            public ViewHolder(View v) {
                super(v);
                txtHeader = (TextView) v.findViewById(R.id.data);
                imageView=(ImageView)v.findViewById(R.id.icon);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<Page_Data> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_layout, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.txtHeader.setText(mDataset.get(position).text);
            switch (mDataset.get(position).icon){
                case 1:
                    //holder.imageView.setImageResource(R.drawable.ic_map_white_24dp);
                    break;
                case 2:
                    holder.imageView.setImageResource(R.drawable.ic_message_white_24dp);
                    break;
                case 3:
                    holder.imageView.setImageResource(R.drawable.ic_phone_white_24dp);
                    break;
                case 4:
                    holder.imageView.setImageResource(R.drawable.ic_email_white_24dp);
                    break;
            }
            holder.txtHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (mDataset.get(position).icon){
                        case 2:
                            Intent sendmessage = new Intent(Intent.ACTION_VIEW);
                            sendmessage.setData(Uri.parse("sms:"));
                            sendmessage.putExtra("address", mob);
                            startActivity(sendmessage);
                            break;
                        case 3:
                            call_mob=mDataset.get(position).text;
                            if (ContextCompat.checkSelfPermission(page.this,
                                    Manifest.permission.CALL_PHONE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(page.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            }
                            else {
                                Intent in = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+call_mob));
                                try {
                                    startActivity(in);
                                } catch (Exception e) {
                                    Log.e("exc",e.getMessage().toString());
                                    Toast.makeText(getApplicationContext(), "Insufficient Permissions", Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                        case 4:
                            send_mail=mDataset.get(position).text;
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            sendIntent.setType("plain/text");
                            sendIntent.setData(Uri.parse(send_mail));
                            sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { send_mail });
                            try{startActivity(sendIntent);}
                            catch (Exception e){
                                Toast.makeText(page.this,"No Mail client found", Toast.LENGTH_SHORT).show();}

                            break;
                    }

                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    }
    //Handle the requestPermissions call
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted
                    Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+call_mob));
                    try{startActivity(in);}
                    catch (Exception e){
                        Toast.makeText(page.this, "Insufficient Permissions", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Permission Denied
                    Toast.makeText(page.this, "App does not have enough permissions", Toast.LENGTH_SHORT).show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}


