package projetT;

public class Send extends Action {
    private String nom_user;
    private String contenue_message;
    private String datePubli_message;
    private String nom_channel;

    public String getUsername() {
        return nom_user;
    }

    public String getContenuemessage() {
        return contenue_message;
    }

    public String getDatePublimessage() {
        return datePubli_message;
    }

    public String getNomchanel() {
        return nom_channel;
    }
}
