package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.query.Param;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;
    @Test
    public void testMember() {
        Member member = new Member("MemberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long delectCount = memberRepository.count();
        assertThat(delectCount).isEqualTo(0);
    }

    @Test
    public void FindUser() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> findUsers = memberRepository.findUser("AAA", 10);
        Member findUser = findUsers.get(0);
        assertThat(findUser).isEqualTo(m1);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member memberAA = new Member("memberAA", 10);
        Member memberBB = new Member("memberBB", 20);

        memberRepository.save(memberAA);
        memberRepository.save(memberBB);

        List<Member> memberList = memberRepository.findByUsernameAndAgeGreaterThan("memberBB", 15);
        assertThat(memberList.get(0).getUsername()).isEqualTo("memberBB");
        assertThat(memberList.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void findUsernameList(){
        Member memberAA = new Member("memberAA", 10);
        Member memberBB = new Member("memberBB", 20);

        memberRepository.save(memberAA);
        memberRepository.save(memberBB);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("usernameList = " + s);
        }
    }

    @Test
    public void findMemberDto(){
        Member memberAA = new Member("memberAA", 10);
        Member memberBB = new Member("memberBB", 20);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        memberAA.setTeam(teamA);
        memberBB.setTeam(teamB);

        em.persist(teamA);
        em.persist(teamB);
        em.persist(memberAA);
        em.persist(memberBB);

        em.flush();
        em.clear();

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("Member = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member memberAA = new Member("memberAA", 10);
        Member memberBB = new Member("memberBB", 20);

        memberRepository.save(memberAA);
        memberRepository.save(memberBB);
        List<Member> result = memberRepository.findByNames(Arrays.asList("memberAA", "MemberBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member memberAA = new Member("memberAA", 10);
        Member memberBB = new Member("memberBB", 20);

        memberRepository.save(memberAA);
        memberRepository.save(memberBB);

        List<Member> memberList = memberRepository.findListByUsername("memberAA");
        assertThat(memberList.get(0)).isEqualTo(memberAA);
        Member findMember = memberRepository.findMemberByUsername("memberBB");
        assertThat(findMember).isEqualTo(memberBB);
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("memberAA");
        assertThat(optionalMember.get()).isEqualTo(memberAA);

    }
}