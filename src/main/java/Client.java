import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class Client {
    private static final int PORT = 8989;
    private static final String HOST = null;

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat("2022-11-05");
        Date date = new Date();
        //Открываем клиентский socket, используем try,catch, т.к. socket требует закрытия
        try (Socket clientSocket = new Socket(HOST, PORT);
             //Создаем поток вывода, flush - очистка буфера
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             //Создаем поток ввода
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String smth = in.readLine();

            System.out.println("Input name of product and sum of your purchase");
            Scanner scanner = new Scanner(System.in);
            String[] parts = new String[2];
            String line = scanner.nextLine();
            parts = line.split(" ");
            //сохранение в json файл
            JSONObject obj = new JSONObject();
            File file = new File("purchase.json");
            obj.put("title", parts[0]);
            obj.put("date", formater.format(date));
            obj.put("sum", parts[1]);

            try (FileWriter files = new FileWriter(file)) {
                files.write(obj.toString());
                files.flush();
                out.println(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String fileIn = in.readLine();
            JSONParser parser = new JSONParser();
            try {
                Object object = parser.parse(new FileReader(fileIn));
                JSONObject jsonObject = (JSONObject) object;

                String jsonText = JSONValue.toJSONString(object);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                System.out.println(gson.toJson(object).toString());
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

