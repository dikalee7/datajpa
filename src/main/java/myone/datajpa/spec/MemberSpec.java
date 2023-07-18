package myone.datajpa.spec;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import myone.datajpa.entity.Member;
import myone.datajpa.entity.Team;

public class MemberSpec {
	public static Specification<Member> teamName(final String teamName) {
		return (Specification<Member>) (root, query, builder) -> {
			if (!StringUtils.hasText(teamName)) {
				return null;
			}
			Join<Member, Team> t = root.join("team", JoinType.INNER); 
			return builder.equal(t.get("name"), teamName);
		};
	}

	public static Specification<Member> username(final String username) {
		// Specification interface 구현 toPredicate
		// 구현 메소드가 하나만 존재하면 아래와 같이 람다식으로 표현 가능
		return (Specification<Member>) (root, query, builder) -> builder.equal(root.get("username"), username);
	}
}
