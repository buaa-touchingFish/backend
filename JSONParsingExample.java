import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParsingExample {
    public static void main(String[] args) {
        String jsonString = "[\n" +
                "    {\n" +
                "        \"author\": {\n" +
                "            \"id\": \"A5046060925\",\n" +
                "            \"display_name\": \"Laura Mureşan\"\n" +
                "        },\n" +
                "        \"institutions\": [\n" +
                "            {\n" +
                "                \"id\": \"I3125347698\",\n" +
                "                \"display_name\": \"Babeș-Bolyai University\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    // ... (其他对象)\n" +
                "]";

        List<String> displayNames = extractDisplayNames(jsonString);
        System.out.println(displayNames);
    }

    private static List<String> extractDisplayNames(String jsonString) {
        List<String> displayNames = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            JSONObject author = obj.getJSONObject("author");
            String displayName = author.getString("display_name");
            displayNames.add(displayName);
        }

        return displayNames;
    }
}
