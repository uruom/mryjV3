package mryj;

/**
 * @author uruom
 * @version 1.0
 * @ClassName Automat
 * @date 2022/7/20 10:38
 */
public class Automat {
    private static Automat instance;
    private Estatus estatus=Estatus.Prime;

    private Automat() {}

    public Estatus getStatu(){
        return estatus;
    }
    public static Automat getAutomat() {
        if (instance==null) {
            instance = new Automat();
        }
        return instance;
    }
    public void updateStatu(boolean isPrime,boolean isNext,boolean isLast){
        if (isPrime) {
            estatus=Estatus.Prime;
        }else{
            switch (estatus){
                case Prime:
                    if (isNext) {
                        estatus = Estatus.IsStart;
                    }
                    if (isLast) {
                        estatus = Estatus.Prime;
                    }
                    break;
                case IsStart:
                    if (isNext) {
                        estatus = Estatus.Running;
                    }
                    if (isLast) {
                        estatus = Estatus.Prime;
                    }
                    break;
                case Running:
                    if (isNext) {
                        estatus = Estatus.IsClose;
                    }
                    if (isLast) {
                        estatus = Estatus.Running;
                    }
                    break;
                case IsClose:
                    if (isNext) {
                        estatus = Estatus.Prime;
                    }
                    if (isLast) {
                        estatus = Estatus.Running;
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
