package com.maiajam.myflowres.data.repo;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.maiajam.myflowres.BuildConfig;
import com.maiajam.myflowres.data.modle.allFlowers.AllFlower;
import com.maiajam.myflowres.data.retrofit.ApiServer;


import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AllFlowersRepo {


    static Observable<List<AllFlower>> allFlowersObservable;
    static MutableLiveData<List<AllFlower>> listMutableLiveData;
    static private AllFlowersRepo INSTANCE;

    private AllFlowersRepo() {
        if (listMutableLiveData == null)
            fetchAllFlowersPhotos();
    }

    public static AllFlowersRepo getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new AllFlowersRepo();
        return INSTANCE;
    }

    public static MutableLiveData<List<AllFlower>> getAllFlowers() {
        return listMutableLiveData;
    }

    private void fetchAllFlowersPhotos() {

        allFlowersObservable = ApiServer.getINSTANCE().ApiServer()
                .getAllFlowersPhotos(1, 10, "last", BuildConfig.UNSPLASH_ACCESS_KEY)
                .observeOn((Schedulers.io()))
                .subscribeOn(AndroidSchedulers.mainThread());

        allFlowersObservable.subscribeWith(new Observer<List<AllFlower>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("obserMaiD", d.toString());
            }

            @Override
            public void onNext(List<AllFlower> allFlowers) {
                listMutableLiveData.setValue(allFlowers);
                Log.d("obserMaiD", "done");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("obserMaiD", "error");
            }

            @Override
            public void onComplete() {
                Log.d("obserMaiD", "complete");
            }
        });
    }

}
