package data.authentication;

public class Login {

    private String user;
    private String pwd;

    public Login() {}

    public Login(String user, String pwd) {
        super();
        this.user = user;
        this.pwd = pwd;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }


}
