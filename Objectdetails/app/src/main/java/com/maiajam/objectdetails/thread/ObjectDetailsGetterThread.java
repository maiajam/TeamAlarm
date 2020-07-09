package com.maiajam.objectdetails.thread;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.maiajam.objectdetails.R;
import com.maiajam.objectdetails.helper.Global;
import com.maiajam.objectdetails.helper.ObjectCoordinateGetter;

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
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Math.floor;
import static org.opencv.core.Core.countNonZero;
import static org.opencv.core.Core.inRange;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2HSV;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2BGR;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class ObjectDetailsGetterThread extends Thread {


    private final Context mContext;
    private Bitmap mTattoBitmap;
    private Handler mHandler;
    private float dpi = (float) 95.96;
    private boolean[] ColorsDetected;
    private Message message;
    private float objectSize;
    private Mat resizedImage;
    private Bitmap imageUri;
    private ObjectCoordinateGetter[] objectCoordinateGetters;
    private Observable<Integer[]> observableGetFirstXY;
    private Observable<Integer[]> observableGetLastXY;
    private Disposable disposable;
    private Integer firstX, firstY;
    private Disposable disposableLast;
    private Integer lastX, lastY;
    private com.maiajam.objectdetails.helper.Global Global;
    private Bitmap mImageBitmap;

    public ObjectDetailsGetterThread(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void run() {
        super.run();


        Mat Rgb = new Mat(mImageBitmap.getHeight(), mImageBitmap.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(mImageBitmap, Rgb);
        Imgproc.cvtColor(Rgb, Rgb, COLOR_RGBA2BGR);

        resizeImage(Rgb);
        Mat object =
                getobject(resizedImage);

        Mat hsvobject = new Mat();
        cvtColor(object, hsvobject, COLOR_BGR2HSV);
        getTattoColors(hsvobject);
        message = new Message();
        Log.d("mai", "tatto size " + objectSize);
        message.arg1 = (int) objectSize;
        message.obj = ColorsDetected;
        mHandler.sendMessage(message);
    }


    private void resizeImage(Mat rgb) {
        resizedImage = new Mat();
        Size newSize = new Size(500, 500);
        Imgproc.resize(rgb, resizedImage, newSize);
    }

    private void getTattoColors(Mat hsvTatto) {

        ColorsDetected = new boolean[7];
        ColorsDetected[Global.RED_COLOR_ITEM] = DetectRedColor(hsvTatto);
        ColorsDetected[Global.BLACK_COLOR_ITEM] = DetectBlackColor(hsvTatto);
        ColorsDetected[Global.BLUE_COLOR_ITEM] = DetectBlueColor(hsvTatto);
        ColorsDetected[Global.GREEN_COLOR_ITEM] = DetectGreenColor(hsvTatto);
        ColorsDetected[Global.YELLOW_COLOR_ITEM] = DetectYellowColor(hsvTatto);
        ColorsDetected[Global.CYAN_COLOR_ITEM] = DetectCyanColor(hsvTatto);
        ColorsDetected[Global.PURPLE_COLOR_ITEM] = DetectPurpleColor(hsvTatto);
    }

    public Mat getobject(Mat matRgb) {
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

    private float GetobjectLength(Mat src) {
        int width = 0;
        int length = 0;

        // will divide the image to 16 part and looking for the first and last object  position
        int firstQuarterRow = (int) src.size().height / 4;
        int firstQuarterCol = (int) src.size().width / 4;
        int lastQuarterRow = (int) src.size().height - 1;
        int lastQuarterCol = (int) src.size().width - 1;

        objectCoordinateGetters = new ObjectCoordinateGetter[4];

        lookForFirstXY(src, firstQuarterRow, firstQuarterCol);
        lookForLastXY(src, lastQuarterRow, lastQuarterCol);

        width = lastX - firstX;
        length = lastY - firstY;


        float value = (float) 0.0;

        if (length > width)
            value = length / dpi;
        else
            value = width / dpi;

        return value;
    }

    private void lookForLastXY(Mat src, int lastQuarterRow, int lastQuarterCol) {
        for (int i = 0; i < objectCoordinateGetters.length; i++) {
            checkInEachQuarterHorizontal(src, i, lastQuarterRow, lastQuarterCol, Global.LAST_POSITION_HINT);
            for (int j = 0; j < objectCoordinateGetters.length; j++) {
                checkInEachQuarterVertically(src, j, i, lastQuarterRow, lastQuarterCol, Global.LAST_POSITION_HINT);
            }
        }
    }

    private void lookForFirstXY(Mat src, int firstQuarterRow, int firstQuarterCol) {
        for (int i = 0; i < objectCoordinateGetters.length; i++) {
            checkInEachQuarterHorizontal(src, i, firstQuarterRow, firstQuarterCol, Global.FIRST_POSITION_HINT);
            for (int j = 0; j < objectCoordinateGetters.length; j++) {
                checkInEachQuarterVertically(src, j, i, firstQuarterRow, firstQuarterCol, Global.FIRST_POSITION_HINT);
            }
        }
    }

    private void checkInEachQuarterVertically(Mat src, int j, int i, int QuarterRow, int QuarterCol, int hint) {
        // y is  for shifting to  the next quarter vertically
        i++;
        final int x = i;
        j++;
        final int y = j;
        int finalJ = j;
        objectCoordinateGetters[finalJ] = new ObjectCoordinateGetter();
        if (hint == Global.FIRST_POSITION_HINT) {
            observableGetFirstXY = Observable.create(new ObservableOnSubscribe<Integer[]>() {
                @Override
                public void subscribe(ObservableEmitter<Integer[]> emitter) throws Exception {
                    if (objectCoordinateGetters[finalJ] != null){
                        emitter.onNext(objectCoordinateGetters[finalJ].getFirstXY(src, QuarterRow * y, QuarterCol * x));
                    }

                }
            });
            observeFirstobjectPosition();
        } else if (hint == Global.LAST_POSITION_HINT) {
            observableGetLastXY = Observable.create(new ObservableOnSubscribe<Integer[]>() {
                @Override
                public void subscribe(ObservableEmitter<Integer[]> emitter) throws Exception {
                    emitter.onNext(objectCoordinateGetters[finalJ].getLastXY(src, QuarterRow / y, QuarterCol / x));
                }
            });
            observeLastobjectPosition();
        }


    }

    private void checkInEachQuarterHorizontal(Mat src, int i, int QuarterRow, int QuarterCol, int hint) {

        // x is  for shifting to  the next quarter horizontally
        i++;
        final int xHorizontally = i;
        int finalI = i;
        if (hint == Global.FIRST_POSITION_HINT) {
            observableGetFirstXY = Observable.create(new ObservableOnSubscribe<Integer[]>() {
                @Override
                public void subscribe(ObservableEmitter<Integer[]> emitter) throws Exception {
                    if(objectCoordinateGetters[finalI] != null)
                    emitter.onNext(objectCoordinateGetters[finalI].getFirstXY(src, QuarterRow, QuarterCol * xHorizontally));
                }
            });
            observeFirstobjectPosition();
        } else if (hint == Global.LAST_POSITION_HINT) {
            observableGetLastXY = Observable.create(new ObservableOnSubscribe<Integer[]>() {
                @Override
                public void subscribe(ObservableEmitter<Integer[]> emitter) throws Exception {
                    emitter.onNext(objectCoordinateGetters[finalI].getLastXY(src, QuarterRow, QuarterCol / xHorizontally));
                }
            });
            observeLastobjectPosition();
        }

    }


    private void observeFirstobjectPosition() {

        firstX = 0;
        firstY = 0;
        Observer<Integer[]> observeFirstXY = new Observer<Integer[]>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer[] firstPosition) {
                firstX = firstPosition[0];
                firstY = firstPosition[1];
                disposable.dispose();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

            }
        };
        disposable = observableGetFirstXY.subscribe();
        observableGetFirstXY.subscribe(observeFirstXY);
        observableGetFirstXY.subscribeOn(Schedulers.computation());
        observableGetFirstXY.observeOn(AndroidSchedulers.mainThread());

    }

    private void observeLastobjectPosition() {

        lastX = 0;
        lastY = 0;
        Observer<Integer[]> observeLastXY = new Observer<Integer[]>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer[] firstPosition) {
                lastX = firstPosition[0];
                lastY = firstPosition[1];
                disposableLast.dispose();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        disposableLast = observableGetLastXY.subscribe();
        observableGetLastXY.subscribe(observeLastXY);
        observableGetLastXY.subscribeOn(Schedulers.computation());
        observableGetLastXY.observeOn(AndroidSchedulers.mainThread());
    }

    private int getPixelIntensityValues(Mat img, int row, int col) {
        byte[] imgData = new byte[(int) (img.total() * img.channels())];
        img.get(0, 0, imgData);
        byte intensity = imgData[row * img.cols() + col];
        return intensity;
    }

    private Boolean DetectRedColor(Mat src) {

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

    private boolean DetectBlackColor(Mat src) {
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

    private boolean DetectBlueColor(Mat src) {
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

    private boolean DetectGreenColor(Mat src) {

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

    private boolean DetectYellowColor(Mat src) {
        Mat mask1 = new Mat();
        inRange(src, new Scalar(3, 200, 200), new Scalar(30, 255, 255), mask1);
        boolean flag = false;
        if (countNonZero(mask1) > 1) {
            flag = true;
        }
        return flag;
    }

    private boolean DetectCyanColor(Mat src) {

        Mat mask1 = new Mat();
        inRange(src, new Scalar(35, 255, 255), new Scalar(83, 255, 255), mask1);
        boolean flag = false;
        if (countNonZero(mask1) > 1)
            flag = true;
        return flag;
    }

    private boolean DetectPurpleColor(Mat src) {
        Mat mask1 = new Mat();
        inRange(src, new Scalar(155, 244, 255), new Scalar(184, 244, 255), mask1);

        boolean flag = false;
        if (countNonZero(mask1) > 1) {
            flag = true;
        }
        return flag;
    }

    public void setBitmap(Bitmap objectBitmap) {
        mImageBitmap = objectBitmap;
    }

}
