package com.maiajam.myflowres.ui.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maiajam.myflowres.R;
import com.maiajam.myflowres.databinding.FragmentAllFlowersBinding;


public class AllFlowersFragment extends Fragment {

    FragmentAllFlowersBinding flowersBinding;

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

        flowersBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_all_flowers,container,false);
        setInitialView(flowersBinding);
        return flowersBinding.getRoot();
    }

    private void setInitialView(FragmentAllFlowersBinding flowersBinding) {


    }
}