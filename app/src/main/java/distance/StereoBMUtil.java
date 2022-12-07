package distance;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;

import android.graphics.Bitmap;

import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class StereoBMUtil {
    private static final String TAG = StereoBMUtil.class.getName();
    // 【需要根据摄像头修改参数】
    private static final int imageWidth = 480;                      // 单目图像的宽度
    private static final int imageHeight = 320;                      // 单目图像的高度
    private Mat Q = new Mat();

    //映射表
    private Mat mapLx = new Mat();
    private Mat mapLy = new Mat();
    private Mat mapRx = new Mat();
    private Mat mapRy = new Mat();

    //    private StereoBM bm = StereoBM.create();
    private StereoSGBM bm = StereoSGBM.create(0,96,9,8*9*9,32*9*9,1,63,10,100,32, StereoSGBM.MODE_SGBM);
    private Mat xyz;

    public StereoBMUtil() {
        System.out.println("进入2");
        Mat cameraMatrixL = new Mat(3, 3, CvType.CV_64F);
        Mat distCoeffL = new Mat(5, 1, CvType.CV_64F);
        Mat cameraMatrixR = new Mat(3, 3, CvType.CV_64F);
        Mat distCoeffR = new Mat(5, 1, CvType.CV_64F);
        Mat T = new Mat(3, 1, CvType.CV_64F);
        Mat R = new Mat(3, 3, CvType.CV_64F);
        // 【需要根据摄像头修改参数】左目相机标定参数 fc_left_x  0  cc_left_x  0  fc_left_y  cc_left_y  0  0  1 //-1.290375522
        cameraMatrixL.put(0, 0, 471.7210848, -1.290375522, 258.0649567, 0, 473.367965, 175.7559263, 0, 0, 1);
        //【需要根据摄像头修改参数】左目相机标定参数 kc_left_01,  kc_left_02,  kc_left_03,  kc_left_04,   kc_left_05
        distCoeffL.put(0, 0, 0.0866861989752892, -0.116165518206748, 0.00850943723324226, 0.00550650531024472, 0.0);
        //【需要根据摄像头修改参数】右目相机标定参数 fc_right_x  0  cc_right_x  0  fc_right_y  cc_right_y  0  0  1 //-1.91404693
        cameraMatrixR.put(0, 0, 474.816445, -1.91404693, 244.6052438, 0, 476.0403941, 154.3788901, 0, 0, 1);
        //【需要根据摄像头修改参数】右目相机标定参数 kc_right_01,  kc_right_02,  kc_right_03,  kc_right_04,   kc_right_05
        distCoeffR.put(0, 0, 0.122252983702687, -0.245973209596523, -0.00155123763839407, 0.00906769546110424, 0.0);
        //【需要根据摄像头修改参数】T平移向量
        T.put(0, 0, -158.421592415655, 10.9827688100483, -13.8513966921966);
        // 【需要根据摄像头修改参数】rec旋转向量
        R.put(0, 0, 0.994106145, 0.08440893, -0.068030178, -0.081235418, 0.995531581, 0.048142279, 0.071789829, -0.042332076, 0.996521056);

        // Rl、Rr 左右相机的立体校正变换矩阵
        // Pl、Pr 左右相机在新坐标系下的投影矩阵
        // validROI 图像校正之后，会对图像进行裁剪，这里的validROI就是指裁剪之后的区域
        Size imageSize = new Size(imageWidth, imageHeight);
        Mat Rl = new Mat();
        Mat Rr = new Mat();
        Mat Pl = new Mat();
        Mat Pr = new Mat();
        Rect validROIL = new Rect();
        Rect validROIR = new Rect();

        // 双目校正准备工作
        // 获取立体校正矩阵，第一行为input参数，第二行为output参数
        Calib3d.stereoRectify(cameraMatrixL, distCoeffL, cameraMatrixR, distCoeffR, imageSize, R, T,
                Rl, Rr, Pl, Pr, Q, Calib3d.CALIB_ZERO_DISPARITY, 0, imageSize, validROIL, validROIR);
        // 立体校正矩阵映射到畸变矫正矩阵


        Imgproc.initUndistortRectifyMap(cameraMatrixL, distCoeffL, Rl, Pl, imageSize, CvType.CV_32FC1, mapLx, mapLy);
        Imgproc.initUndistortRectifyMap(cameraMatrixR, distCoeffR, Rr, Pr, imageSize, CvType.CV_32FC1, mapRx, mapRy);

//        int blockSize = 18;
//        int numDisparities = 11;
//        int uniquenessRatio = 5;
//        bm.setBlockSize(2 * blockSize + 5);                           //SAD窗口大小
//        bm.setROI1(validROIL);                                        //左右视图的有效像素区域
//        bm.setROI2(validROIR);
//        bm.setPreFilterCap(61);                                       //预处理滤波器
//        bm.setMinDisparity(32);                                       //最小视差，默认值为0, 可以是负值，int型
//        bm.setNumDisparities(numDisparities * 16);                    //视差窗口，即最大视差值与最小视差值之差,16的整数倍
//        bm.setTextureThreshold(10);
//        bm.setUniquenessRatio(uniquenessRatio);                       //视差唯一性百分比,uniquenessRatio主要可以防止误匹配
//        bm.setSpeckleWindowSize(100);                                 //检查视差连通区域变化度的窗口大小
//        bm.setSpeckleRange(32);                                       //32视差变化阈值，当窗口内视差变化大于阈值时，该窗口内的视差清零
//        bm.setDisp12MaxDiff(-1);
        System.out.println("出来2");
    }

    public Bitmap compute(Bitmap left, Bitmap right) {
        Mat rgbImageL = new Mat();
        Mat rgbImageR = new Mat();
        Mat grayImageL = new Mat();
        Mat grayImageR = new Mat();
        Mat rectifyImageL = new Mat();
        Mat rectifyImageR = new Mat();

        //用于存放每个像素点距离相机镜头的三维坐标
        xyz = new Mat();
        Mat disp = new Mat();

        // 双目校正
        // 1. 将图像从bitmap格式转为Mat格式
        // 2. 将图像颜色空间从BGR转为Gray
        // 3. 重映射
        bitmapToMat(left, rgbImageL);
        bitmapToMat(right, rgbImageR);
        Imgproc.cvtColor(rgbImageL, grayImageL, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(rgbImageR, grayImageR, Imgproc.COLOR_BGR2GRAY);
        Imgproc.remap(grayImageL, rectifyImageL, mapLx, mapLy, Imgproc.INTER_LINEAR);
        Imgproc.remap(grayImageR, rectifyImageR, mapRx, mapRy, Imgproc.INTER_LINEAR);

        // 计算视差图disp，输入图像必须为灰度图
        bm.compute(rectifyImageL, rectifyImageR, disp);

        // 计算深度图，即算出xyz
        Calib3d.reprojectImageTo3D(disp, xyz, Q, true);  // 在实际求距离时，ReprojectTo3D出来的X / W, Y / W, Z / W都要乘以16
        Core.multiply(xyz, new Mat(xyz.size(), CvType.CV_32FC3, new Scalar(16, 16, 16)), xyz);

        // 用于显示处理
        Mat disp8U = new Mat(disp.rows(), disp.cols(), CvType.CV_8UC1);
        disp.convertTo(disp, CvType.CV_32F, 1.0 / 16);               //除以16得到真实视差值
        Core.normalize(disp, disp8U, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
        Imgproc.medianBlur(disp8U, disp8U, 9);
        Bitmap resultBitmap = Bitmap.createBitmap(disp8U.cols(), disp8U.rows(), Bitmap.Config.ARGB_8888);
        matToBitmap(disp8U, resultBitmap);
        return resultBitmap;
    }

    public double[] getCoordinate(int dstX, int dstY) {
        double x = xyz.get(dstY, dstX)[0];
        double y = xyz.get(dstY, dstX)[1];
        double z = xyz.get(dstY, dstX)[2];
        return new double[]{x, y, z};
    }

    // 返回摄像头焦距，单位毫米
    public float getFocalLength(){
        return (float) Q.get(2, 3)[0];
    }

    public static int getImageHeight() {
        return imageHeight;
    }

    public static int getImageWidth() {
        return imageWidth;
    }
}
