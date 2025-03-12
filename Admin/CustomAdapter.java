package com.example.assessment2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Event> implements View.OnClickListener {
    private final ArrayList<Event> dataSet;
    DBase database = new DBase();
    Context mContext;

    //View lookup

    private static class ViewHolder{
        TextView txtID;
        TextView txtDescription;
       TextView txtDate;
        TextView txtOwner;

        TextView txtGoal;

    }

    public CustomAdapter(ArrayList<Event> data, Context context){
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v){
        int position = (Integer) v.getTag();
        Event event = getItem(position);

        if (v.getId() == R.id.item_info) {
            Snackbar.make(v, "Redirecting to Map", Snackbar.LENGTH_LONG).
                    setAction("No action", null).show(); //CHANGE THIS LATER FOR MAP DIRECTING
        }
    }

    private int lastPosition = -1;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        //Get data item for this position;
        Event event = getItem(position);
        //Check for existed view, inflate if none exist
        ViewHolder viewHolder;

        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtID = convertView.findViewById(R.id.id);
            viewHolder.txtDescription = convertView.findViewById(R.id.description);
            viewHolder.txtDate = convertView.findViewById(R.id.date);
            viewHolder.txtOwner = convertView.findViewById(R.id.Owner);
            viewHolder.txtGoal = convertView.findViewById(R.id.Goal);


            result = convertView;

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        assert event != null;
        viewHolder.txtID.setText("ID: " + event.getLocation().getId());
        viewHolder.txtDescription.setText("Description: \n" + event.getLocation().getDescription());
        viewHolder.txtDate.setText("Date: "+ event.getLocation().getDate());
        viewHolder.txtOwner.setText("Owner: "+ event.getOwner().getName());
        viewHolder.txtGoal.setText("Goal: " + event.getLocation().getGoal() + " kg" );

        // Return the completed view to render on screen
        return convertView;
    }
}

