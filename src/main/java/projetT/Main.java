package projetT;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.jdbc.Driver;
import fr.imie.chat.specification.WebSocketServer;
import fr.imie.chat.specification.exceptions.SessionNotFoundException;
import fr.imie.chat.specification.listeners.CloseWebSocketListener;
import fr.imie.chat.specification.listeners.MessageWebSocketListener;
import fr.imie.chat.specification.listeners.OpenWebSocketListener;
import javax.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main{
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static void main(String [] args) throws IOException, SQLException, DeploymentException {
        new Main();
    }

    private Main() throws IOException, SQLException, DeploymentException {

        //DriverManager.registerDriver(new Driver());
        //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/projett?serverTimezone=UTC", "root", "");
        WebSocketServer<String> webSocketServer = WebSocketServer.get("localhost", 8083, String.class);
        webSocketServer.addListener((OpenWebSocketListener<String>) idSession -> System.out.println(" Ouvert sur " + idSession));

        webSocketServer.addListener((CloseWebSocketListener<String>) idSession -> System.out.println(" Fermer sur " + idSession));

        webSocketServer.addListener((MessageWebSocketListener<String>) (idSession, message) -> {
            try {
                Action action = MAPPER.readValue(message, Action.class);

                if (action.getType().compareTo("singin") == 0) {
                    SignIn signIn = MAPPER.readValue(message, SignIn.class);
                    System.out.println(signIn.getUsername());
                    System.out.println(signIn.getPassword());
                    System.out.println(signIn.getEmail());

                }
                else if(action.getType().compareTo("connect") == 0){
                    LogIn logIn = MAPPER.readValue(message, LogIn.class);
                    System.out.println(logIn.getUsername());
                    System.out.println(logIn.getPassword());
                }
                else if(action.getType().compareTo("disconnect") == 0){
                    Disconnect disconnect = MAPPER.readValue(message, Disconnect.class);

                }
                webSocketServer.send(idSession, MAPPER.writeValueAsString(action));
            } catch (IOException | SessionNotFoundException e) {
                e.printStackTrace();
            }
        });
        webSocketServer.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(" Si tu veux arrêter le serveur appuie sur une touche ;) ");
        reader.readLine();
    }
}
