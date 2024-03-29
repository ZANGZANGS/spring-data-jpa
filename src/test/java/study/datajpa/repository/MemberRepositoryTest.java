package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test    public void testMember(){
        System.out.println("memberRepository =" +memberRepository.getClass());
        Member member = new Member("장장스");
        Member savedMember = memberRepository.save(member);

        em.flush();
        em.clear(); //영속성 컨텍스트 초기화
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
    }

    @Test
    @DisplayName("basic CRUD")
    public void badicCRUD(){
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);


        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("member!!!!");

        //리스트 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //삭제 검증
    }

    @Test
    @DisplayName("findByUsernameAndAgeGreaterThen")
    public void findByUsernameAndAgeGreaterThen(){
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        //then

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("@Query 테스트")
    public void Query어노테이션테스트(){
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        //when
        List<Member> result = memberRepository.findUser("AAA", 10);

        //then
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    @DisplayName("findUsernameList")
    public void findUsernameList(){
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        //when
        List<String> usernameList = memberRepository.findUsernameList();
        //then
        for (String s : usernameList) {
            System.out.println("s = "+ s);
        }
    }

    @Test
    public void findMemberDtoList(){
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member m1 = new Member("AAA", 10, teamA);
        Member m2 = new Member("BBB", 20, teamB);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<MemberDto> memberDto = memberRepository.findMemberDtoList();
        for (MemberDto dto : memberDto) {
            System.out.println("dto =" + dto);
        }

    }

    @Test
    public void inQuery(){
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> names = new ArrayList<>();
        names.add("AAA");
        names.add("BBB");
        names.add("CCC");

        List<Member> members = memberRepository.findBynNames(names);
        for (Member member : members) {
            System.out.println("member =" + member);
        }

        //when

        //then
    }

    @Test
    public void returnType(){
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findListByUsername("BBBA"); //컬렉션

        System.out.println("result = "+ result.size());
        System.out.println("result is empty =" + result.isEmpty()); // <- empty Collection을 보장한다. Good!!

        Member aaa = memberRepository.findMemberByUsername("AAA");
        Optional<Member> aaa1 = memberRepository.findOptionalByUsername("AAA");

        //when

        //then
    }

    @Test
    @DisplayName("paging")
    public void paging(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        em.flush();
        em.clear();

        int age = 10;
        int offset = 0;
        int limit = 3;

        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        //Slice<Member> byAgeSlice = memberRepository.findByAge(age, pageRequest);


        List<Member> members = page.getContent();
        long totalElements = page.getTotalElements();

        //then
        for (Member member : members) {
            System.out.println("member = "+ member);
        }

        System.out.println("totalElements = " + totalElements);

        assertThat(members.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    @DisplayName("bulk")
    @Rollback(value = false)
    public void 벌크업데이트(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));


        //when
        int bulkResultCount = memberRepository.bulkAgePlus(20);
        /**
         * 벌크연산은 영속성 컨텍스트를 거치지 않고 직접 DB에 트랜잭션을 날린다.
         * 따라서, 벌크 연산 이후에는 영속성 컨텍스트를 초기화 해줘야만 DB와 동일한 데이터로 맞춰진다.
         * @Modifying(clearAutomatically = true 옵션을 추가해도 동일하게 동작한다.
         */
        //em.clear();

        Member member5 = memberRepository.findByUsername("member5");
        System.out.println("member5 =" + member5);

        //then
        assertThat(bulkResultCount).isEqualTo(3);
    }

    @Test
    @DisplayName("findMemberLazy")
    public void findMemberLazy(){
        //given
        Team teamA = teamRepository.save(new Team("TeamA"));
        Team teamB = teamRepository.save(new Team("TeamB"));
        Member member1 = memberRepository.save(new Member("member1", 10, teamA));
        Member member2 = memberRepository.save(new Member("member2", 10, teamB));

        em.flush();
        em.clear();
        //when
        //List<Member> members = memberRepository.findAll();
        //List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("##### member = "+member.getUsername());
            System.out.println("##### member.teamClass = "+member.getTeam().getClass());
            System.out.println("##### member.team = "+member.getTeam().getName());
        }

        //then
    }
    
    @Test
    @DisplayName("queryHint")
    public void queryHint(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();
        //when
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2"); //read only 이므로 변경감지가 일어나지 않는다.

        em.flush();

        //then
    }

    @Test
    @DisplayName("lock")
    public void lock(){
        //given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        List<Member> members = memberRepository.findLockByUsername("member1");
        //when

        //then
    }
    @Test
    @DisplayName("커스텀_레포지터리")
    public void 커스텀_레포지터리(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();
        List<Member> memberCustom = memberRepository.findMemberCustom();
        //when

        //then
    }

    @Test
    @DisplayName("spec테스트")
    public void spec테스트(){
        //given
        Team teamA = teamRepository.save(new Team("teamA"));

        Member m1 = memberRepository.save(new Member("m1",10, teamA));
        Member m2 = memberRepository.save(new Member("m2",10, teamA));

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> all = memberRepository.findAll(spec);

        assertThat(all.size()).isEqualTo(1);


    }

    @Test
    @DisplayName("query by example")
    public void query_by_example(){
        //given
        Team teamA = teamRepository.save(new Team("teamA"));

        Member m1 = memberRepository.save(new Member("m1",10, teamA));
        Member m2 = memberRepository.save(new Member("m2",10, teamA));

        em.flush();
        em.clear();

        //when
        Member member = new Member("m1");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");
        Example<Member> memberExample = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(memberExample);
        assertThat(result.get(0).getUsername()).isEqualTo("m1");



    }

    @Test
    @DisplayName("projection")
    public void projection(){
        //given
        Team teamA = teamRepository.save(new Team("teamA"));

        Member m1 = memberRepository.save(new Member("m1",10, teamA));
        Member m2 = memberRepository.save(new Member("m2",10, teamA));

        em.flush();
        em.clear();

        //when
        //동작하지 않음;;
        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1", UsernameOnlyDto.class);

        for (UsernameOnlyDto usernameOnlyDto : result) {
            System.out.println( " ====> " +usernameOnlyDto.getUsername());
        }


       List<NestedClosedProjections> result2 = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections obj : result2) {
            System.out.println( " ====> " +obj.getUsername());
            System.out.println( " ====> " +obj.getTeam().getName());
        }

    }
    @Test
    @DisplayName("nativeQuery")
    public void nativeQuery() {
        //given
        Team teamA = teamRepository.save(new Team("teamA"));

        Member m1 = memberRepository.save(new Member("m1", 10, teamA));
        Member m2 = memberRepository.save(new Member("m2", 10, teamA));

        em.flush();
        em.clear();
        Member result = memberRepository.findByNativeQuery("m1");
        System.out.println("nativeQuery result =" + result.getUsername());

        Page<MemberProjection> nativeProjectionsQuery = memberRepository.findByNativeProjectionsQuery(PageRequest.of(0, 10));
        List<MemberProjection> content = nativeProjectionsQuery.getContent();
        System.out.println("byNativeProjectionsQuery ="+ content.get(0).getId());
        System.out.println("byNativeProjectionsQuery ="+ content.get(0).getUsername());
        System.out.println("byNativeProjectionsQuery ="+ content.get(0).getTeamName());
    }
}
