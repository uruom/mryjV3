package Veriable;

import android.graphics.Bitmap;

/**
 * @author uruom
 * @version 1.0
 * @ClassName TransmissionVeriable
 * @date 2022/9/4 20:13
 */
public class TransmissionVeriable {
    /**
     * leftImage/rightImage 分别是左右两张图片（辜set，袁、郑get）
     * timeStamp为时间戳（辜set）
     * coordinateX/coordinateY 为坐标位置 (袁set，郑get）
     * direction为最终方位，(郑set，张get)
     * distance为最终距离(郑set）
     * state 当前状态，即进行到了哪个步骤（all set，get）
     * Isfinished 当前状态是否完成，是否可进入下一个状态
     */
    private Bitmap leftImage = null;
    private Bitmap rightImage = null;
//    private Image leftImage = null;
//    private  Image rightImage = null;
    private int timeStamp = 0;
    private float coordinateX = 0, coordinateY = 0;
    private Edirection direction = null;
    private float distance = 0;
    private Estate state= null;
    private Boolean Isfinished = true;
    private int objects;
    private int modified[];

    public int[] getModified() {
        return modified;
    }

    public void setModified(int[] modified) {
        this.modified = modified;
    }

    public int getObjects() {
        return objects;
    }

    public void setObjects(int objects) {
        this.objects = objects;
    }

    public Boolean getIsfinished() {
        return Isfinished;
    }

    public Estate getState() {
        return state;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public Bitmap getLeftImage() {
        return leftImage;
    }

    public Bitmap getRightImage() {
        return rightImage;
    }

    public float getCoordinateX() {
        return coordinateX;
    }

    public float getCoordinateY() {
        return coordinateY;
    }

    public Edirection getDirection() {
        return direction;
    }

    public float getDistance() {
        return distance;
    }

    public void setCoordinateX(float coordinateX) {
        this.coordinateX = coordinateX;
    }

    public void setCoordinateY(float coordinateY) {
        this.coordinateY = coordinateY;
    }

    public void setDirection(Edirection direction) {
        this.direction = direction;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setRightImage(Bitmap rightImage) {
        this.rightImage = rightImage;
    }

    public void setLeftImage(Bitmap leftImage) {
        this.leftImage = leftImage;
    }

    public void setState(Estate state) {
        this.state = state;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setIsfinished(Boolean isfinished) {
        Isfinished = isfinished;
    }


    /**
     * 单例模式，以确保修改在同一个类中
     */

    private TransmissionVeriable(){};

    private static volatile  TransmissionVeriable transmissionVeriable = null;

    public static  TransmissionVeriable getInstance(){
        if (transmissionVeriable==null) {
            synchronized (TransmissionVeriable.class){
                if (transmissionVeriable==null) {
                    transmissionVeriable = new TransmissionVeriable();
                }
            }
        }
        return transmissionVeriable;
    }
}
