public class PredictionResult {


    private double predictedPrice;

    private String direction;

    private PredictionPeriod period;



    public PredictionResult(
            double predictedPrice,
            String direction,
            PredictionPeriod period) {

        this.predictedPrice = predictedPrice;
        this.direction = direction;
        this.period = period;

    }



    public double getPredictedPrice() {

        return predictedPrice;

    }



    public String getDirection() {

        return direction;

    }



    public PredictionPeriod getPeriod() {

        return period;

    }

}