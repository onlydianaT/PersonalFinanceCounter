import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static final int PORT = 8989;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Map<String, String> tsv = new HashMap<>();
        Map<String, Integer> basket = new HashMap<>();
        //Set<Map.Entry<String, Integer>> entrySet = basket.entrySet();
        File file = new File("src/categories.tsv");
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
                    String fileIn = in.readLine();

                    JSONParser parser = new JSONParser();
                    Object obj = null;
                    try {
                        obj = parser.parse(new FileReader(fileIn));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println(jsonObject);
                    Object key = jsonObject.get("title");
                    Object sumFromClient = jsonObject.get("sum");
                    Object date = jsonObject.get("date");
                    int sumClient = Integer.parseInt((String) sumFromClient);

                    //Записываем ответ в виде json файла
                    Map object = new LinkedHashMap();
                    //JSONObject object = new JSONObject();
                    File fileOut = new File("category.json");
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

                    Counter counter = new Counter(basket);
                    List<String> listCounter = counter.count();

                    String categoryMax = listCounter.get(1);
                    int maxSum = Integer.parseInt(listCounter.get(0));

                    object.put("maxCategory: { category: ", categoryMax);
                    object.put("sum", maxSum);

                    String jsonText = JSONValue.toJSONString(object);
                    System.out.println(jsonText);

                    try (
                            FileWriter files = new FileWriter(fileOut)) {
                        files.write(jsonText.toString());
                        files.flush();
                        out.println(fileOut);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}
