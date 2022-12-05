import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {
    private static final int PORT = 8989;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Map<String, String> tsv = new HashMap<>();
        Map<String, Integer> basket = new HashMap<>();
        Map<String, Integer> basketYear = new HashMap<>();
        Map<String, Integer> basketMonth = new HashMap<>();
        Map<String, Integer> basketDay = new HashMap<>();
        Map object = new LinkedHashMap();
        List<String> listDate = new ArrayList<>();
        File file = new File("src/categories.tsv");
        File fileBin = new File("src/data.bin");

        BufferedReader TSVFile = null;
        try {
            TSVFile = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String dataRow = null; // Read first line.
        try {
            dataRow = TSVFile.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (dataRow != null) {
            String[] parts = dataRow.split("\t");
            tsv.put(parts[0], parts[1]);
            try {
                dataRow = TSVFile.readLine(); // Read next line of data.
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //Открываем серверный socket, используем try,catch, т.к. socket требует закрытия
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is started");
            //Запускаем вечный цикл
            while (true) {
                //Ожидаем подключения
                //После открытия серверный сокет откроет клиентский сокет
                try (Socket clientSocket = serverSocket.accept();
                     //Создаем поток вывода
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     //Создаем поток ввода
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    out.println("Server started");
                    String purchase = in.readLine();
                    JSONParser parser = new JSONParser();
                    Object obj = null;
                    try {
                        obj = parser.parse(purchase);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    Counter counter = new Counter(tsv, basket, basketYear, basketMonth, basketDay);
                    JSONObject jsonObject = (JSONObject) obj;

                    Object key = jsonObject.get("title");
                    Object sumFromClient = jsonObject.get("sum");
                    Object date = jsonObject.get("date");
                    int sumClient = Integer.parseInt((String) sumFromClient);
                    String lineSaveBin = key + " " + date + " " + sumClient;

                    ObjectOutputStream outBin = new ObjectOutputStream(new FileOutputStream(fileBin, true));
                    outBin.writeObject(lineSaveBin + " ");
                    outBin.close();

                    if (fileBin.exists()) {
                        InputStreamReader inBin = new InputStreamReader(new FileInputStream(fileBin));
                        BufferedReader bufferedReader = new BufferedReader(inBin);

                        String lineUploadBin = bufferedReader.readLine();
                        String[] parts = lineUploadBin.split(" ");
                        String[] bin = new String[parts.length];
                        for (int j = 0; j < bin.length; j++) {
                            bin[j] = "";
                        }
                        for (int i = 0; i < parts.length; i++) {
                            if (parts[i] == " ") {
                                continue;
                            }
                            for (char ch : parts[i].toCharArray()) {
                                if (Character.isLetterOrDigit(ch) && ch != 't' && ch != ' ' || ch == '-') {
                                    bin[i] += ch;
                                }
                            }
                        }

                        for (int j = 0; j < bin.length - 1; j++) {
                            for (char ch : bin[j].toCharArray()) {
                                if (Character.isLetter(ch)) {
                                    basket = counter.categoryCount((bin[j] + " " + bin[j + 1]), Integer.valueOf(bin[j + 2]));
                                    basketYear = counter.countMaxYearCategory((bin[j] + " " + bin[j + 1]), Integer.valueOf(bin[j + 2]), String.valueOf(date));
                                    basketMonth = counter.countMaxMonthCategory((bin[j] + " " + bin[j + 1]), Integer.valueOf(bin[j + 2]), String.valueOf(date));
                                    basketDay = counter.countMaxDayCategory((bin[j] + " " + bin[j + 1]), Integer.valueOf(bin[j + 2]), String.valueOf(date));
                                    break;
                                }
                            }
                            if (bin[j] != "") {
                                j += 2;
                            }
                        }
                    }
                    //Записываем ответ в виде json файла

                    List<String> listCounter = counter.count();
                    List<String> listCounterYear = counter.countYear();
                    List<String> listCounterMonth = counter.countMonth();
                    List<String> listCounterDay = counter.countDay();

                    String categoryMax = "";
                    int maxSum = 0;
                    categoryMax = listCounter.get(1);
                    maxSum = Integer.parseInt(listCounter.get(0));

                    String categoryMaxYear = listCounterYear.get(1);
                    int maxSumYear = Integer.parseInt(listCounterYear.get(0));
                    String categoryMaxMonth = listCounterMonth.get(1);
                    int maxSumMonth = Integer.parseInt(listCounterMonth.get(0));
                    String categoryMaxDay = listCounterDay.get(1);
                    int maxSumDay = Integer.parseInt(listCounterDay.get(0));

                    object.put("maxYearCategory: { category: ", categoryMax);
                    object.put("sum", maxSum);
                    object.put("maxYearCategory: { category: ", categoryMaxYear);
                    object.put("sumYear", maxSumYear);
                    object.put("maxMonthCategory: { category: ", categoryMaxMonth);
                    object.put("sumMonth", maxSumMonth);
                    object.put("maxDayCategory: { category: ", categoryMaxDay);
                    object.put("sumDay", maxSumDay);

                    basket.clear();
                    basketDay.clear();
                    basketMonth.clear();
                    basketYear.clear();

                    listCounterDay.removeAll(listCounterDay);
                    listCounterMonth.removeAll(listCounterMonth);
                    listCounterYear.removeAll(listCounterYear);

                    String jsonText = JSONValue.toJSONString(object);
                    out.println(jsonText);
                } catch (IOException e) {
                    System.out.println("Не могу стартовать сервер");
                    e.printStackTrace();
                }
            }
        }
    }
}