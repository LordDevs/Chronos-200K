import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * In-memory ring of recent Observatory Channel operations for the HUD panel.
 */
public class ObservatoryActivityLog {
    private static final ObservatoryActivityLog INSTANCE = new ObservatoryActivityLog(20);
    private final int capacity;
    private final Deque<JSONObject> recent = new ArrayDeque<>();

    public ObservatoryActivityLog(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    public static ObservatoryActivityLog getInstance() {
        return INSTANCE;
    }

    public synchronized void record(String type, String title, String summary) {
        JSONObject entry = new JSONObject();
        try {
            entry.put("type", type == null ? "unknown" : type);
            entry.put("title", title == null ? "" : title);
            entry.put("summary", summary == null ? "" : truncate(summary, 280));
            entry.put("at", System.currentTimeMillis());
        } catch (Exception e) {
            return;
        }
        recent.addFirst(entry);
        while (recent.size() > capacity) {
            recent.removeLast();
        }
    }

    public synchronized List<JSONObject> list() {
        return new ArrayList<>(recent);
    }

    public synchronized JSONArray toJsonArray() {
        JSONArray arr = new JSONArray();
        for (JSONObject o : recent) {
            arr.put(o);
        }
        return arr;
    }

    /** Test helper. */
    public synchronized void clear() {
        recent.clear();
    }

    private static String truncate(String s, int max) {
        String plain = s.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
        if (plain.length() <= max) {
            return plain;
        }
        return plain.substring(0, max - 1) + "…";
    }
}
