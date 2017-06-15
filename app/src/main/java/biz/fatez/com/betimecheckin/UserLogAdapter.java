package biz.fatez.com.betimecheckin;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Fatez on 4/22/2016.
 */

public class UserLogAdapter extends RecyclerView.Adapter<UserLogAdapter.UserViewHolder> {

    private ArrayList<LogObj> mDataSet;
    public UserLogAdapter(ArrayList<LogObj> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @Override
    public UserLogAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_row_layout, parent, false);
        UserLogAdapter.UserViewHolder userViewHolder = new UserLogAdapter.UserViewHolder(v);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(UserLogAdapter.UserViewHolder holder, int position) {

        holder.timestamp.setText(mDataSet.get(position).getTimestampId());
        holder.userAd.setText(mDataSet.get(position).getUserAd());
        holder.timestamp.setText(mDataSet.get(position).getTimestamp());
        holder.status.setText(mDataSet.get(position).getStatus());
        holder.latlng.setText(mDataSet.get(position).getLatlng());
        holder.address.setText(mDataSet.get(position).getAddress());
        holder.ip_address.setText(mDataSet.get(position).getIp_address());

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView timestampId, userAd, status,timestamp,latlng,address,ip_address;

        UserViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.user_layout1);
            status = (TextView) itemView.findViewById(R.id.icon_entry1);
            timestampId = (TextView) itemView.findViewById(R.id.timestampId);
            userAd = (TextView) itemView.findViewById(R.id.userAd);
            timestamp = (TextView)itemView.findViewById(R.id.timestamp);
            latlng = (TextView)itemView.findViewById(R.id.latlng);
            address = (TextView)itemView.findViewById(R.id.address);
            ip_address = (TextView)itemView.findViewById(R.id.ip_address);

            status.setTextColor(Color.parseColor("#ffffff"));


        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
