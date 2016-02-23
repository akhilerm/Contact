package com.slateandpencil.contact;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class Tab extends Fragment {
    String category;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> myDataset;
    private SQLiteDatabase sb;
    public Tab() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_tab, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle bundle=this.getArguments();
        sb = getActivity().openOrCreateDatabase("contact",android.content.Context.MODE_PRIVATE, null);
        if(bundle!=null){
            category=bundle.getString("cat");
        }
        mRecyclerView = (RecyclerView)(getView()).findViewById(R.id.contact_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset=new ArrayList<String>();
        Cursor resultset = sb.rawQuery("select name,mob from details where category='"+category+"'", null);
        while(resultset.moveToNext()){
            myDataset.add(resultset.getString(0));
        }
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);


    }
}
