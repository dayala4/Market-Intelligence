public class MovingAverageData {

    private double ma20;
    private double ma50;
    private double ma200;


    public MovingAverageData(
            double ma20,
            double ma50,
            double ma200
    ) {

        this.ma20 = ma20;
        this.ma50 = ma50;
        this.ma200 = ma200;

    }


    public double getMa20() {
        return ma20;
    }


    public double getMa50() {
        return ma50;
    }


    public double getMa200() {
        return ma200;
    }

}