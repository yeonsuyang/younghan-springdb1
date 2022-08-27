package hello.jdbc_study.service;

import hello.jdbc_study.domain.Member;
import hello.jdbc_study.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

    //private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }


    
    public void accountTransfer(String formId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult((status -> { //해당 람다가 정상수행이면 커밋, 에러나면 롤백
            try {
                bizLogic(toId, money, formId);
            } catch (SQLException e) {
                throw new IllegalStateException(e); //checked예외를 런타임 예외로 바꿔서 던져줘야한다.
            }
        }));
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
