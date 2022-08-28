package study.datajpa.repository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.PersistenceUnitUtil;
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
    TeamRepository teamRepository;

    @PersistenceContext
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

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC,"username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements :"  + totalElements);

        //페이지에서 불러오는 사이즈 검증
        assertThat(content.size()).isEqualTo(3);
        //totalCount 체크
        assertThat(page.getTotalElements()).isEqualTo(5);
        //페이지 넘버도 불러올 수 있다
        assertThat(page.getNumber()).isEqualTo(0);
        //총 페이지 수
        assertThat(page.getTotalPages()).isEqualTo(2);
        //첫번째 페이지 인지
        assertThat(page.isFirst()).isTrue();
        //다음 페이지가 있는지
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        // 벌크 연산 이후에 영속성 컨텍스트에 있는 값을 확이한게 되면
        // 데이터 값이 맞지 않는다 그러므로 벌크 연산이후에는 꼭 영속성 컨텍스트를 초기화해줘야한다.
        em.flush();
        em.clear();

        List<Member> memberList = memberRepository.findByUsername("member5");
        Member findMember = memberList.get(0);
        System.out.println("findMember = " + findMember);

        //then
        assertThat(resultCount).isEqualTo(3);

    }

    @Test
    public void findMemberLazy() throws Exception {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 10, teamB));

        em.flush();
        em.clear();

        //when
        //jpql 페치 조인이기 때문에 Team 로딩 되어 있슴
        List<Member> members = memberRepository.findMemberfetchJoin();

        //일반 멤버 지연로딩 멤버변수인 Team은 Member Entity의 fetchType이 Lazy이기 때문에 지연로딩된다.
        //List<Member> members = memberRepository.findAll();
        //then
        for (Member member : members) {
            //System.out.println("member.getTeam().getName() = " + member.getTeam().getName());

            //지연로딩 여부 확인하기
            //1. Hibernate 기능으로 확인
            boolean teamIsInit = Hibernate.isInitialized(member.getTeam());
            System.out.println("teamIsInit = " + teamIsInit);

            //2. JPA 표준 방법으로 확인
            PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            boolean teamIsLoaded= util.isLoaded(member.getTeam());
            System.out.println("teamIsLoaded = " + teamIsLoaded);
        }

    }

    //쿼리 힌트 사용확인
    @Test
    public void queryHint() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member member1 = memberRepository.findReadOnlyByUsername("member1");
        member1.setUsername("member2");
        em.flush(); //Update Query 실행 X
    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }

}