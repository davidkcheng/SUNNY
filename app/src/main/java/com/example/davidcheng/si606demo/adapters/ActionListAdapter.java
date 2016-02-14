package com.example.davidcheng.si606demo.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davidcheng.si606demo.R;

/**
 * Created by davidcheng on 12/10/15.
 */
public class ActionListAdapter extends BaseAdapter {

    String [] actionlist;
    Context context;
    int  layoutResID;
    LayoutInflater inflater;

    public ActionListAdapter(Context context, int LayoutResID, String[] actionlist) {
//        inflater = lf_in;
        this.actionlist=actionlist;
//        context=context_in;
//        imageId=prgmImages;
        this.layoutResID = LayoutResID;
        this.context = context;
//        inflater = ( LayoutInflater ) context.
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater = ( LayoutInflater ) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return actionlist.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;

//        int imagesrc = imageId[position];
//        String textsrc = result[position];
//        LayoutInflater inflater = LayoutInflater.from(context);
        rowView = inflater.inflate(layoutResID, null);
        holder.tv= (TextView) rowView.findViewById(R.id.action_id);

        holder.tv.setText(actionlist[position]);

        int a;
        if (position == 0) {
            a = context.getResources().getIdentifier("color1", "color", context.getPackageName());
        } else if (position == 1) {
            a = context.getResources().getIdentifier("color2", "color", context.getPackageName());
        } else {
            a = context.getResources().getIdentifier("color3", "color", context.getPackageName());
        }
        rowView.setBackgroundResource(a);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You chose " + actionlist[position], Toast.LENGTH_LONG).show();

            }
        });

        return rowView;
    }


}
