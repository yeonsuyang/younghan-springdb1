package hello.jdbc_study.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection; //jdbc 표준 인터페이스가 제공하는 connection
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc_study.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConntection(){
        //F2누르면 오류난데로 이동해줌, command+p : 설명보기
        //option+엔터 : static import
        try {
            //옵션+커맨트+V : 변수 받아오는 것, introduce variable
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                        //객체정보랑, 객체의 클래스(타입)
            log.info("get connection={}, class={}",connection,connection.getClass());
            return connection;
        } catch (SQLException e) {
           throw new IllegalStateException(e); //런타임익셉션
        }
    }
}
