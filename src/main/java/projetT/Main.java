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
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main{
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    public static void main(String [] args) throws IOException, DeploymentException {
        new Main();
    }

    private Main() throws IOException, DeploymentException {

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

                    if (action.getType().compareTo("insc0ription") == 0) {
                        System.out.println(message + " de " + sessionId);
                        Inscription inscription = MAPPER.readValue(message, Inscription.class);
                        System.out.println(inscription.getUserName());
                        System.out.println(inscription.getUserEmail());
                        System.out.println(inscription.getUserPassword());

                        try {
                            Statement statement = connexion.createStatement();
                            int statut = statement.executeUpdate("INSERT INTO users (nom_user, email_user, mdp_user) VALUES ('" + inscription.getUserName() + "','" + inscription.getUserEmail() + "','" + inscription.getUserPassword() + "');");
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
                            ResultSet resultat = statement.executeQuery("SELECT * FROM users WHERE nom_user='" + connexionUser.getUserName() + "' AND mdp_user='" + connexionUser.getUserPassword() + "';");

                            while (resultat.next()){

                                String Utilisateur = resultat.getString("nom_user");
                                Integer id = resultat.getInt("id_user");

                                    System.out.println("Utilisateur connecter");
                                    System.out.println(connexionUser.getUserName());

                                    StringBuffer apiKey = null;

                                try {
                                    try {
                                        apiKey = RandomKeyGen.generate();
                                    } catch (NoSuchProviderException e) {
                                        e.printStackTrace();
                                    }
                                } catch (NoSuchAlgorithmException e) {
                                    System.out.println("Exception caught");
                                    e.printStackTrace();
                                }
                                System.out.println(apiKey);
                                Users users = new Users();
                                users.setType("user_connect");
                                users.setApiKey(apiKey);
                                users.setUserId(id);
                                users.setUserName(Utilisateur);

                                int result = statement.executeUpdate("UPDATE users SET apiKey='"+apiKey+"' WHERE nom_user='" + connexionUser.getUserName() + "' AND mdp_user='" + connexionUser.getUserPassword() + "';");

                                String retour =  MAPPER.writeValueAsString(users);;
                                try {
                                    webSocketServer.send(sessionId, retour);
                                } catch (SessionNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    if (action.getType().compareTo("deconnexion") == 0){
                        System.out.println(message + " de " + sessionId);
                        Deconnexion deconnexion = MAPPER.readValue(message, Deconnexion.class);

                        try{
                            Statement statement = connexion.createStatement();
                            int result = statement.executeUpdate("UPDATE users SET apiKey= DEFAULT WHERE apiKey='" + deconnexion.getApiKey() + "'");
                            System.out.println("Utilisateur deconnecter");
                        }catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    if (action.getType().compareTo("message") == 0) {
                        System.out.println(message + " de " + sessionId);
                        Message messagePV = MAPPER.readValue(message, Message.class);
                        try {
                            Statement statement = connexion.createStatement();
                            int statut = statement.executeUpdate("INSERT INTO message (contenue_message, datePubli_message, nom_user) VALUES ('" + messagePV.getMessageContenu() + "', '" + messagePV.getDatePubliMessage() + "','" + messagePV.getUserName() + "');");
                            System.out.println("message envoyer");
                            System.out.println(messagePV.getUserName());
                            System.out.println(messagePV.getMessageContenu());
                            System.out.println(messagePV.getDatePubliMessage());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (action.getType().compareTo("creer") == 0){
                        System.out.println(message + " de " + sessionId);
                        Creer creer = MAPPER.readValue(message, Creer.class);
                        try{
                            Statement statement = connexion.createStatement();
                            int statut = statement.executeUpdate("INSERT INTO channel (nom_channel, nom_user) VALUES ('" + creer.getChannelName() + "', '" + creer.getUserName() + "');");
                            statut = statement.executeUpdate("INSERT INTO createur_chanel (nom_channel, nom_user) VALUES ('" + creer.getChannelName() + "', '" + creer.getUserName() + "');");
                            System.out.println("channel créer!");
                            System.out.println(creer.getChannelName());
                            System.out.println(creer.getUserName());
                        }catch (SQLException e){
                            e.printStackTrace();
                        }
                    }
                    if (action.getType().compareTo("supprimer") == 0){
                        System.out.println(message + " de " + sessionId);
                        Supprimer supprimer = MAPPER.readValue(message, Supprimer.class);

                        try{
                            Statement statement = connexion.createStatement();
                            int result = statement.executeUpdate("UPDATE channel SET nom_channel= DEFAULT WHERE nom_channel='" + supprimer.getChannelName() + "'");
                            result = statement.executeUpdate("UPDATE channel SET nom_user= DEFAULT WHERE nom_user='" + supprimer.getUserName() + "'");
                            result = statement.executeUpdate("UPDATE createur_chanel SET nom_channel= DEFAULT WHERE nom_channel='" + supprimer.getChannelName() + "'");
                            result = statement.executeUpdate("UPDATE createur_chanel SET nom_user= DEFAULT WHERE nom_user='" + supprimer.getUserName() + "'");
                            System.out.println("Channel supprimé");
                        }catch (SQLException e) {
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
