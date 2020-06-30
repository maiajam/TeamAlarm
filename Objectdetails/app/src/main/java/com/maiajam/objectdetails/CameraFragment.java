package com.maiajam.objectdetails;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class CameraFragment extends Fragment {

    private final int REQUEST_CODE_PERMISSION = 100 ;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera,container);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isAllPermissionGenerated())
            startCamera();
        else{
            requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CODE_PERMISSION);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_PERMISSION);
        }




    }

    private void startCamera() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isAllPermissionGenerated() {
        for(String permission : REQUIRED_PERMISSIONS){
            if(getContext().checkSelfPermission( permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
