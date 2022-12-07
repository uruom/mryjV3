package distance;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * @author uruom
 * @version 1.0
 * @ClassName DistanceCal
 * @date 2022/12/2 13:29
 */

/*
 * 该类中的所有坐标均以左图象坐标系为参考系
 */
public class DistanceCal {
    private Bitmap leftBitmap;
    private Bitmap rightBitmap;
    private final int[][] coordinate = new int[2][];
    private final StereoBMUtil stereoBMUtil;
    private float[] dis;
    private float[] angle;

    private final int ANGLE_FRONT_SMALL = 10;
    private final int ANGLE_FRONT_LARGE = 20;
    private final int ANGLE_SIDE = 45;
    private final int DIS_SHORT = 2;
    private final int DIS_LONG = 5;

    public DistanceCal(){
        System.out.println("进入");
        this.stereoBMUtil = new StereoBMUtil();
        System.out.println("出来");
    }

    /*
     * 传入待计算图像和坐标点
     * @param leftBitmap, rightBitmap 左右输入图像
     * @param cordinateX, cordinateY xy坐标
     */
    public void initImage(Bitmap leftBitmap, Bitmap rightBitmap, int[] coordinateX, int[] coordinateY){
        this.leftBitmap = leftBitmap;
        this.rightBitmap = rightBitmap;
        this.coordinate[0] = coordinateX;
        this.coordinate[1] = coordinateY;
    }

    /*
     * 计算坐标点对应的距离，单位：米
     */
    private void calDistance(){
        dis = new float[coordinate[0].length];
        stereoBMUtil.compute(leftBitmap, rightBitmap);
        for(int i=0;i<coordinate[0].length;i++){
            double[] c = stereoBMUtil.getCoordinate(coordinate[0][i], coordinate[1][i]);
            dis[i] = (float)c[2] / 1000;
        }
    }

    /*
     * 计算坐标点对应的角度，单位：°
     * 经测试，视野角度约在-30°~30°
     */
    private void calAngle(float f){
        // TODO
        // 实际视觉中轴相较于左图象中轴应向右平移，故middle在左图中轴的基础上加了一个平移项：30pixels
        double[] middle = stereoBMUtil.getCoordinate(StereoBMUtil.getImageWidth()/2+30, StereoBMUtil.getImageHeight()/2);
        angle = new float[coordinate[0].length];
        for(int i=0;i<angle.length;i++){
            double tan = (stereoBMUtil.getCoordinate(coordinate[0][i], coordinate[1][i])[0] - middle[0]) / f;
            angle[i] = (float) Math.toDegrees(Math.atan(tan));
            Log.d("test", "point " + i + ":" +  angle[i]);
        }
    }

    /*
     * 计算是否播报
     * @return 返回一个int[][]，每个坐标点都有两个属性（是否播报1/0，方向左中右0/1/2）
     */
    public int[][] castingJudge(){

        calDistance();
        calAngle(stereoBMUtil.getFocalLength());

        int[][] res = new int[angle.length][];

        for(int i=0;i<angle.length;i++){
            int attr1=-1;
            int attr2=-1;
            float angleAbs = Math.abs(angle[i]);
            Log.d("test", "distance:" + dis[i]);
            if(angleAbs <= ANGLE_FRONT_SMALL){
                attr1 = dis[i] < DIS_LONG ? 1 : 0;
            }
            else if(ANGLE_FRONT_SMALL < angleAbs && angleAbs <= ANGLE_SIDE){
                attr1 = dis[i] < DIS_SHORT ? 1 : 0;
            }

            if(ANGLE_FRONT_LARGE < angle[i] && angle[i] <= ANGLE_SIDE){
                attr2 = 2;
            }
            else if(-ANGLE_FRONT_LARGE < angle[i] && angle[i] <= ANGLE_FRONT_LARGE){
                attr2 = 1;
            }
            else if(-ANGLE_SIDE <= angle[i] && angle[i] <= -ANGLE_FRONT_LARGE){
                attr2 = 0;
            }
            Log.d("test", "attr1:"+attr1);
            Log.d("test", "attr2:"+attr2);
            res[i] = new int[]{attr1, attr2};
        }
        return res;
    }
}
