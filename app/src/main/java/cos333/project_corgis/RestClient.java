package cos333.project_corgis;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestClient {
   public static String Get(String dataUrl) {
       // String dataUrlParameters = "email="+"brandonz@princeton.edu"+"&name="+"Brandon";
       URL url;
       HttpURLConnection connection = null;
       try {
           // Create connection
           url = new URL(dataUrl);
           connection = (HttpURLConnection) url.openConnection();
           connection.setRequestMethod("GET");
           connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//           connection.setRequestProperty("Content-Length","" + Integer.toString(dataUrlParameters.getBytes().length));
//           connection.setRequestProperty("Content-Language", "en-US");
           connection.setUseCaches(false);
           connection.setDoInput(true);
           connection.setDoOutput(true);
           // Send request
           DataOutputStream wr = new DataOutputStream(
                   connection.getOutputStream());
//           wr.writeBytes(dataUrlParameters);
           wr.flush();
           wr.close();
           // Get Response
           InputStream is = connection.getInputStream();
           BufferedReader rd = new BufferedReader(new InputStreamReader(is));
           String line;
           StringBuffer response = new StringBuffer();
           while ((line = rd.readLine()) != null) {
               response.append(line);
               response.append('\r');
           }
           rd.close();
           String responseStr = response.toString();
           Log.d("Server response", responseStr);
           return responseStr;
       } catch (Exception e) {

           e.printStackTrace();

       } finally {

           if (connection != null) {
               connection.disconnect();
           }
       }
       return null;
   }

    public static String Post(String dataUrl) {
//        String dataUrlParameters = "email="+"brandonz@princeton.edu"+"&name="+"Brandon";
        URL url;
        HttpURLConnection connection = null;
        try {
            // Create connection
            url = new URL(dataUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
//            connection.setRequestProperty("Content-Length","" + Integer.toString(dataUrlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
//            wr.writeBytes(dataUrlParameters);
            wr.flush();
            wr.close();
            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            String responseStr = response.toString();
            Log.d("Server response",responseStr);
            return responseStr;

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }
}
