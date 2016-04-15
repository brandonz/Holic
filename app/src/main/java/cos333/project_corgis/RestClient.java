package cos333.project_corgis;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
// import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RestClient {
   public static String Get(String urlString) {
       // String dataUrlParameters = "email="+"brandonz@princeton.edu"+"&name="+"Brandon";
       URL url;
       HttpURLConnection con = null;
       try {

           url = new URL(urlString);
           con = (HttpURLConnection) url.openConnection();
           con.setRequestMethod("GET");

           //add request header (crashes without this, shrug)
           con.setRequestProperty("User-Agent", "Android");

           // Debugging
           int responseCode = con.getResponseCode();
           System.out.println("\nSending 'GET' request to URL : " + url);
           System.out.println("Response Code : " + responseCode);

           BufferedReader in = new BufferedReader(
                   new InputStreamReader(con.getInputStream()));
           String inputLine;
           StringBuffer response = new StringBuffer();

           while ((inputLine = in.readLine()) != null) {
               response.append(inputLine);
           }
           in.close();
           String responseStr = response.toString();
           Log.d("Server response", responseStr);
           System.out.println(responseStr);
           return responseStr;
       } catch (Exception e) {

           e.printStackTrace();

       } finally {

           if (con != null) {
               con.disconnect();
           }
       }
       return null;
   }

    public static String Post(String urlString, String urlParams) {
        URL url;
        HttpURLConnection con = null;
        try {
            url = new URL(urlString);
            con = (HttpsURLConnection) url.openConnection();

            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Android");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParams);
            wr.flush();
            wr.close();

            // code to read the response. Should be the "User created!" message.
            // Leaving it here if we need the response later; currently does nothing after return.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String str_response = response.toString();
            return str_response;

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (con != null) {
                con.disconnect();
            }
        }
        return null;
    }

    public static String Put(String urlString, String urlParams) {
        URL url;
        HttpURLConnection con = null;
        try {
            url = new URL(urlString);
            con = (HttpsURLConnection) url.openConnection();

            //add request header
            con.setRequestMethod("PUT");
            con.setRequestProperty("User-Agent", "Android");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // Send put request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParams);
            wr.flush();
            wr.close();

            // code to read the response.
            // Leaving it here if we need the response later; currently does nothing after return.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String str_response = response.toString();
            return str_response;

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (con != null) {
                con.disconnect();
            }
        }
        return null;

    }
}
