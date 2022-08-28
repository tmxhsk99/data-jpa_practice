package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
/*
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);
*/

    @Query("select m from Member m where m.username= :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //@Query : 단순히 값 하나를 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //@Query : DTO로 직접 조회
    @Query("select new study.datajpa.dto.MemberDto(m.id ,m.username, t.name)" +
            "from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); //컬렉션

    Member findMemberByUsername(String username); //단건

    Optional<Member> findOptionalByUsername(String username); //단건 Optional

    // 페이징과 정렬 사용 예제
    Page<Member> findByAge(int age, Pageable pageable);

    //스프링 데이터 JPA를 사용한 벌크성 수정 쿼리
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //JPQL 페치 조인
    @Query("select  m from Member m left join fetch m.team")
    List<Member> findMemberfetchJoin();

    //EntityGraph

    //공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select  m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 이름으로 쿼리에서 특히 관리가 편리하다
    @EntityGraph(attributePaths = {"team"})
    List<Member> findByUsername(String username);

    //NamedEntityGraph 사용
    @EntityGraph("Member.all")
    @Query("select m from Member m")
    List<Member> findMemberNamedEntityGraph();
    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly",value="true"))
    Member findReadOnlyByUsername(String username);

    @QueryHints(value = {@QueryHint(name = "orghibernate.readOnly", value = "true")}, forCounting = true)
    Page<Member> findByUsername(String name, Pageable pageable);
}
