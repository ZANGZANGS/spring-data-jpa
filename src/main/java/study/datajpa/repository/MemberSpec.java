package study.datajpa.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;

public class MemberSpec {

    public static Specification<Member> teamName(final String teamName){
        return (root, query, builder)->{
            if(StringUtils.hasText(teamName)){
                return null;
            }
            Join<Object, Object> team = root.join("team", JoinType.INNER);//회원과 조인
            return builder.equal(team.get("name"), teamName);
        } ;
    }

    public static Specification<Member> username(final String username){
        return (root, query, builder)-> builder.equal(root.get("username"), username);
    }
}
