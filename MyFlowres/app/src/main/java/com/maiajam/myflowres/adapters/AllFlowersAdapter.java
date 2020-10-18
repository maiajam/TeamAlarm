package com.maiajam.myflowres.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.maiajam.myflowres.R;
import com.maiajam.myflowres.data.modle.allFlowers.AllFlower;
import com.maiajam.myflowres.databinding.ItemFlowersBinding;

import java.util.List;

public class AllFlowersAdapter extends RecyclerView.Adapter<AllFlowersAdapter.AllFlowersHolder> {


    List<AllFlower> allFlowerList ;
    Context mContext;

    public AllFlowersAdapter(List<AllFlower> allFlowerList, Context context) {
        this.allFlowerList = allFlowerList;
        this.mContext = context ;
    }

    @NonNull
    @Override
    public AllFlowersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFlowersBinding itemFlowersBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.item_flowers,parent,false);
        return new AllFlowersHolder(itemFlowersBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllFlowersHolder holder, int position) {

        AllFlower flower = allFlowerList.get(position);
        holder.bindingItems(flower);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class AllFlowersHolder extends RecyclerView.ViewHolder{

        private final ItemFlowersBinding itemFlowersBinding;

        AllFlowersHolder(ItemFlowersBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemFlowersBinding = itemBinding;
        }

        private void bindingItems(AllFlower itemView) {
            itemFlowersBinding.username.setText(itemView.getUser().getFirstName());
        }
    }
}
