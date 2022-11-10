import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Counter {
    private Map<String, Integer> basket = new HashMap<>();
    private Integer[] values = new Integer[basket.size() + 1];
    private Map<String, String> tsv = new HashMap<>();

    private int max = -1;
    private String keyMax = "1";

    public Counter(Map<String, String> tsv, Map<String, Integer> basket) {
        this.tsv = tsv;
        this.basket = basket;
    }

    public Map<String, Integer> categoryCount(Object key, Integer sumClient) {
        //Сравнение есть ли в мапе и json данные продукты
        String category = tsv.get(key);
        if (category == null) {
            category = "другое";
        }
        boolean keyTrue = tsv.containsKey(key);
        boolean basketTrue = basket.containsKey(category);
        if (keyTrue) {
            if (basketTrue) {
                int sumPrevious = basket.get(category);
                int generalSum = sumPrevious + sumClient;
                basket.put(category, generalSum);
            } else {
                basket.put(category, sumClient);
            }
        } else {
            if (basketTrue) {
                int sumPrevious = basket.get(category);
                int generalSum = sumPrevious + sumClient;
                basket.put(category, generalSum);
            } else {
                basket.put(category, sumClient);
            }
        }
        return basket;
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
