package http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class ServerThread implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(Socket sock) {
        this.client = sock;

        try {
            //inicijalizacija ulaznog sistema
            in = new BufferedReader(
                    new InputStreamReader(
                            client.getInputStream()));

            //inicijalizacija izlaznog sistema
            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    client.getOutputStream())), true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            String komanda = in.readLine();
            String response = "";

            response = napraviOdogovor();

            do {
                komanda = in.readLine();
            } while (!komanda.trim().equals(""));


            //treba odgovoriti browser-u po http protokolu:
            out.println(response);

            in.close();
            out.close();
            client.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String napraviOdogovor() throws IOException {

        String retVal = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n";
        retVal += "<html><head><title>Odgovor servera</title></head>\n";
        retVal += "<body><h1>" + getQOTD() +  "</h1 >\n ";
        retVal += "</body></html>";

        return retVal;
    }

    private String getQOTD() throws IOException {

        String sURL = "http://quotes.rest/qod.json";
        URL url = new URL(sURL);
        URLConnection request = url.openConnection();
        request.connect();

        JsonParser jsonParser = new JsonParser();
        InputStreamReader json = new InputStreamReader((InputStream) request.getContent());
        JsonObject quote = jsonParser.parse(json)
                .getAsJsonObject().get("contents")
                .getAsJsonObject().getAsJsonArray("quotes").get(0)
                .getAsJsonObject();
        return "QOTD: " + quote.get("quote").getAsString() + " Author: " + quote.get("author").getAsString();

    }


}
