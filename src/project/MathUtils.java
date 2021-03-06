package project;

import java.math.BigDecimal;
import project.entity.Location;

public class MathUtils {

    /**
     * @return the radian value from angles in degree
     */
    public static double toRadian(double degree) {
        return (Math.PI / 180) * degree;
    }

    public static double calcHaversineDistance(Location pos1, Location pos2) {
    	double R = 6371.0;
    	double dLat = toRadian(pos2.getLatitude() - pos1.getLatitude());
    	double dLon = toRadian(pos2.getLongitude() - pos1.getLongitude());
    	double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
    			+ Math.cos(toRadian(pos1.getLatitude()))
    			* Math.cos(toRadian(pos2.getLatitude())) * Math.sin(dLon / 2)
    			* Math.sin(dLon / 2);
    	double c = 2 * Math.asin(Math.min(1, Math.sqrt(a)));
    	double d = R * c;
    	return d;
    }

    public static double round(double value, int decimalPlaces) {
        return new BigDecimal(String.valueOf(value)).setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
