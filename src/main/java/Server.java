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

        File file = new File("src/categories.tsv");
        File fileBin = new File("src/data.bin");
        String[] dateArray = new String[10];

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

                    Counter counter = new Counter(tsv, basket);
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println(jsonObject);

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
                        //while (true){
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

                        dateArray = new String[parts.length];
                        for (int j = 0; j < bin.length - 1; j++) {
                            for (char ch : bin[j].toCharArray()) {
                                if (Character.isLetter(ch)) {
                                    basket = counter.categoryCount(bin[j], Integer.valueOf(bin[j + 2]));
                                    if (j + 1 < bin.length - 1) {
                                        dateArray[j] = bin[j + 1];
                                    }
                                    break;
                                }
                            }
                            j += 2;
                        }
                    }

                    List<String> listDate = new ArrayList<>();
                    for (int j = 0; j < dateArray.length; j++) {
                        if (dateArray[j] != null) {
                            listDate.add(dateArray[j]);
                        }
                    }
                    counter = new Counter(tsv, basket);
                    String category = tsv.get(key);
                    if (category == null) {
                        category = "другое";
                    }
                    if (basket == null) {
                        basket.put(category, sumClient);
                        counter = new Counter(tsv, basket);
                    } else {
                        counter = new Counter(tsv, basket);
                        basket = counter.categoryCount(key, sumClient);
                    }

                    counter = new Counter(tsv, basket);
                    //Записываем ответ в виде json файла
                    Map object = new LinkedHashMap();
                    //JSONObject object = new JSONObject();
                    List<String> listCounter = counter.count();
                    String categoryMax = listCounter.get(1);
                    int maxSum = Integer.parseInt(listCounter.get(0));
                    object.put("maxCategory: { category: ", categoryMax);
                    object.put("sum", maxSum);
                    String jsonText = JSONValue.toJSONString(object);
                    File fileOut = new File("category.json");
                    try (
                            FileWriter files = new FileWriter(fileOut)) {
                        files.write(jsonText.toString());
                        files.flush();
                        out.println(fileOut);
                    }


                } catch (IOException e) {
                    System.out.println("Не могу стартовать сервер");
                    e.printStackTrace();
                }
            }
        }
    }
}