package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {
    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m ", Long.class).getSingleResult();
    }


    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }


    /**
     * useranme 이 같고 age 가 매개변수 나이 보다 많은 유저 리스트
     * @param useranme
     * @param age
     * @return
     */
    public List<Member> findByUsernameAndAgeGreaterThan(String useranme, int age) {
        List<Member> resultList = em.createQuery(
                        "select m from Member m where m.username = :username and m.age > :age", Member.class)
                        .setParameter("username", useranme)
                        .setParameter("age", age).getResultList();
        return resultList;
    }

}
