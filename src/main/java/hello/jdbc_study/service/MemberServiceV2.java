package hello.jdbc_study.service;

import hello.jdbc_study.domain.Member;
import hello.jdbc_study.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor 
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    
    public void accountTransfer(String formId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); //트랜잭션 시작
            bizLogic(con, toId, money, formId);

            con.commit(); //성공시 커밋
        }catch (Exception e){
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        }finally {
            release(con);
        }
    }

                         //command + f6: 파라미터 순서 변경
    private void bizLogic(Connection con, String toId, int money, String formId) throws SQLException {
        //비즈니스 로직
        Member fromMember = memberRepository.findById(con, formId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(formId,fromMember.getMoney() - money);

        validation(toMember);

        memberRepository.update(toId,toMember.getMoney() + money);
    }


    private void release(Connection con) {
        if(con != null){
            try{
                con.setAutoCommit(true);//자동커밋이 아닌데 커넥션을 반납하면 문제가 될 수 잇음.
                con.close(); 
            }catch (Exception e){
                log.info("error", e);
            }
        }
    }


    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외발생");
        }
    }


}
