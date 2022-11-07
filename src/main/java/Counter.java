import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Counter {
    private Map<String, Integer> basket = new HashMap<>();
    private Integer[] values = new Integer[basket.size() + 1];
    private int max = -1;
    private String keyMax = "1";

    public Counter(Map<String, Integer> basket) throws IOException {
        this.basket = basket;
    }

    public List<String> count() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            values = basket.values().toArray(new Integer[i]);
            if (values[i] > max) {
                max = values[i];
                for (Map.Entry<String, Integer> entry : basket.entrySet()) {
                    if (max == entry.getValue()) {
                        keyMax = entry.getKey();
                    }
                }
            }
        }
        list.add(String.valueOf(max));
        list.add(keyMax);
        return list;
    }

}
