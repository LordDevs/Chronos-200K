import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.Scanner;

public class WeatherData {

    private static final Logger LOGGER = Logger.getLogger(WeatherData.class.getName());

    private static final String apiKey = initializeApiKey();

    private static String loadApiKey() {
        String envKey = System.getenv("API_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            return envKey;
        }

        try (Scanner scanner = new Scanner(new File("API_KEY"))) {
            if (scanner.hasNextLine()) {
                return scanner.nextLine().trim();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load API_KEY from file.", e);
        }
        return null;
    }

    private static String initializeApiKey() {
        String key = loadApiKey();
        if (key == null || key.isBlank()) {
            String message = "API_KEY not found. Configure the API_KEY environment variable or provide an API_KEY file.";
            LOGGER.severe(message);
            throw new IllegalStateException(message);
        }
        return key;
    }
    public String city;
    public double temperature;
    public Date date;
    private JSONObject apiData;
    
    
    public WeatherData(){
        this.date = new Date();
    }
    
    public WeatherData(String city){
        this.city = city.trim().toLowerCase();
        this.date = new Date();
    }
    
    public WeatherData(String city, Date date){
        this.city = city;
        this.date = date;
    }
    
    public void setCity(String city){this.city = city.toLowerCase();}

    public void setDate(String dateString) throws Exception{
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
        this.setDate(date);
    }
    public void setDate(Date date)  {this.date = date;}

    public double getTemperature(){
        try{
            if(this.apiData == null){
                this.apiData = this.getAPIdata();
            }
            return this.apiData.getJSONObject("main").getDouble("temp");
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Failed to obtain temperature from API data.", e);
            return 0;
        }
    }

    public String getDescription(){
        try{
            if(this.apiData == null){
                this.apiData = this.getAPIdata();
            }
            return this.apiData.getJSONArray("weather").getJSONObject(0).getString("description");
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Failed to obtain weather description from API data.", e);
            return null;
        }
    }

    public String genCloth(){
        // Ensure API data is fetched
        if (this.apiData == null) {
            this.apiData = this.getAPIdata();
        }

       
    
        if (this.apiData == null ||
            !this.apiData.has("main") ||
            !this.apiData.has("weather")) {
            LOGGER.warning("Missing fields or apiData is null.");
            return String.format("City not found: %s", this.city);
        }

        double temperature = this.getTemperature();
        String description = this.getDescription();

        if(temperature > 20){
            return String.format("%.2f°C. Warm day, %s.<br>You should wear shorts 🩳 and T-Shirts 👕",temperature, description);
        }else if(temperature < 20 && temperature > 10){
            return String.format("%.2f°C. Mild day, %s.<br>You should consider bringing a coat 🧥",temperature, description);
        }else {
            return String.format("%.2f°C. Cold day, %s.<br>You should <strong>definitely</strong> bring a coat🧥", temperature, description);
        }
    }


    public JSONObject getAPIdata(){
        try {
            if (this.city == null || this.city.isEmpty()) {
                LOGGER.warning("City is null or empty.");
                return null;
            }
    
            
            String normalized = this.city.trim().toLowerCase();
            String encodedCity = URLEncoder.encode(normalized, StandardCharsets.UTF_8);
    
            // Additional logging for debugging requests
            LOGGER.info(() -> "Requested city: " + this.city);
            LOGGER.info(() -> "Encoded city parameter: " + encodedCity);
    
            HttpClient client = HttpClient.newHttpClient();
            String url = String.format(
                "https://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric&APPID=%s",
                encodedCity, WeatherData.apiKey
            );
    
            LOGGER.info(() -> "Final request URL: " + url);
    
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                LOGGER.info(() -> "API response body: " + response.body());

            
            /// Analisa a resposta JSON
        JSONObject jsonObject = new JSONObject(response.body());

           // Verifica erro 404 (cidade não encontrada)
        if (jsonObject.has("cod") && jsonObject.get("cod").toString().equals("404")) {
           // System.out.println("❌ Cidade não encontrada na API.");
            return null;
        }

        JSONArray forecastList = jsonObject.getJSONArray("list");
        JSONObject result = null;
        long closestTimeDifference = Long.MAX_VALUE;

       // System.out.println("🔄 Procurando previsão mais próxima para: " + this.date);

        for (int i = 0; i < forecastList.length(); i++) {
            JSONObject iObj = forecastList.getJSONObject(i);
            long timestamp = iObj.getLong("dt") * 1000L;
            Date forecastDate = new Date(timestamp);


          //  System.out.println("⏰ Previsão disponível: " + forecastDate);

            long timeDifference = Math.abs(forecastDate.getTime() - this.date.getTime());

            if (timeDifference < closestTimeDifference) {
                closestTimeDifference = timeDifference;
                result = iObj;
            }
        }

        if (result == null) {
            LOGGER.warning("No nearby forecast was found.");
        } else {
            LOGGER.info(() -> "Selected forecast timestamp: " + result.getString("dt_txt"));
        }

        return result;
    } catch(Exception e){
        LOGGER.log(Level.SEVERE, "Failed to fetch API data.", e);
        return null;
    }
  }
}
