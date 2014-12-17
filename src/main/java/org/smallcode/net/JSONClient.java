package org.smallcode.net;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.*;

/**
* @author Hikmat Dhamee
* @email me.hemant.available@gmail.com
*/
public class JSONClient {

    public static JSONObject fetch(String address) throws IOException, JSONException ,UnknownHostException,ConnectException {
        JSONObject jsonObject;

            URL url = new URL(address);
            System.out.println("URL:" + url);

            long start = System.nanoTime();

            URLConnection connection=url.openConnection();

            connection.setDoOutput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection
                    .getInputStream()));
            String str;
            StringBuffer sb = new StringBuffer();
            int i = 0;
            while ((str = br.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
                ++i;
            }
            br.close();
            System.out.println("time to fetch " + i + " records in ms:" + (System.nanoTime() - start) / 1000000);
            jsonObject = new JSONObject(sb.toString());


        return jsonObject;
    }
}

