package com.maiajam.editimage;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class EditImageThread extends Thread {

    @Override
    public void run() {
        super.run();
    }

    public EditImageThread(int funcId) {

    }

    public Mat compressImage(Mat srcMat,int maxHight,int maxWidth){
        Mat newCompressedImage = new Mat();
        Size size =new Size(maxWidth,maxHight);
        Imgproc.resize(srcMat,newCompressedImage,size);
        return newCompressedImage;
    }


}
