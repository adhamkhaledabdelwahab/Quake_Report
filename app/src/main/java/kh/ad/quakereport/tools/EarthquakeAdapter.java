package kh.ad.quakereport.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kh.ad.quakereport.R;
import kh.ad.quakereport.model.Earthquake;

@SuppressLint("NonConstantResourceId")
public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.mViewHolder> {

    ArrayList<Earthquake> mList;
    Context c;

    public EarthquakeAdapter(ArrayList<Earthquake> mList, Context c) {
        this.mList = mList;
        this.c = c;
    }

    public class mViewHolder extends RecyclerView.ViewHolder{

        TextView magnitude;

        TextView location1;

        TextView location2;

        TextView date1;

        TextView date2;

        LinearLayout itemClick;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            magnitude = itemView.findViewById(R.id.mag);
            location1 = itemView.findViewById(R.id.loc1);
            location2 = itemView.findViewById(R.id.loc2);
            date1 = itemView.findViewById(R.id.date1);
            date2 = itemView.findViewById(R.id.date2);
            itemClick = itemView.findViewById(R.id.ClickableBar);
        }
    }

    public EarthquakeAdapter(ArrayList<Earthquake> myList) {
        mList = myList;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        Earthquake current = mList.get(position);

        holder.itemClick.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(current.getUrl()));
            c.startActivity(browserIntent);
        });

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitude.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(current.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        holder.magnitude.setText(String.valueOf(current.getMagnitude()));
        if (current.getLocation().split("/")[0].isEmpty()){
            holder.location1.setVisibility(View.GONE);
        }else{
            holder.location1.setText(current.getLocation().split("/")[0]);
        }
        holder.location2.setText(current.getLocation().split("/")[1]);
        holder.date1.setText(current.getDate().split("/")[0]);
        holder.date2.setText(current.getDate().split("/")[1]);
    }

    public void clear() {
        int size = mList.size();
        mList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(List<Earthquake> a) {
        int size = a.size();
        mList.addAll(a);
        //notifyDataSetChanged();
        notifyItemRangeInserted(0,size);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(c, magnitudeColorResourceId);
    }
}
