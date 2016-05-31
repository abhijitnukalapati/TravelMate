package com.gill.travelmate.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gill.travelmate.DetailActivity;
import com.gill.travelmate.R;
import com.gill.travelmate.arraylists.MySavesArraylist;
import com.gill.travelmate.utils.GeneralValues;
import com.gill.travelmate.utils.Utils;

import java.util.ArrayList;

//Adapter to add items into list of MySavesFragment
public class MySavesAdapter extends RecyclerView.Adapter<MySavesAdapter.ChatHolder>{

    private Context mContext;
    private ArrayList<MySavesArraylist> list;
    Intent i;

    public MySavesAdapter(Context mContext, ArrayList<MySavesArraylist> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_results, parent, false);

        final ChatHolder holder = new ChatHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(mContext, DetailActivity.class);
                i.putExtra("check", GeneralValues.MYSAVES_CHECK);
                i.putExtra("position",""+holder.getAdapterPosition());
                mContext.startActivity(i);
                ((Activity)mContext).overridePendingTransition(R.anim.to_leftin, R.anim.to_leftout);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {
        holder.tv_name.setText(list.get(position).getName());
        String reviews = list.get(position).getReviews()+ " "+mContext.getString(R.string.reviews_save);
        holder.tv_reviews.setText(reviews);
        holder.tv_categories.setText(list.get(position).getCategory());
        holder.rating.setRating(Float.parseFloat(list.get(position).getRating()));
        Glide.with(mContext).load(list.get(position).getImage()).placeholder(R.drawable.logo).error(R.drawable.no_image).into(holder.img_main);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder{

        TextView tv_name,tv_reviews,tv_categories;
        ImageView img_main,img_rating;
        RatingBar rating;

        public ChatHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_reviews = (TextView) itemView.findViewById(R.id.tv_distance);
            img_main = (ImageView) itemView.findViewById(R.id.img_main);
            img_rating = (ImageView) itemView.findViewById(R.id.img_rating);
            rating = (RatingBar) itemView.findViewById(R.id.rating);
            tv_categories = (TextView) itemView.findViewById(R.id.tv_categories);

            Utils.typeface_font(mContext,tv_name);
            Utils.typeface_font(mContext,tv_reviews);
            Utils.typeface_font(mContext,tv_categories);
        }
    }
}
