package com.example.assessment2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class CustomAdapter_Volunteer extends ArrayAdapter<Volunteer> implements View.OnClickListener {
    private final Volunteer[] dataSet;
    DBase database = new DBase();
    Context mContext;

    //View lookup

    private static class ViewHolder{
        TextView txtID;
        TextView txtName;
        TextView txtPhoneNum;

    }

    public CustomAdapter_Volunteer(Volunteer[] data, Context context){
        super(context, R.layout.row_item_volunteer, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v){
        int position = (Integer) v.getTag();
        Volunteer Volunteer = getItem(position);

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
        Volunteer Volunteer = getItem(position);
        //Check for existed view, inflate if none exist
        ViewHolder viewHolder;

        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_volunteer, parent, false);
            viewHolder.txtID = convertView.findViewById(R.id.id);
            viewHolder.txtPhoneNum = convertView.findViewById(R.id.PhoneNum);
            viewHolder.txtName = convertView.findViewById(R.id.name);



            result = convertView;

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        assert Volunteer != null;
        viewHolder.txtID.setText("ID: " + Volunteer.getID());

        viewHolder.txtName.setText("Name: " + Volunteer.getName());

        viewHolder.txtPhoneNum.setText("Phone Number: " + Volunteer.getPhoneNumber());


        // Return the completed view to render on screen
        return convertView;
    }
}
