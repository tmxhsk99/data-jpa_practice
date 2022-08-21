package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

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

}
