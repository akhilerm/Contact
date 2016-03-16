package com.slateandpencil.contact;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class settings extends AppCompatActivity {

    private static final String ns = null;
    ProgressDialog progress;
    Boolean success=true;

    public class RPMXmlParser {
        // We don't use namespaces


        public List parse(InputStream in) throws XmlPullParserException, IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                return readFeed(parser);
            } finally {
                in.close();
            }
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List contacts = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "MyRpm");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the contact tag
            if (name.equals("contact")) {

                contacts.add(readContact(parser));

            } else {
                skip(parser);
            }
        }
        return contacts;
    }



    public class Contact{
        int id;
        String name;
        String category;
        String mob;
        String email;
        Contact(int id,String name, String category, String mob,String email) {
            this.id = id;
            this.name= name;
            this.category = category;
            this.mob=mob;
            this.email=email;
        }
    }

    Contact readContact(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG,ns, "contact");
        int id=0;
        String name=null;
        String category=null;
        String mob=null;
        String email=null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String line = parser.getName();
            if (line.equals("id")) {
                id = readID(parser);
            } else if (line.equals("name")) {
                name = readName(parser);
            } else if (line.equals("category")) {
                category = readCategory(parser);
            } else if (line.equals("phone")) {
                mob = readPhone(parser);
            } else if (line.equals("email")) {
                email = readEmail(parser);
            } else {
                skip(parser);
            }
        }
        return new Contact(id, name, category,mob,email);
    }

    private int readID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        String id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return Integer.parseInt(id);
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private String readCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "category");
        String category = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "category");
        return category;
    }

    private String readPhone(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "phone");
        String mob = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "phone");
        return mob;
    }

    private String readEmail(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "email");
        String email = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "email");
        return email;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.action_settings);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String stringUrl="http://www.mac.edu.in/cs-apps/myrpm/MyRpm.php";
        Button register=(Button)findViewById(R.id.register);
        Button update = (Button) findViewById(R.id.update);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mac.edu.in/cs-apps/myrpm"));
                startActivity(browserIntent);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    progress = ProgressDialog.show(settings.this, "Updating",
                            "Downloading data", true);
                    new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            // do the thing that takes a long time
                            try{
                                loadXmlFromNetwork(stringUrl);
                                success=false;
                            }
                            catch (IOException|XmlPullParserException e){
                                success=false;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    progress.dismiss();
                                }
                            });
                        }
                    }).start();

                    //show progress dialog here
                }
                else{
                    Toast.makeText(settings.this, "Failed to update Database", Toast.LENGTH_SHORT).show();
                }
                if(success==false){
                    Toast.makeText(settings.this, "Failed to update Database", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return e.getMessage().toString();
            } catch (XmlPullParserException e) {
                return e.getMessage().toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        RPMXmlParser rpmXmlParser = new RPMXmlParser();
        List<Contact> contactList = null;
        try {
            stream = downloadUrl(urlString);
            contactList = rpmXmlParser.parse(stream);
            SQLiteDatabase sb=openOrCreateDatabase("contact", settings.MODE_PRIVATE, null);
            sb.execSQL("delete from details;");
            for(ListIterator<Contact> iter = contactList.listIterator(); iter.hasNext();){
                Contact data=iter.next();
                sb.execSQL("insert into 'details' ('id','name','category','mob','email') values ('"+data.id+"','"+data.name+"','"+data.category+"','"+data.mob+"','"+data.email+"');");
            }
            sb.close();

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        // StackOverflowXmlParser returns a List (called "entries") of Entry objects.
        // Each Entry object represents a single post in the XML feed.
        // This section processes the entries list to combine each entry with HTML markup.
        // Each entry is displayed in the UI as a link that optionally includes
        // a text summary.
        return "Updated";
    }

    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }


}
