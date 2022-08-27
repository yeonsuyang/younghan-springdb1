package hello.jdbc_study.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc_study.connection.ConnectionConst;
import hello.jdbc_study.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.internal.Urls;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc_study.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach //각 테스트가 실행되기 직전에 호출
    void beforeEach() {
        //기본 DriverManager - 항상 새로운 커넥션을 획득
       // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);

        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }



    @Test
    void crud() throws SQLException, InterruptedException {
        Member member = new Member("memberV3",10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}",findMember);
        log.info("member == findMember {}", member == findMember);
        log.info("member equals findMember {}", member.equals(findMember));
        assertThat(findMember).isEqualTo(member); //equals가 true인 이유: @Data

        Thread.sleep(1000);
    }

}