package projetT;

public class Users {
    private String type;
    private String userName;
    private StringBuffer apiKey;
    private int userId;

    public void setType(String type) {
        this.type = type;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setApiKey(StringBuffer apiKey) {
        this.apiKey = apiKey;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {

        return type;
    }

    public String getUserName() {
        return userName;
    }

    public StringBuffer getApiKey() {
        return apiKey;
    }

    public int getUserId() {
        return userId;
    }


}
