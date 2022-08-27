package hello.jdbc_study.service;

import hello.jdbc_study.domain.Member;
import hello.jdbc_study.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor 
public class MemberServiceV3_1 {

    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    
    public void accountTransfer(String formId, String toId, int money) throws SQLException {

        //트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            //비즈니스로직
            bizLogic(toId, money, formId);
            transactionManager.commit(status);//성공시 커밋
        }catch (Exception e){
            transactionManager.rollback(status); //실패시 롤백
            throw new IllegalStateException(e);
        } //트랜잭션이 커밋되거나 롤백될 때 알아서 realease해줌
    }

                         //command + f6: 파라미터 순서 변경
    private void bizLogic(String toId, int money, String formId) throws SQLException {
        //비즈니스 로직
        Member fromMember = memberRepository.findById(formId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(formId,fromMember.getMoney() - money);

        validation(toMember);

        memberRepository.update(toId,toMember.getMoney() + money);
    }


    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외발생");
        }
    }
}
