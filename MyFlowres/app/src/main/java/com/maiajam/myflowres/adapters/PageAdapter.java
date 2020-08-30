package com.maiajam.myflowres.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.maiajam.myflowres.helper.Constant;
import com.maiajam.myflowres.ui.fragments.AllFlowersFragment;
import com.maiajam.myflowres.ui.fragments.MyGardenFragment;

public class PageAdapter extends FragmentStateAdapter {


    final int FRAGMENTS_NUM = 2;

    public PageAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @Override
    public int getItemCount() {
        return FRAGMENTS_NUM;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return  (Constant.MY_FLOWERS_FRAGMENT==position)?new MyGardenFragment():new AllFlowersFragment();
    }

}
