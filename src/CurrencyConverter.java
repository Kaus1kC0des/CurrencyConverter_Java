import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class CurrencyConverter {
    private static final String API_KEY = "";
    private static final String API_URL = "https://open.er-api.com/v6/latest/";

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter the source currency code: ");
            String sourceCurrency = reader.readLine().toUpperCase();

            System.out.print("Enter the target currency code: ");
            String targetCurrency = reader.readLine().toUpperCase();

            System.out.print("Enter the amount to convert: ");
            double amount = Double.parseDouble(reader.readLine());

            double conversionRate = getConversionRate(sourceCurrency, targetCurrency);
            if (conversionRate != -1) {
                double convertedAmount = amount * conversionRate;
                System.out.printf("%.2f %s is equal to %.2f %s%n", amount, sourceCurrency, convertedAmount, targetCurrency);
            } else {
                System.out.println("Invalid currency codes.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double getConversionRate(String sourceCurrency, String targetCurrency) {
        try {
            URL url = new URL(API_URL + sourceCurrency + "?app_id=" + API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response to get conversion rate
                String jsonResponse = response.toString();
                double rate = parseJsonResponse(jsonResponse, targetCurrency);
                if (rate != -1) {
                    return rate;
                } else {
                    System.out.println("Invalid currency codes.");
                    return -1;
                }
            } else {
                System.out.println("Failed to fetch data from Open Exchange Rates API. Response Code: " + responseCode);
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static double parseJsonResponse(String jsonResponse, String targetCurrency) {
        try {
            // Parse JSON using Gson
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonObject rates = json.getAsJsonObject("rates");

            // Get the conversion rate for the target currency
            if (rates.has(targetCurrency)) {
                return rates.get(targetCurrency).getAsDouble();
            } else {
                System.out.println("Invalid target currency code.");
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    private static String getAllCurrencies(double conversionRate) {
        try {
            URL url = new URL(API_URL + "?app_id=" + API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response to get all available currencies
                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonObject rates = json.getAsJsonObject("rates");

                // Return a comma-separated list of currency codes
                return String.join(", ", rates.keySet());
            } else {
                System.out.println("Failed to fetch data from Open Exchange Rates API. Response Code: " + responseCode);
                return "N/A";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "N/A";
        }
    }
}