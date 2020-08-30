package com.maiajam.myflowres.ui.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.maiajam.myflowres.helper.Constant;
import com.maiajam.myflowres.R;
import com.maiajam.myflowres.adapters.PageAdapter;
import com.maiajam.myflowres.databinding.FragmentViewPagerBinding;

public class ViewPagerFragment extends Fragment {

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentViewPagerBinding viewPagerBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_view_pager,container,false);
        ViewPager2 viewPager = viewPagerBinding.viewPager;
        initialZeViewPager(viewPager);

        ((AppCompatActivity)getActivity()).setSupportActionBar(viewPagerBinding.toolbar);
        new TabLayoutMediator(viewPagerBinding.tabs, viewPager,
                (tab, position) -> setTabsProperties(tab,position)
        ).attach();

        return viewPagerBinding.getRoot();
    }

    private void setTabsProperties(TabLayout.Tab tab, int position) {

        setTabText(tab,position);
        setTabIcon(tab,position);
    }

    private void setTabIcon(TabLayout.Tab tab, int position) {
    }


    private void setTabText(TabLayout.Tab tab, int position) {
       tab.setText((position == Constant.MY_FLOWERS_FRAGMENT)?getString(R.string.label_myFlowers_tabName):getString(R.string.label_allAflowers_tabName));
    }

    private void initialZeViewPager(ViewPager2 viewPager) {
        PageAdapter pagerAdapter = new PageAdapter(this);
        viewPager.setAdapter(pagerAdapter);

    }
}