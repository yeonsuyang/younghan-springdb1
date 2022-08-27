package hello.jdbc_study.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc_study.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {
    
    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={}, class={}",con1, con1.getClass());
        log.info("connection={}, class={}",con2, con2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {
       // DriverManagerDataSource datasource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        DataSource datasource = new DriverManagerDataSource(URL, USERNAME, PASSWORD); //위와 동일
        useDataSource(datasource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        //커넥션풀이 안찼을 때 connection을 요청하면 기다리게됨
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}",con1, con1.getClass());
        log.info("connection={}, class={}",con2, con2.getClass());
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        //커넥션 풀링
        //jdbc쓰면 자동으로 임포트 (implement datasource)
        HikariDataSource dataSource = new HikariDataSource(); //얘도 DataSource로 됨
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000);
    }
}
