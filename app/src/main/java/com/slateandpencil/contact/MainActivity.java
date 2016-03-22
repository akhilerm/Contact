package com.slateandpencil.contact;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /*Variables for recycler view
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;*/
    private ArrayList<String> tab_list;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private File database;
    private SQLiteDatabase sb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        database = getDatabasePath("contact");
        if (!database.exists()) {
            sb = openOrCreateDatabase("contact", MainActivity.MODE_PRIVATE, null);
            sb.execSQL("CREATE TABLE IF NOT EXISTS `details` (\n" +
                    "  `id` number(10),\n" +
                    "  `name` varchar(50),\n" +
                    "  `category` varchar(30),\n" +
                    "  `mob` varchar(20) ,\n" +
                    "  `email` varchar(100)\n" +
                    ");");
            sb.execSQL("INSERT INTO 'details' (id,'name','category','mob','email') VALUES\n" +
                    /*"(1,'Update Database','JCI Ramapuram','9495647807','tibin@zycon.in'),\n"+
                    "(2,'','','9645534925','akhilerm@gmail.com'),\n"+*/
                    "(1,'Update pls','JCI','9562000692','vishnus687@gmail.com');");
        } else {
            sb = openOrCreateDatabase("contact", MainActivity.MODE_PRIVATE, null);
        }

        tab_list=new ArrayList<String>();
        Cursor resultset = sb.rawQuery("select distinct category from details", null);
        while(resultset.moveToNext()){
            tab_list.add(resultset.getString(0));
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



    }

    @Override
    public void onRestart(){
        super.onRestart();
        recreate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for(String tab_name:tab_list) {
            Bundle bundle=new Bundle();
            bundle.putString("cat", tab_name);
            Tab tab=new Tab();
            tab.setArguments(bundle);
            adapter.addFragment(tab, tab_name);
        }
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            /*Bundle bundle=new Bundle();
            Log.e("Foxtrot",title);
            bundle.putString("cat",title);
            fragment.setArguments(bundle);*/
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        int id=item.getItemId();
        if(id==R.id.settings){
            Intent intent = new Intent(MainActivity.this, settings.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



}
