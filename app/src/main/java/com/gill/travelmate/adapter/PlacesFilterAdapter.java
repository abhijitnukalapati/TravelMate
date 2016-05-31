package com.gill.travelmate.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gill.travelmate.R;
import com.gill.travelmate.fragments.PlacesNearFragment;
import com.gill.travelmate.utils.GeneralValues;
import com.gill.travelmate.utils.TinyDB;
import com.gill.travelmate.utils.Utils;

import java.util.ArrayList;


//Adapter to add filter list of PlacesNear
public class PlacesFilterAdapter extends RecyclerView.Adapter<PlacesFilterAdapter.ChatHolder>{

    private Context mContext;
    private ArrayList<String> list,values_list;
    private ArrayList<Integer> images;
    PlacesNearFragment fragment;
    TinyDB tinyDB;

    public PlacesFilterAdapter(Context mContext, ArrayList<String> cat_arr, ArrayList<String> values_arr, ArrayList<Integer> images, PlacesNearFragment fragment) {
        this.mContext = mContext;
        this.list = cat_arr;
        this.images = images;
        values_list=values_arr;
        tinyDB=new TinyDB(mContext);
        this.fragment=fragment;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_filter_list, parent, false);

        final ChatHolder holder = new ChatHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.onRecyclerClick(holder.getAdapterPosition());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {
        holder.tv_title.setText(list.get(position));
        Glide.with(mContext).load(images.get(position)).placeholder(R.drawable.logo).into(holder.cat_image);
        if(tinyDB.getString(GeneralValues.PLACES_FILTER).equalsIgnoreCase(values_list.get(position))){
            holder.check_image.setImageResource(R.drawable.green_tick);
        }else{
            holder.check_image.setImageResource(R.drawable.grey_tick);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder{

        TextView tv_title;
        ImageView cat_image,check_image;
        LinearLayout ll_filter;

        public ChatHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            cat_image = (ImageView) itemView.findViewById(R.id.cat_image);
            check_image = (ImageView) itemView.findViewById(R.id.check_image);
            ll_filter = (LinearLayout) itemView.findViewById(R.id.ll_filter);

            Utils.typeface_font(mContext,tv_title);
        }
    }
}
