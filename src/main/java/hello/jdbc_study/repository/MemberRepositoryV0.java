package hello.jdbc_study.repository;

import hello.jdbc_study.connection.DBConnectionUtil;
import hello.jdbc_study.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */

@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null; //얘로 데이터베이스에 쿼리를 날리는 것


        //메서드로 빼기 : 옵션+커맨드+M
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

    //pstmt: 파라미터를 바인딩 할 수 있어, 기능이 더 많은 것, statement를 상속받음
    //stmt: sql을 그대로 넣는 것,
    private void close(Connection con, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close(); //SQLException
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConntection();
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
