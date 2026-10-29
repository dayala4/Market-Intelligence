public class PredictionScore {

    private double historyScore;
    private double maScore;
    private double earningsScore;
    private double momentumScore;


    public PredictionScore(
            double historyScore,
            double maScore,
            double earningsScore,
            double momentumScore
    ) {

        this.historyScore = historyScore;
        this.maScore = maScore;
        this.earningsScore = earningsScore;
        this.momentumScore = momentumScore;

    }


    public double getFinalScore() {

        return
            (historyScore * 0.60)
            +
            (maScore * 0.25)
            +
            (earningsScore * 0.10)
            +
            (momentumScore * 0.05);

    }
}