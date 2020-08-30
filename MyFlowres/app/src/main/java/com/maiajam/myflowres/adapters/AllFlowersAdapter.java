package com.maiajam.myflowres.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AllFlowersAdapter extends RecyclerView.Adapter<AllFlowersAdapter.AllFlowersHolder> {


    @NonNull
    @Override
    public AllFlowersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AllFlowersHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class AllFlowersHolder extends RecyclerView.ViewHolder{

        public AllFlowersHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
