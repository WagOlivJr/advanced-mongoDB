package com.wjrtech.mongo.service;

public class CalculoHaversine {

    private static final double RAIO_TERRA_KM = 6371;

    public static double calcularDistancia(double startLat, double startLong,
                                           double endLat, double endLong) {

        // Converter as latitudes e longitudes de graus para radianos
        double dLat = Math.toRadians(endLat - startLat);
        double dLon = Math.toRadians(endLong - startLong);

        // Converter as latitudes iniciais e finais para radianos
        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        // Aplicar a fórmula de Haversine
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(startLat) *
                        Math.cos(endLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calcular a distância e retornar o resultado
        return RAIO_TERRA_KM * c;

    }

}
