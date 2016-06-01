package com.gill.travelmate.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gill.travelmate.R;
import com.gill.travelmate.data.TravelContract;
import com.gill.travelmate.fragments.WeatherFragment;
import com.gill.travelmate.utils.Utils;

//Adapter to add data into WeatherFragment
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ForecastAdapterViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean mUseTodayLayout = true;

    private Cursor mCursor;
    final private Context mContext;

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mIconView;
        public final TextView mDateView;
        public final TextView mDescriptionView;
        public final TextView mHighTempView;
        public final TextView mLowTempView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            mIconView = (ImageView) view.findViewById(R.id.list_item_icon);
            mDateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            mDescriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            mHighTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            mLowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dateColumnIndex = mCursor.getColumnIndex(TravelContract.WeatherEntry.COLUMN_DATE);

        }
    }


    public WeatherAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_TODAY: {
                    layoutId = R.layout.list_item_forecast_today;
                    break;
                }
                case VIEW_TYPE_FUTURE_DAY: {
                    layoutId = R.layout.list_item_forecast;
                    break;
                }
            }
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new ForecastAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);
        int weatherId = mCursor.getInt(WeatherFragment.COL_WEATHER_CONDITION_ID);


        ViewCompat.setTransitionName(forecastAdapterViewHolder.mIconView, "iconView" + position);
        String description = Utils.getStringForWeatherCondition(mContext, weatherId);

        // Find TextView and set weather forecast on it
        forecastAdapterViewHolder.mDescriptionView.setText(description);
        forecastAdapterViewHolder.mDescriptionView.setContentDescription(mContext.getString(R.string.a11y_forecast, description));

        double high = mCursor.getDouble(WeatherFragment.COL_WEATHER_MAX_TEMP);
        String highString = Utils.formatTemperature(mContext, high);
        forecastAdapterViewHolder.mHighTempView.setText(highString);
        forecastAdapterViewHolder.mHighTempView.setContentDescription(mContext.getString(R.string.a11y_high_temp, highString));

        double low = mCursor.getDouble(WeatherFragment.COL_WEATHER_MIN_TEMP);
        String lowString = Utils.formatTemperature(mContext, low);
        forecastAdapterViewHolder.mLowTempView.setText(lowString);
        forecastAdapterViewHolder.mLowTempView.setContentDescription(mContext.getString(R.string.a11y_low_temp, lowString));

    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

}
