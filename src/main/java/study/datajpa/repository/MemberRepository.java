package study.datajpa.repository;

import jakarta.persistence.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    Member findByUsername(String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username")String username, @Param("age")int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto (m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDtoList();

    @Query("select m from Member m where m.username in :names")
    List<Member> findBynNames(@Param("names") List<String> names);


    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //Optional

    Page<Member> findByAge(int age, Pageable pageable);
    //Slice<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) //excuteupdate임
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m join fetch m.team t")
    List<Member> findMemberFetchJoin();

    @EntityGraph(attributePaths = {"team"})
    @Override
    List<Member> findAll();
}
