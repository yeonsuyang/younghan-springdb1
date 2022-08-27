package hello.jdbc_study.service;

import hello.jdbc_study.domain.Member;
import hello.jdbc_study.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor //final만 가지고 생성자 생성
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    public void accountTransfer(String formId, String toId, int money) throws SQLException {
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
