package projetT;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        webSocketServer.addListener(new OpenWebSocketListener<String>() {
            @Override
            public void onOpen(String sessionId) {
                System.out.println("Open "+sessionId);
            }
        });

        webSocketServer.addListener(new CloseWebSocketListener<String>() {
            @Override
            public void onClose(String sessionId) {
                System.out.println("Close "+sessionId);
            }
        });
        webSocketServer.addListener(new MessageWebSocketListener<String>() {
            @Override
            public void onMessage(String sessionId, String message) {
                Connection connexion = ConnectDb.getConnection();

                try {
                    Action action = MAPPER.readValue(message, Action.class);

                    if (action.getType().compareTo("inscription") == 0) {
                        System.out.println(message + " de " + sessionId);
                        Inscription inscription = MAPPER.readValue(message, Inscription.class);
                        System.out.println(inscription.getUserName());
                        System.out.println(inscription.getUserEmail());
                        System.out.println(inscription.getUserPassword());

                        try {
                            Statement statement = connexion.createStatement();
                            int statut = statement.executeUpdate("INSERT INTO users (nom_user, email_user, mdp_user) VALUES ('" + inscription.getUserName() + "', '" + inscription.getUserEmail() + "','" + inscription.getUserPassword() + "');");
                            System.out.println("nouvel utilisateur ajouté");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    if (action.getType().compareTo("connexion") == 0) {
                        System.out.println(message + " de " + sessionId);
                        Connexion connexionUser = MAPPER.readValue(message, Connexion.class);


                        try {
                            Statement statement = connexion.createStatement();
                            ResultSet resultat = statement.executeQuery("SELECT * FROM users WHERE nom_user LIKE '+connexionUser.getUserName()'");

                            System.out.println("Utilisateur connecter");
                            System.out.println(connexionUser.getUserName());
                            System.out.println(connexionUser.getUserPassword());
                            System.out.println(resultat);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();


                }
            }
        });
        webSocketServer.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(" Si tu veux arrêter le serveur appuie sur une touche ;) ");
        reader.readLine();
    }
}
