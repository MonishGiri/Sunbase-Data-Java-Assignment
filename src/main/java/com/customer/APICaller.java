package com.customer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APICaller {
	public static int callApi(String login_id, String password) {
		int apiResponseCode = 0;
		 try {
	            // API endpoint URL
	            URL url = new URL("https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp");
	            
	            // Open a connection
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            
	            // Set request method
	            connection.setRequestMethod("POST");
	            
	            // Enable input/output
	            connection.setDoOutput(true);
	            connection.setDoInput(true);
	            
	            // Set request headers
	            connection.setRequestProperty("Content-Type", "application/json");
	            
	            // Request body
	            String requestBody = "{\"login_id\":\""+login_id+"\",\"password\":\""+password+"\"}";
	            
	            // Write request body
	            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
	            outputStream.writeBytes(requestBody);
	            outputStream.flush();
	            outputStream.close();
	            
	            // Get response code
	            int responseCode = connection.getResponseCode();
	            System.out.println("Response Code: " + responseCode);
	            
	            // Read response
	            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	            
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();
	            
	            // Print response
	            System.out.println("Response: " + response.toString());
	            apiResponseCode = responseCode;
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		 return apiResponseCode;
	}
    public static void main(String[] args) {
       
    }
}

