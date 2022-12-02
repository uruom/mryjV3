//package distance;
//
//import android.graphics.Bitmap;
//
///**
// * @author uruom
// * @version 1.0
// * @ClassName DistanceCal
// * @date 2022/12/2 13:29
// */
//
//public class DistanceCal {
//    private Bitmap leftBitmap;
//    private Bitmap rightBitmap;
//    private final float[][] coordinate = new float[2][];
//    private final StereoBMUtil stereoBMUtil;
//    private float[] dis;
//    private float[] angle;
//
//    public DistanceCal(){
//        this.stereoBMUtil = new StereoBMUtil();
//    }
//
//    /*
//     * 传入待计算图像和坐标点
//     * @param leftBitmap, rightBitmap 左右输入图像
//     * @param cordinateX, cordinateY xy坐标
//     */
//    public void initImage(Bitmap leftBitmap, Bitmap rightBitmap, float[] coordinateX, float[] coordinateY){
//        this.leftBitmap = leftBitmap;
//        this.rightBitmap = rightBitmap;
//        this.coordinate[0] = coordinateX;
//        this.coordinate[1] = coordinateY;
//    }
//
//    /*
//     * 计算坐标点对应的距离
//     */
//    private void calDistance(){
//        this.dis = new float[coordinate[0].length];
//        stereoBMUtil.compute(leftBitmap, rightBitmap);
//        for(int i=0;i<coordinate[0].length;i++){
//            double[] c = stereoBMUtil.getCoordinate((int)coordinate[0][i], (int)coordinate[1][i]);
//            // 距离单位是：米
//            this.dis[i] = (float)c[2] / 100;
//        }
//    }
//
//    /*
//     * 计算坐标点对应的角度
//     */
//    private void calAngle(){
//        // TODO
//        this.angle = new float[coordinate[0].length];
//    }
//
//    /*
//     * 计算是否播报
//     * @return 返回一个int[][]，每个坐标点都有两个属性（是否播报1/0，方向左中右0/1/2）
//     */
//    public int[][] castingJudge(){
//
//        calDistance();
//        calAngle();
//
//        int[][] res = new int[angle.length][];
//
//        for(int i=0;i<angle.length;i++){
//            int attr1=-1;
//            int attr2=-1;
//            float angleAbs = Math.abs(angle[i]);
//            if(angleAbs <= 10){
//                attr1 = dis[i] < 5 ? 1 : 0;
//            }
//            else if(10 < angleAbs && angleAbs <= 45){
//                attr1 = dis[i] < 2 ? 1 : 0;
//            }
//
//            if(20 < angle[i] && angle[i] <= 45){
//                attr2 = 2;
//            }
//            else if(-20 < angle[i] && angle[i] <= 20){
//                attr2 = 1;
//            }
//            else if(-45 <= angle[i] && angle[i] <= -20){
//                attr2 = 0;
//            }
//            res[i] = new int[]{attr1, attr2};
//        }
//        return res;
//    }
//}
