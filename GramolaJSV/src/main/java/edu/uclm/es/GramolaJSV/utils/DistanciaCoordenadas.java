package edu.uclm.es.GramolaJSV.utils;

public class DistanciaCoordenadas {

    /**
     * Calcula la distancia en metros entre dos coordenadas (lat, lon) usando la
     * fórmula
     * de Haversine.
     *
     * @param lat1 latitud del punto 1 en grados
     * @param lon1 longitud del punto 1 en grados
     * @param lat2 latitud del punto 2 en grados
     * @param lon2 longitud del punto 2 en grados
     * @return distancia en metros
     */
    public static double calcularDistanciaMetros(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6_371_000d; // radio de la Tierra en metros
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                        * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
