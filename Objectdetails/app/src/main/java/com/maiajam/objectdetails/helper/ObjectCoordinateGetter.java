package com.maiajam.objectdetails.helper;

import org.opencv.core.Mat;

public class ObjectCoordinateGetter {

    int row, col;
    private int firstX, firstY;
    private int lastX,lastY;

    public ObjectCoordinateGetter(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public ObjectCoordinateGetter() {

    }

    public Integer[] getFirstXY(Mat src , int rowSrc, int colSrc) {
        Integer[] firstPostion =new Integer[2];
        for (int col = 0; col < colSrc; col++) {
            boolean flag = false;
            for (int row = 0; row < rowSrc; row++) {
                if (getPixelIntensityValues(src, row, col) != 0) {
                    firstX = col;
                    firstY = row;
                    flag = true;
                    break;
                }
            }
        }

        firstPostion[0] =firstX;
        firstPostion[1] = firstY ;
        return firstPostion;
    }

    public Integer[] getLastXY(Mat src, int rowSrc, int colSrc)
    {
        for (int row = rowSrc; row > 0; row--) {
            boolean flag = false;
            for (int col = colSrc; col > 0; col--) {
                if (getPixelIntensityValues(src, row, col) != 0) {
                    lastX = col;
                    lastY = row;
                    flag = true;
                    break;
                }
            }
            if (flag)
                break;
        }
        Integer lastPosition[] ={lastX,lastY};
        return lastPosition;
    }
    private int getPixelIntensityValues(Mat img, int row, int col) {
        byte[] imgData = new byte[(int) (img.total() * img.channels())];
        img.get(0, 0, imgData);
        byte intensity = imgData[row * img.cols() + col];
        return intensity;
    }
}
