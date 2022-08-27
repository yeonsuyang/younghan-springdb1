package hello.jdbc_study.repository;

import hello.jdbc_study.domain.Member;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 * DataSourceUtils.getConnection() : 커넥션 획득
 * DataSourceUtils.releaseConnection() : 커넥션 닫을 때
 * DataSourceUtils -> 트랜잭션 동기화 매니저 접근해서 획득하고 닫는 코드
 */

@Slf4j
public class MemberRepositoryV3 {


    private final DataSource datasource;

    public MemberRepositoryV3(DataSource datasource) {
        this.datasource = datasource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null; //얘로 데이터베이스에 쿼리를 날리는 것

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            int count = pstmt.executeUpdate(); //쿼리가 실행됨,영향받은 DB row 수를 반환
            return member;
        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
             /*
            pstmt.close(); //Exception이 터지면 아래가 호출안되는 문제가 발생해서. 아래메서드 생성
              con.close(); */
            close(con, pstmt, null);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {

            close(con, pstmt, null);
        }

    }


    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
       // JdbcUtils.closeConnection(con);
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con,datasource);
    }

    private Connection getConnection() throws SQLException {

        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        //스프링프레임웤이 제공
        Connection con =DataSourceUtils.getConnection(datasource);
        log.info("get connection={}, class={}",con,con.getClass());
        return con;
    }



    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            rs = pstmt.executeQuery();
            if(rs.next()) { //한번은 호출해줘야 데이터가 있는 것 부터 시작
                    Member member = new Member();
                    member.setMemberId(rs.getString("member_id"));
                    member.setMoney(rs.getInt("money"));
                    return member;
            }else{
                throw new NoSuchElementException("member not found memberId = "+memberId);
            }

        }catch (SQLException e){
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,rs);
        }
    }

}
