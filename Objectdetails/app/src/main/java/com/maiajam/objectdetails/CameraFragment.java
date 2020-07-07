package com.maiajam.objectdetails;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraFragment extends Fragment implements View.OnClickListener {

    private final int REQUEST_CODE_PERMISSION = 100 ;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private Executor executor = Executors.newSingleThreadExecutor();
    private PreviewView mCameraPreview;
    private Button mCaptureButton;
    private CameraInfo cameraInfo;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera,container,false);
        mCameraPreview = view.findViewById(R.id.CameraFragment_PreviewView_CameraView);
        mCaptureButton = view.findViewById(R.id.CameraFragment_Button_Capture);

        mCaptureButton.setOnClickListener(this::onClick);

        if(isAllPermissionGenerated())
            startCamera();
        else{
            requestPermissions(REQUIRED_PERMISSIONS,REQUEST_CODE_PERMISSION);
        }
        return view;
    }


    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
      //  HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        //if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
          //  hdrImageCaptureExtender.enableExtension(cameraSelector);
      //  }

        final ImageCapture imageCapture = builder
                .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .build();


        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);
        preview.setSurfaceProvider(mCameraPreview.createSurfaceProvider(camera.getCameraInfo()));


        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");

                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Image Saved successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        error.printStackTrace();
                    }
                });
            }
        });
    }

    private String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
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

    @Override
    public void onClick(View view) {

    }
}
