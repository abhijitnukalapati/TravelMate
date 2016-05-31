package com.gill.travelmate.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gill.travelmate.R;
import com.gill.travelmate.adapter.MySavesAdapter;
import com.gill.travelmate.utils.TinyDB;
import com.gill.travelmate.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */

//MySaves main fragment
public class MySavesFragment extends Fragment {

    TextView tv_message;
    RecyclerView recyclerView;
    Context mContext;
    TinyDB tinyDB;
    LinearLayoutManager layoutManager;
    MySavesAdapter adapter;

    public MySavesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_my_saves, container, false);

        mContext=getActivity();
        tinyDB=new TinyDB(mContext);

        tv_message=(TextView)v.findViewById(R.id.tv_message);
        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        //get data from local DB where we save MySaves data and set adapter
        if(Utils.getMySavesArr(tinyDB)!=null&&Utils.getMySavesArr(tinyDB).size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            tv_message.setVisibility(View.GONE);

            adapter = new MySavesAdapter(mContext, Utils.getMySavesArr(tinyDB));
            recyclerView.setAdapter(adapter);
        }else{
            tv_message.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}
