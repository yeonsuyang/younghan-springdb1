package hello.jdbc_study.connection;

public abstract class ConnectionConst { //상수모음이기때문에 객체가 생성되면 안돼서 abstract 선언
    public static final String URL = "jdbc:h2:tcp://localhost/~/test"; //이건 규약. 틀리면 안됨
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
