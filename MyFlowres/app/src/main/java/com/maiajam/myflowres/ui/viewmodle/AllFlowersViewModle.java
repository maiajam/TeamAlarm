package com.maiajam.myflowres.ui.viewmodle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.maiajam.myflowres.data.modle.allFlowers.AllFlower;
import com.maiajam.myflowres.data.repo.AllFlowersRepo;
import java.util.List;

public class AllFlowersViewModle extends ViewModel {

    MutableLiveData<List<AllFlower>> listMutableLiveData;
    AllFlowersRepo allFlowersRepo;

    public MutableLiveData<List<AllFlower>> getAllFlowers() {
        listMutableLiveData = AllFlowersRepo.getINSTANCE().getAllFlowers();
        return listMutableLiveData;
    }
}
