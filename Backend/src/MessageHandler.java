import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.alicebot.ab.*;

public class MessageHandler {
    private Chat chat;

    public MessageHandler(){
    }

    /// Initiate MessageHandler with Web Socket Session
    public MessageHandler(Chat chat){
        this.chat = chat;
    }

    public String processMessage(String message) {
        StringBuilder response;

        String botResponse = chat.multisentenceRespond(message);
        if (botResponse == null) {
            return "⚠️ I'm having trouble responding right now. Please try again later.";
        }

        // Simula como se o bot tivesse retornado "weather:..."
        if (message.toLowerCase().startsWith("weather:")) {
            botResponse = message;
        }

        if (botResponse.toLowerCase().startsWith("weather:")) {
            response = new StringBuilder("Forecast for<br>");

            String[] locationDayPairs = botResponse.substring(8).split(",");

            if (locationDayPairs.length > 5) {
                return "⚠️ Son, five cities is enough to predict the future. Do not rush the time.<br>";
            }

            for (String pair : locationDayPairs) {
                String[] parts = pair.split(":");
                if (parts.length != 2) {
                    response.append("⚠️ What is this? <strong>")
                            .append(pair)
                            .append("</strong>? That’s not how we talk weather here, champ. Use format: <em>city:day</em><br>");
                    continue;
                }

                String location = parts[0].trim();
                String day = parts[1].trim();

                // Converte "today" corretamente
                Date targetDate = convertDayToDate(day);

                // Validação de data passada
                Date today = new Date();
                if (targetDate.before(today)) {
                    response.append("⚠️ Son, you cannot predict the past. Try again.<br>");
                    continue;
                }

                // Capitaliza o nome da cidade para buscar corretamente
                String normalizedCity = location.substring(0, 1).toUpperCase() + location.substring(1).toLowerCase();
                WeatherData cityWeather = new WeatherData(normalizedCity, targetDate);

                // Formata a data como "Monday, April 7"
                SimpleDateFormat prettyFormat = new SimpleDateFormat("EEEE, MMMM d");
                String formattedDate = prettyFormat.format(targetDate);

                // Monta a resposta final
                response.append(String.format("%s on %s: ", normalizedCity, formattedDate));
                response.append(cityWeather.genCloth()).append("<br><br>");
            }

            return response.toString();
        }

        return botResponse;
    }

    // Convert day string (e.g., "Monday") to a Date relative to today (April 3, 2025)
    private Date convertDayToDate(String day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // Set to today: April 3, 2025
        cal.set(Calendar.HOUR_OF_DAY, 12); // Noon to avoid edge cases
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String todayDay = sdf.format(cal.getTime()).toLowerCase();
        String targetDay = day.trim().toLowerCase();

        int daysToAdd = 0;
        String[] daysOfWeek = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
        int todayIndex = Arrays.asList(daysOfWeek).indexOf(todayDay);
        int targetIndex = Arrays.asList(daysOfWeek).indexOf(targetDay);

        if (targetIndex < 0) {
            return cal.getTime(); // Invalid day, return today
        }

        daysToAdd = (targetIndex - todayIndex + 7) % 7; // Ensure positive difference within a week
        cal.add(Calendar.DAY_OF_YEAR, daysToAdd);

        return cal.getTime();
    }
}
