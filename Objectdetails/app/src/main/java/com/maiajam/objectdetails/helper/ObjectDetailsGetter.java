package com.maiajam.objectdetails.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.maiajam.objectdetails.R;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.IOException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.maiajam.objectdetails.helper.Global.BLACK_COLOR_ITEM;
import static com.maiajam.objectdetails.helper.Global.BLUE_COLOR_ITEM;
import static com.maiajam.objectdetails.helper.Global.CYAN_COLOR_ITEM;
import static com.maiajam.objectdetails.helper.Global.GREEN_COLOR_ITEM;
import static com.maiajam.objectdetails.helper.Global.PURPLE_COLOR_ITEM;
import static com.maiajam.objectdetails.helper.Global.RED_COLOR_ITEM;
import static com.maiajam.objectdetails.helper.Global.YELLOW_COLOR_ITEM;
import static java.lang.Math.floor;
import static org.opencv.core.Core.countNonZero;
import static org.opencv.core.Core.inRange;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2HSV;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2BGR;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class ObjectDetailsGetter {


    private Bitmap mTattoBitmap;
    private Handler mHandler;
    private static float dpi = (float) 95.96;
    private static boolean[] ColorsDetected;
    private Message message;
    private static float objectSize;
    private static Mat resizedImage;
    private static Bitmap imageBitmap;
    private Context mContext;
    private static ObjectCoordinateGetter[] objectCoordinateGetters;

    public ObjectDetailsGetter(Bitmap imageBitmap, Context mContext) {
        this.imageBitmap = imageBitmap;
        this.mContext = mContext;
    }

   public void getObjectDetails() {
        Mat Rgb = new Mat( imageBitmap.getHeight(),  imageBitmap.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat( imageBitmap, Rgb);
        Imgproc.cvtColor(Rgb, Rgb, COLOR_RGBA2BGR);
        resizeImage(Rgb);
        Mat object =
                getobject(resizedImage);

        Mat hsvobject = new Mat();
        cvtColor(object, hsvobject, COLOR_BGR2HSV);
        getObjectColors(hsvobject);
    }

    public int getobjectSize() {
        return (int) objectSize;
    }

    public boolean[] getobjectColors(Context context){
        return ColorsDetected;
    }

    private Bitmap convertUriTobitmap(Uri imageUri) {
        Bitmap photo = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_background);

        try {
            photo = MediaStore.Images.Media
                    .getBitmap(mContext.getContentResolver(),
                            imageUri);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return photo ;
    }


    private static void resizeImage(Mat rgb) {
        resizedImage = new Mat();
        Size newSize = new Size(500,500);
        Imgproc.resize(rgb,resizedImage,newSize,2.28,2.28,Imgproc.INTER_LANCZOS4);
    }

    private static void getObjectColors(Mat hsvTatto) {

        ColorsDetected = new boolean[7];
        ColorsDetected[RED_COLOR_ITEM] = DetectRedColor(hsvTatto);
        ColorsDetected[BLACK_COLOR_ITEM] = DetectBlackColor(hsvTatto);
        ColorsDetected[BLUE_COLOR_ITEM] = DetectBlueColor(hsvTatto);
        ColorsDetected[GREEN_COLOR_ITEM] = DetectGreenColor(hsvTatto);
        ColorsDetected[YELLOW_COLOR_ITEM] = DetectYellowColor(hsvTatto);
        ColorsDetected[CYAN_COLOR_ITEM] = DetectCyanColor(hsvTatto);
        ColorsDetected[PURPLE_COLOR_ITEM] =  DetectPurpleColor(hsvTatto);
    }

    public static Mat getobject(Mat matRgb) {
        Mat bgdModel = new Mat(), fgdModel = new Mat();
        Rect rec;
        Mat mask = new Mat(matRgb.size(), CV_8U, new Scalar(255, 255, 255));//cv::Mat::ones(src.size(), CV_8U) * cv::GC_PR_BGD;
        Mat newmask = new Mat();
        Mat object = new Mat();
        float margin = (float) .05;

        rec = new Rect(new Point(floor((matRgb.cols() - 1) * margin),
                floor((matRgb.rows() - 1) * margin)),
                new Point(matRgb.cols() - 1 - floor((matRgb.cols() - 1) * margin),
                        matRgb.rows() - 1 - floor((matRgb.rows() - 1) * margin)));

        Imgproc.grabCut(matRgb,
                mask,
                rec,
                bgdModel, fgdModel,
                1,
                Imgproc.GC_INIT_WITH_RECT & Imgproc.GC_INIT_WITH_MASK);

        Mat source = new Mat(1, 1, CvType.CV_8U);

        Core.compare(mask, source, newmask, Core.CMP_EQ);

        objectSize =
         GetobjectLength(newmask);

        matRgb.copyTo(object, newmask);
        return object;

    }

    private static float GetobjectLength(Mat src) {
        int width = 0;
        int length = 0;

        int firstX = 0;
        int firstY = 0;
        int lastX = 0;
        int lastY = 0;

        objectCoordinateGetters = new ObjectCoordinateGetter[4];

        Observable<ObjectDetailsGetter> observable = Observable.create(new ObservableOnSubscribe<ObjectDetailsGetter>() {
            @Override
            public void subscribe(ObservableEmitter<ObjectDetailsGetter> emitter) throws Exception {

            }
        });
        for (int col = 0; col < src.size().width; col++) {
            boolean flag = false;
            for (int row = 0; row < src.size().height; row++) {
                if (getPixelIntensityValues(src, row, col) != 0) {
                    firstX = col;
                    firstY = row;
                    flag = true;
                    break;
                }
            }
            if (flag)
                break;
        }

        for (int col = (int) (src.size().width - 1); col > 0; col--) {
            boolean flag = false;
            for (int row = (int) (src.size().height - 1); row > 0; row--) {
                if (getPixelIntensityValues(src, row, col) != 0)//do what you want;
                {
                    lastX = col;
                    lastY = row;
                    flag = true;
                    break;
                }
            }
            if (flag)
                break;
        }

        width = lastX - firstX;

        firstX = 0;
        firstY = 0;
        lastX = 0;
        lastY = 0;
        for (int row = 0; row < src.size().height; row++) {
            boolean flag = false;
            for (int col = 0; col < src.size().width; col++) {
                if (getPixelIntensityValues(src, row, col) != 0) {
                    firstX = col;
                    firstY = row;
                    flag = true;
                    break;
                }
            }
            if (flag)
                break;
        }

        for (int row = (int) (src.size().height - 1); row > 0; row--) {
            boolean flag = false;
            for (int col = (int) (src.size().width - 1); col > 0; col--) {
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

        length = lastY - firstY;

        float value = (float) 0.0;

        if (length > width)
            value = length / dpi;
        else
            value = width / dpi;

        return value;
    }

    private static int getPixelIntensityValues(Mat img, int row, int col) {
        byte[] imgData = new byte[(int) (img.total() * img.channels())];
        img.get(0, 0, imgData);
        byte intensity = imgData[row * img.cols() + col];
        return intensity;
    }

    private static Boolean DetectRedColor(Mat src) {

        Mat mask1 = new Mat(), mask2 = new Mat(), mask3 = new Mat();
        inRange(src, new Scalar(0, 120, 70), new Scalar(10, 255, 255), mask1);
        inRange(src, new Scalar(170, 120, 70), new Scalar(180, 255, 255), mask2);

        Core.add(mask1, mask2, mask3);
        Boolean flagColor = false;
        if (countNonZero(mask3) > 1) {
            flagColor = true;
        }
        return flagColor;
    }

    private static boolean DetectBlackColor(Mat src) {
        Mat mask1 = new Mat(), mask2 = new Mat(), mask3 = new Mat();

        inRange(src, new Scalar(0, 0, 10), new Scalar(99, 48, 53), mask1);
        inRange(src, new Scalar(0, 0, 10), new Scalar(28, 30, 109), mask2);

        Core.add(mask1, mask2, mask3);
        boolean flag = false;
        if (countNonZero(mask3) > 1) {
            flag = true;
        }
        return flag;
    }

    private static boolean DetectBlueColor(Mat src) {
        Mat mask1 = new Mat(), mask2 = new Mat(), mask3 = new Mat();

        inRange(src, new Scalar(106, 234, 238), new Scalar(116, 234, 238), mask1);
        inRange(src, new Scalar(95, 147, 234), new Scalar(106, 147, 234), mask2);

        Core.add(mask1, mask2, mask3);
        boolean flag = false;
        if (countNonZero(mask3) > 1) {
            flag = true;
        }
        return flag;
    }

    private static boolean DetectGreenColor(Mat src) {

        Mat mask1 = new Mat(), mask2 = new Mat(), mask3 = new Mat();

        inRange(src, new Scalar(49, 100, 238), new Scalar(59, 100, 238), mask1);
        inRange(src, new Scalar(49, 8, 151), new Scalar(110, 78, 187), mask2);

        Core.add(mask1, mask2, mask3);
        boolean flag = false;
        if (countNonZero(mask3) > 1) {
            flag = true;
        }
        return flag;
    }

    private static boolean DetectYellowColor(Mat src) {
        Mat mask1 = new Mat();
        inRange(src, new Scalar(3, 200, 200), new Scalar(30, 255, 255), mask1);
        boolean flag = false;
        if (countNonZero(mask1) > 1) {
            flag = true;
        }
        return flag;
    }

    private static boolean DetectCyanColor(Mat src) {

        Mat mask1 = new Mat();
        inRange(src, new Scalar(35, 255, 255), new Scalar(83, 255, 255), mask1);
        boolean flag = false;
        if (countNonZero(mask1) > 1)
            flag = true;
        return flag;
    }

    private static boolean DetectPurpleColor(Mat src) {
        Mat mask1 = new Mat();
        inRange(src, new Scalar(155, 244, 255), new Scalar(184, 244, 255), mask1);

        boolean flag = false;
        if (countNonZero(mask1) > 1) {
            flag = true;
        }
        return flag;
    }


}

