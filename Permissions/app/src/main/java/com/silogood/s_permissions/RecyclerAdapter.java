package com.silogood.s_permissions;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ChoiDW on 2015-11-28.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>  {
     Context context;
    List<Recycler_item> items;
    int item_layout;
    public RecyclerAdapter(Context context, List<Recycler_item> items, int item_layout) {
        this.context=context;
        this.items=items;
        this.item_layout=item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview,null);
        return new ViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Recycler_item item=items.get(position);
       Drawable drawable=item.getImage();
      //  Drawable drawable = context.getResources().getDrawable(position);
        holder.image.setBackground(drawable);
        holder.title.setText(item.getTitle());
        holder.packagename.setText(item.getPackagename());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(context,Applications_Permissions.class);
                    intent.putExtra("PackageName", item.getPackagename());
                    Log.v("", "SSSSSSSSSSS" + item.getPackagename());
                    Log.v("", "ssssssssssss2" +intent.getDataString());
                    v.getContext().startActivity(intent);
                   Log.v("", "ssssssssssss3" +intent);
                    //startActivity(intent);
                    //  perfoem your action here

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView packagename;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.image);
            title=(TextView)itemView.findViewById(R.id.title);
            packagename=(TextView)itemView.findViewById(R.id.packagename);
            cardview=(CardView)itemView.findViewById(R.id.cardview);
        }
    }
}