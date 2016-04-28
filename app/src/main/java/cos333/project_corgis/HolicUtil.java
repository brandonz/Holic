package cos333.project_corgis;


/**
 * Created by emilyzhang on 4/25/16.
 */
public class HolicUtil {

    public static double calcBAC(long millis,  double bacNumDrinks, double r, int weight) {
        if (millis == 0)
            return 0;

        //use Widmark's equation
        // Equation taken from http://www.wsp.wa.gov/breathtest/docs/webdms/Studies_Articles/Widmarks%20Equation%2009-16-1996.pdf
        // C =  0.8 A z / (W r) - b dt
        double b = 0.00017; //kg/L/hr
        double dt = (double)(System.currentTimeMillis() - millis)/(1000*3600);
        double alc = 0.6 * bacNumDrinks; //fluid ounces of alcohol

        double C = Math.max(0.8 * alc / (weight * 16 * r) - b * dt, 0);
        if (C == 0) {
//            bacNumDrinks = 0;
            // update baccalcindex on server
//            String formatString = "type=add&baccalcindex=%s";
//            String params = String.format(formatString, num_drinks);
//            new PutAsyncTask().execute(getResources().getString(R.string.server_currsession) +
//                    AccessToken.getCurrentAccessToken().getUserId(), params);
        }
        return C*100;
    }
}
