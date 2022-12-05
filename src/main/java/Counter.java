import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Counter {
    private Map<String, Integer> basket = new HashMap<>();
    private Map<String, Integer> basketYear = new HashMap<>();
    private Map<String, Integer> basketMonth = new HashMap<>();
    private Map<String, Integer> basketDay = new HashMap<>();
    private Integer[] values = new Integer[basket.size() + 1];
    private Integer[] valuesYear = new Integer[basketYear.size() + 1];
    private Integer[] valuesMonth = new Integer[basketMonth.size() + 1];
    private Integer[] valuesDay = new Integer[basketDay.size() + 1];
    private Map<String, String> tsv = new HashMap<>();
    private String keyMax = "1";

    public Counter(Map<String, String> tsv, Map<String, Integer> basket, Map<String, Integer> basketYear, Map<String, Integer> basketMonth, Map<String, Integer> basketDay) {
        this.tsv = tsv;
        this.basket = basket;
        this.basketYear = basketYear;
        this.basketMonth = basketMonth;
        this.basketDay = basketDay;
    }
    public Counter(Map<String, String> tsv, Map<String, Integer> basket) {
        this.tsv = tsv;
        this.basket = basket;

    }

    public List<String> count() {
        int max = -1;
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

    public Map<String, Integer> categoryCount(String key, Integer sumClient) {
        String[] splitKey = key.split(" ");
        String category = tsv.get(splitKey[0]);
        if (category == null) {
            category = "другое";
        }
        boolean keyTrue = tsv.containsKey(splitKey[0]);
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

    public Map<String, Integer> countMaxYearCategory(String key, Integer sumClient, String date) {
        String[] lastDateSplit = date.split("-");
        String year = lastDateSplit[0];
        String[] splitKey = key.split(" ");
        String[] datePresent = splitKey[1].split("-");
        String category = tsv.get(splitKey[0]);
        if (category == null) {
            category = "другое";
        }
        boolean keyTrue = tsv.containsKey(splitKey[0]);
        boolean basketTrue = basketYear.containsKey(category);
        if (keyTrue && basketTrue) {
            if (year.equals(datePresent[0])) {
                int sumPrevious = basketYear.get(category);
                int generalSum = sumPrevious + sumClient;
                basketYear.put(category, generalSum);
            } else {
                if (year.equals(datePresent[0])) {
                    basketYear.put(category, sumClient);
                }
            }
        } else if (!keyTrue && basketTrue) {
            if (year.equals(datePresent[0])) {
                int sumPrevious = basketYear.get(category);
                int generalSum = sumPrevious + sumClient;
                basketYear.put(category, generalSum);
            }
        } else {
            if (year.equals(datePresent[0])) {
                basketYear.put(category, sumClient);
            }
        }
        return basketYear;
    }

    public Map<String, Integer> countMaxMonthCategory(String key, Integer sumClient, String date) {
        List<String> list = new ArrayList<>();
        String[] lastDateSplit = date.split("-");
        String year = lastDateSplit[0];
        String month = lastDateSplit[1];
        String[] splitKey = key.split(" ");
        String[] datePresent = splitKey[1].split("-");
        String category = tsv.get(splitKey[0]);
        if (category == null) {
            category = "другое";
        }
        boolean keyTrue = tsv.containsKey(splitKey[0]);
        boolean basketTrue = basketMonth.containsKey(category);
        if (keyTrue && basketTrue) {
            if (month.equals(datePresent[1]) && year.equals(datePresent[0])) {
                int sumPrevious = basketMonth.get(category);
                int generalSum = sumPrevious + sumClient;
                basketMonth.put(category, generalSum);
            } else {
                if (month.equals(datePresent[1]) && year.equals(datePresent[0])) {
                    basketMonth.put(category, sumClient);
                }
            }
        } else if (!keyTrue && basketTrue) {
            if (month.equals(datePresent[1]) && year.equals(datePresent[0])) {
                int sumPrevious = basketMonth.get(category);
                int generalSum = sumPrevious + sumClient;
                basketMonth.put(category, generalSum);
            }
        } else {
            if (month.equals(datePresent[1]) && year.equals(datePresent[0])) {
                basketMonth.put(category, sumClient);
            }
        }
        return basketMonth;
    }

    public Map<String, Integer> countMaxDayCategory(String key, Integer sumClient, String date) {
        List<String> list = new ArrayList<>();
        String[] lastDateSplit = date.split("-");
        String year = lastDateSplit[0];
        String month = lastDateSplit[1];
        String day = lastDateSplit[2];
        String[] splitKey = key.split(" ");
        String[] datePresent = splitKey[1].split("-");
        String category = tsv.get(splitKey[0]);
        if (category == null) {
            category = "другое";
        }
        boolean keyTrue = tsv.containsKey(splitKey[0]);
        boolean basketTrue = basketDay.containsKey(category);
        if (keyTrue && basketTrue) {
            if (day.equals(datePresent[2]) && month.equals(datePresent[1]) && year.equals(datePresent[0])) {
                int sumPrevious = basketDay.get(category);
                int generalSum = sumPrevious + sumClient;
                basketDay.put(category, generalSum);
            } else {
                if (day.equals(datePresent[2]) && month.equals(datePresent[1]) && year.equals(datePresent[0])) {
                    basketDay.put(category, sumClient);
                }
            }
        } else if (!keyTrue && basketTrue) {
            if (day.equals(datePresent[2]) && month.equals(datePresent[1]) && year.equals(datePresent[0])) {
                int sumPrevious = basketDay.get(category);
                int generalSum = sumPrevious + sumClient;
                basketDay.put(category, generalSum);
            }
        } else {
            if (day.equals(datePresent[2]) && month.equals(datePresent[1]) && year.equals(datePresent[0])) {
                basketDay.put(category, sumClient);
            }
        }
        return basketDay;
    }


    public List<String> countYear() {
        int max = -1;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < valuesYear.length; i++) {
            valuesYear = basketYear.values().toArray(new Integer[i]);
            if (valuesYear[i] > max) {
                max = valuesYear[i];
                for (Map.Entry<String, Integer> entry : basketYear.entrySet()) {
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

    public List<String> countMonth() {
        int max = -1;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < valuesMonth.length; i++) {
            valuesMonth = basketMonth.values().toArray(new Integer[i]);
            if (valuesMonth[i] > max) {
                max = valuesMonth[i];
                for (Map.Entry<String, Integer> entry : basketMonth.entrySet()) {
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

    public List<String> countDay() {
        int max = -1;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < valuesDay.length; i++) {
            valuesDay = basketDay.values().toArray(new Integer[i]);
            if (valuesDay[i] > max) {
                max = valuesDay[i];
                for (Map.Entry<String, Integer> entry : basketDay.entrySet()) {
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
