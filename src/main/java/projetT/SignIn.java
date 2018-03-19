package projetT;

public class SignIn extends Action {
    private String nom_user;
    private String mdp_user;
    private String email_user;

    public String getUsername() {
        return nom_user;
    }

    public String getPassword() {
        return mdp_user;
    }

    public String getEmail()  {
        return email_user;
    }
}
