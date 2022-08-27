package hello.jdbc_study.service;

import hello.jdbc_study.domain.Member;
import hello.jdbc_study.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 트랜잭션 - @Transantional AOP
 */
@Slf4j
public class MemberServiceV3_3 {


    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3 (MemberRepositoryV3 memberRepository) {;
        this.memberRepository = memberRepository;
    }


    @Transactional //나는 이메서드 호출할 때, 트랜잭션 걸고 시작하겠다. 런타임예외가 발생하면 롤백
    public void accountTransfer(String formId, String toId, int money) throws SQLException {
      bizLogic(toId, money, formId);

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
