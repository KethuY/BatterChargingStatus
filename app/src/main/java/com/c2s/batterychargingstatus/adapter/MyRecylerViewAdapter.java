package com.c2s.batterychargingstatus.adapter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.c2s.batterychargingstatus.R;
import com.c2s.batterychargingstatus.model.User;

import java.util.List;

/**
 * Created by satya on 26-Sep-17.
 */

public class MyRecylerViewAdapter extends RecyclerView.Adapter<MyRecylerViewAdapter.MyViewHolder> {

    List<User> mUsers;
    List<String> mNames;

    public MyRecylerViewAdapter(List<User>  users) {
        mUsers = users;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recylerview_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user=mUsers.get(position);
        holder.mName.setText(user.getName());
        holder.mAge.setText(user.getAge());
        holder.mTime.setText(user.getTime());
    }

    @Override
    public int getItemCount() {
        return null!=mUsers? mUsers.size():0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
       TextView mName,mAge,mTime;

        MyViewHolder(View itemView) {
            super(itemView);

            mName= itemView.findViewById(R.id.name_tv);
            mAge=itemView.findViewById(R.id.age_tv);
            mTime=itemView.findViewById(R.id.time_tv);

        }
    }
}
