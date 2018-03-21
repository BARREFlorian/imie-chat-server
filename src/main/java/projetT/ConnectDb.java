package projetT;

    import com.mysql.cj.jdbc.Driver;
    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;

public class ConnectDb     {
    private static final ConnectDb instance = new ConnectDb();
    private static Connection connection;

    private ConnectDb()
    {
        try
        {
            String url = "jdbc:mysql://localhost:3306/projett?serverTimezone=UTC";
            String utilisateur = "root";
            String motDePasse = "";
            DriverManager.registerDriver(new Driver());
            connection = DriverManager.getConnection(url, utilisateur , motDePasse);
            System.out.println("Connexion à la base de données : ok");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static Connection getConnection()
    {
        return connection;
    }
}