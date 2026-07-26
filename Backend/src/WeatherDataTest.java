import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WeatherDataTest {
    WeatherData cityData;
    
    @BeforeEach
    void setUp(){
        System.setProperty("API_KEY", "be74631d6c15530aad0e592d5c66b18e");
        cityData = new WeatherData();
    }
    
    @Test
    void testSetAndGetCity() {
        cityData.setCity("cork");
        assertEquals("cork", cityData.city);
    }
    
    @Test
    void testSetAndGetDate() throws Exception {
        String dateStr = "20/04/2025";
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
        cityData.setDate(date);
        assertEquals(date, cityData.date);
    }

    @Test
    void testGetTemperature() throws Exception {
        cityData.setCity("cork");
        double temperature = cityData.getTemperature(); // Call the method directly
        assertTrue(temperature > -100 && temperature < 100, "Temperature should be within a realistic range.");
    }

    @Test
    void testGenCloth() {
        cityData.setCity("cork");
        String clothingSuggestion = cityData.genCloth(); // Call the method directly
        assertNotNull(clothingSuggestion, "Clothing suggestion should not be null.");
        assertTrue(clothingSuggestion.contains("day"), cityData.getDescription());
    }

    @Test
    void testGetDescription() throws Exception{
        cityData.setCity("cork");
        String description = cityData.getDescription(); // Call the method directly
        assertNotNull(description, "Description should not be null.");
        assertFalse(description.isEmpty(), "Description should not be empty.");
    }

    @Test
    void testGetApiData() throws Exception{
        cityData.setCity("cork");
        assertNotNull(cityData.getAPIdata());
    }
}
