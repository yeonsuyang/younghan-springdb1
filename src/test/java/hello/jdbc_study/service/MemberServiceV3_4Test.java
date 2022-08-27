package hello.jdbc_study.service;

import hello.jdbc_study.domain.Member;
import hello.jdbc_study.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션 - Datasource, transactionManager 자동 등록
 */
@Slf4j //컨트롤+쉬프트+R : 테스트 실행 //컨트롤+R : 마지막 실행했던 테스트 실행
@SpringBootTest //테스트를 돌릴 때 스프링이 스프링을 띄운다. 빈도 등록하고.. 의존관계 주입도 하고
class MemberServiceV3_4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired //의존관계주입
    private MemberRepositoryV3 repository;
    @Autowired
    private MemberServiceV3_3 service;

    @TestConfiguration //의존관계 주입하려면 다 빈등록해줘야됨
    static class TestConfig{

        private final DataSource dataSource;

        public TestConfig(DataSource datasource){
            this.dataSource = datasource;
        }
        @Bean
        MemberRepositoryV3 memberRepositoryV3(){
            return new MemberRepositoryV3(dataSource);
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3(){
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }




    /* //스프링컨테이너를 쓰고있지않다. @Transactional 어노테이션을 쓰려면, 스프링빈에 등록해서 사용해야한다.
    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);
        repository = new MemberRepositoryV3(dataSource);
        service = new MemberServiceV3_3(repository);
    }*/

    @Test
    void AopCheck() {
        log.info("member service class={}", service.getClass());
        log.info("memberRepository class={}",repository.getClass());
    }


    @Test
    @DisplayName("정상 이체")
    void accountTrasfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        repository.save(memberA);
        repository.save(memberB);

        //when
        service.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000);


        //then
        Member findMemberA = repository.findById(memberA.getMemberId());
        Member findMemberB = repository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTrasferEx() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_EX, 10000);
        repository.save(memberA);
        repository.save(memberB);

        //when
        log.info("Start TX"); //내부에서는 같은 커넥션을 사용
        assertThatThrownBy(() -> service.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000))
                .isInstanceOf(IllegalStateException.class);
        log.info("END TX");

        //then
        Member findMemberA = repository.findById(memberA.getMemberId());
        Member findMemberB = repository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }

    @AfterEach
    void after() throws SQLException {
        repository.delete(MEMBER_A);
        repository.delete(MEMBER_B);
        repository.delete(MEMBER_EX);
    }
}