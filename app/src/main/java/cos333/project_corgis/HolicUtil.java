package cos333.project_corgis;


/**
 * Created by emilyzhang on 4/25/16.
 */
public class HolicUtil {

    public static double calcBAC(double prevBAC, long prevDrinkTime, double drinkAmount, double r, int weight) {
        if (prevBAC == 0) {
            // This is the first drink! use Widmark's equation without time decay
            // Equation taken from http://www.wsp.wa.gov/breathtest/docs/webdms/Studies_Articles/Widmarks%20Equation%2009-16-1996.pdf
            // C =  0.8 A z / (W r)
            double alc = 0.6 * drinkAmount; //fluid ounces of alcohol

            double C = Math.max(0.8 * alc / (weight * 16 * r), 0);
            return C*100;
        }
        else {
            //Use previous BAC and previous drink time along with the new consumed drink amount
            //to calculate current BAC
            // C = prevBAC + 0.8 * alc / (W r) - b (t - prevDrinkTime)
            double b = 0.00017; //kg/L/hr
            double dt = (double)(System.currentTimeMillis() - prevDrinkTime)/(1000*3600);
            double alc = 0.6 * drinkAmount; //fluid ounces of alcohol
            System.out.println("Debugging time decay " + dt);

            double C = Math.max((prevBAC/100) + (0.8 * alc / (weight * 16 * r)) - (b * dt), 0);

            return C*100;
        }
    }
}
