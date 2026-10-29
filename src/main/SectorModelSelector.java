public class SectorModelSelector {

    public static double[] getWeights(
            MarketSector sector,
            CompanyData company) {

        switch (sector) {

            case TECHNOLOGY:
                return TechnologyModel.getWeights(company);

            case HEALTHCARE:
                return HealthcareModel.getWeights(company);

            case ENERGY:
                return EnergyModel.getWeights(company);

            case FINANCE:
                return FinanceModel.getWeights(company);

            case CONSUMER:
                return ConsumerModel.getWeights(company);

            case INDUSTRIALS:
                return IndustrialsModel.getWeights(company);

            case REAL_ESTATE:
                return RealEstateModel.getWeights(company);

            case UTILITIES:
                return UtilitiesModel.getWeights(company);

            case COMMUNICATIONS:
                return CommunicationsModel.getWeights(company);

            case MATERIALS:
                return MaterialsModel.getWeights(company);

            case OTHER:
            default:
                return OtherModel.getWeights(company);
        }
    }
}