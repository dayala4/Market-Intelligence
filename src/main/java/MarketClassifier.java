
public class MarketClassifier {

    public static MarketSector classify(String sector) {

        if (sector == null || sector.trim().isEmpty()) {
            return MarketSector.OTHER;
        }

        String value = sector.trim().toUpperCase();

        if (value.contains("TECH")) {
            return MarketSector.TECHNOLOGY;
        }

        if (value.contains("HEALTH")
                || value.contains("PHARMA")
                || value.contains("BIOTECH")) {
            return MarketSector.HEALTHCARE;
        }

        if (value.contains("ENERGY")
                || value.contains("OIL")
                || value.contains("GAS")) {
            return MarketSector.ENERGY;
        }

        if (value.contains("FINANCE")
                || value.contains("FINANCIAL")
                || value.contains("BANK")
                || value.contains("INSURANCE")) {
            return MarketSector.FINANCE;
        }

        if (value.contains("CONSUMER")
                || value.contains("RETAIL")
                || value.contains("CYCLICAL")
                || value.contains("DEFENSIVE")
                || value.contains("STAPLES")) {
            return MarketSector.CONSUMER;
        }

        if (value.contains("INDUSTR")
                || value.contains("AEROSPACE")
                || value.contains("DEFENSE")) {
            return MarketSector.INDUSTRIALS;
        }

        if (value.contains("REAL ESTATE")
                || value.contains("REALTY")) {
            return MarketSector.REAL_ESTATE;
        }

        if (value.contains("UTILITY")
                || value.contains("UTILITIES")) {
            return MarketSector.UTILITIES;
        }

        if (value.contains("COMMUNICATION")
                || value.contains("TELECOM")) {
            return MarketSector.COMMUNICATIONS;
        }

        if (value.contains("MATERIAL")
                || value.contains("MINING")
                || value.contains("CHEMICAL")) {
            return MarketSector.MATERIALS;
        }

        return MarketSector.OTHER;
    }
}
