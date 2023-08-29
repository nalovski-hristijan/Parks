package com.hnalovski.parks.adapter;

import static android.os.Build.VERSION_CODES.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hnalovski.parks.R;
import com.hnalovski.parks.model.Park;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ParkRecyclerVIewAdapter extends RecyclerView.Adapter<ParkRecyclerVIewAdapter.ViewHolder> {
    private final List<Park> parkList;
    private final OnParkClickListener parkClickListener;

    public ParkRecyclerVIewAdapter(List<Park> parkList, OnParkClickListener parkClickListener) {
        this.parkList = parkList;
        this.parkClickListener = parkClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the view (show the item row)
        View view = LayoutInflater.from(parent.getContext()).inflate(com.hnalovski.parks.R.layout.park_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Park park = parkList.get(position);
        holder.parkName.setText(park.getName());
        holder.parkType.setText(park.getDesignation());
        holder.parkState.setText(park.getStates());
        if (park.getImages().size() > 0){
            Picasso.get().load(park.getImages().get(0).getUrl())
                    //shows a gif that shows that the image is being downloaded
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.stat_notify_error)
                    .resize(100,100)
                    .centerCrop()
                    .into(holder.parkImage);
        }
    }

    @Override
    public int getItemCount() {
        return parkList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView parkImage;
        public TextView parkName;
        public TextView parkType;
        public TextView parkState;
        OnParkClickListener onParkClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parkImage = itemView.findViewById(com.hnalovski.parks.R.id.row_park_imageview);
            parkName = itemView.findViewById(com.hnalovski.parks.R.id.row_park_name_textview);
            parkType = itemView.findViewById(com.hnalovski.parks.R.id.row_park_type_textview);
            parkState = itemView.findViewById(com.hnalovski.parks.R.id.row_park_state_textview);
            this.onParkClickListener = parkClickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Park currPark = parkList.get(getAdapterPosition());
            onParkClickListener.onParkClicked(currPark);
        }
    }
}
