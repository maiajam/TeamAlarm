package com.maiajam.myflowres.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maiajam.myflowres.R;
import com.maiajam.myflowres.adapters.AllFlowersAdapter;
import com.maiajam.myflowres.data.modle.allFlowers.AllFlower;
import com.maiajam.myflowres.databinding.FragmentAllFlowersBinding;
import com.maiajam.myflowres.ui.viewmodle.AllFlowersViewModle;

import java.util.List;


public class AllFlowersFragment extends Fragment {

    FragmentAllFlowersBinding flowersBinding;
    AllFlowersViewModle allFlowersViewModle;
    AllFlowersAdapter allFlowersAdapter;

    public AllFlowersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        flowersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_flowers, container, false);
        return flowersBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInitialView(flowersBinding);
    }

    private void setInitialView(FragmentAllFlowersBinding flowersBinding) {

        allFlowersViewModle = new ViewModelProvider(requireActivity()).get(AllFlowersViewModle.class);
        allFlowersViewModle.getAllFlowers().observe(getActivity(), new Observer<List<AllFlower>>() {
            @Override
            public void onChanged(List<AllFlower> allFlowers) {
                allFlowersAdapter = new AllFlowersAdapter(allFlowers, getContext());
                flowersBinding.AllFlowersRec.setAdapter(allFlowersAdapter);
                allFlowersAdapter.notifyDataSetChanged();
            }
        });

    }
}