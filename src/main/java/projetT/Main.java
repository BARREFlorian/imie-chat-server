package projetT;

import com.mysql.cj.jdbc.Driver;
import fr.imie.chat.specification.WebSocketServer;
import fr.imie.chat.specification.listeners.CloseWebSocketListener;
import fr.imie.chat.specification.listeners.MessageWebSocketListener;
import fr.imie.chat.specification.listeners.OpenWebSocketListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.sql.DriverManager.println;

public class Main{
public static void main(String [] args) throws IOException, SQLException {
        new Main();
    }

    private Main() throws IOException, SQLException {

        DriverManager.registerDriver(new Driver());
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/projett?serverTimezone=UTC", "root", "");
        WebSocketServer<String> webSocketServer = WebSocketServer.get("localhost", 8080, String.class);
        webSocketServer.addListener((OpenWebSocketListener<String>) idSession -> println(" Ouvert sur " + idSession));

        webSocketServer.addListener((CloseWebSocketListener<String>) idSession -> println(" Fermer sur " + idSession));

        webSocketServer.addListener((MessageWebSocketListener<String>) (message, idSession) -> println(message + " de " + idSession));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(" Si tu veux arrêter le serveur appuie sur une touche ;) ");
        reader.readLine();
    }
}
