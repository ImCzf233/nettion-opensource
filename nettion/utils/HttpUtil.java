package nettion.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setDoOutput(true);
            connection.setReadTimeout(981);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) { }
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line+"\n";
            }
        } catch (Exception e) {
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
            }
        }
        return result;
    }

    public static String get(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while((inputLine = in.readLine())!= null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();
        return response.toString();
    }
}
