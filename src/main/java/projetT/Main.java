package projetT;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
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

                if (action.getType().compareTo("inscription") == 0) {
                    Inscription inscription = MAPPER.readValue(message, Inscription.class);
                    System.out.println(inscription.getUsername());
                    System.out.println(inscription.getPassword());
                    System.out.println(inscription.getEmail());

                }
                else if(action.getType().compareTo("connexion") == 0){
                    Connexion connexion = MAPPER.readValue(message, Connexion.class);
                    System.out.println(connexion.getUsername());
                    System.out.println(connexion.getPassword());
                }
                else if(action.getType().compareTo("deconnexion") == 0){
                    Deconnexion deconnexion = MAPPER.readValue(message, Deconnexion.class);

                }
                else if(action.getType().compareTo("creer") == 0){
                    Creer creer = MAPPER.readValue(message, Creer.class);
                    System.out.println(creer.getNomchanel());
                    System.out.println(creer.getUsername());
                }
                else if(action.getType().compareTo("supprimer") == 0 ){
                    Supprimer supprimer = MAPPER.readValue(message, Supprimer.class);
                    System.out.println(supprimer.getNomchanel());
                    System.out.println(supprimer.getUsername());
                }
                else if(action.getType().compareTo("envoyer") == 0){
                    Envoyer envoyer = MAPPER.readValue(message, Envoyer.class);
                    System.out.println(envoyer.getNomchanel());
                    System.out.println(envoyer.getUsername());
                    System.out.println(envoyer.getContenuemessage());
                    System.out.println(envoyer.getDatePublimessage());
                }
                webSocketServer.send(idSession, MAPPER.writeValueAsString(action));
            } catch (IOException | SessionNotFoundException e) {
                e.printStackTrace();
            }
        });
        webSocketServer.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(" Si tu veux arreter le serveur appuie sur une touche ;) ");
        reader.readLine();
    }
}
