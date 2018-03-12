package projetT;

import fr.imie.chat.specification.WebSocketServer;
import fr.imie.chat.specification.listeners.CloseWebSocketListener;
import fr.imie.chat.specification.listeners.MessageWebSocketListener;
import fr.imie.chat.specification.listeners.OpenWebSocketListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.sql.DriverManager.println;

public class Main{
public static void main(String [] args) throws IOException {
    new Main();

    }

    private Main() throws IOException {

        WebSocketServer<String> webSocketServer = WebSocketServer.get("localhost", 8080, String.class);
        webSocketServer.addListener(new OpenWebSocketListener<String>(){
            @Override
            public void onOpen(String idSession){
                println(" Ouvert sur " + idSession);
            }
        });

        webSocketServer.addListener(new CloseWebSocketListener<String>() {
            @Override
            public void onClose(String idSession) {
                println(" Fermer sur " + idSession);
            }
        });

        webSocketServer.addListener(new MessageWebSocketListener<String>() {
            @Override
            public void onMessage(String message, String idSession) {
                println(" Votre message: " + message + " de " + idSession);
            }
        });
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(" Si tu veux arrêter le serveur appuie sur une touche ;) ");
        reader.readLine();
    }
}
