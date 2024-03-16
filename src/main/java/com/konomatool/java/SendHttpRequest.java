package com.konomatool.java;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SendHttpRequest {
    public static int sendRequest(String type, String urlString, String bodyFilePath, String body) throws Exception {

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(type);
        connection.setRequestProperty("Content-Type", "application/javascript");
        connection.setDoOutput(true);

        if (bodyFilePath != null) {
            // Read body from JavaScript file
            /*FileInputStream inputStream = new FileInputStream(bodyFilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder bodyBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                bodyBuilder.append(line);
            }
            String requestBody = bodyBuilder.toString();*/
            File file = new File(bodyFilePath);
            String requestBody = FileUtils.readFileToString(file, "UTF-8");
            // Write body to the connection
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }
            // Close resources
            /*reader.close();
            inputStream.close();*/
        }
        if (body != null) {
            // Write body content to the connection
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }

        // Get response
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        // Print response
        System.out.println("Response: " + response.toString());
        connection.disconnect();
        return responseCode;
    }
}
