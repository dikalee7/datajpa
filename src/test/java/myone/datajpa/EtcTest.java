package myone.datajpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;

import myone.datajpa.common.MoBusinessException;
import myone.datajpa.dto.UsernameOnly;
import myone.datajpa.entity.Member;
import myone.datajpa.entity.Team;
import myone.datajpa.repository.MemberRepository;
import myone.datajpa.spec.MemberSpec;

@SpringBootTest
@Transactional
@Rollback(false)
class EtcTest {
	@PersistenceContext
	private EntityManager em;

	@Autowired
	MemberRepository memberRepository;

	@Test
	void test() {
		Member member = Member.createMember().username("dikalee").build();
//		member.applyTeam(Team.createTeam().name("ddd").build());

		System.out.println(Optional.ofNullable(member.getTeam()).orElseGet(() -> Team.createTeam().build()).getName());

		Optional.ofNullable(member.getTeam()).orElseThrow(MoBusinessException::new);
		// Optional.ofNullable(member.getTeam()).orElseThrow(() -> new
		// IllegalArgumentException());

	}

	@Test
	void specTest() {
		Team teamA = Team.createTeam().name("teamA").build();
		em.persist(teamA);

		Member member1 = Member.createMember().username("dikalee1").age(0).build();
		Member member2 = Member.createMember().username("dikalee2").age(0).build();
		// 팀설정
		member1.applyTeam(teamA);
		member2.applyTeam(teamA);

		em.persist(member1);
		em.persist(member2);

		em.flush();
		em.clear();

		Specification<Member> specUsername = MemberSpec.username("dikalee1");
		Specification<Member> specTeamname = MemberSpec.teamName("teamA");

		// username과 팀 name을 and 조건으로 검색
		List<Member> result = memberRepository.findAll(specUsername.and(specTeamname));

		assertThat(result.size()).isEqualTo(1);

	}

	@Test
	void QueryByExampleTest() {
		Team teamA = Team.createTeam().name("teamA").build();
		em.persist(teamA);

		Member member1 = Member.createMember().username("dikalee1").age(0).build();
		Member member2 = Member.createMember().username("dikalee2").age(0).build();
		// 팀설정
		member1.applyTeam(teamA);
		member2.applyTeam(teamA);

		em.persist(member1);
		em.persist(member2);

		em.flush();
		em.clear();

		// Probe 생성
		// 필드에 데이터가 있는 실제 도메인 객체
		Member member = Member.createMember().username("dikalee1").age(0).build();
		Team team = Team.createTeam().name("teamA").build(); // 내부조인으로 teamA 가능
		member.applyTeam(team);

		// ExampleMatcher 생성, age 프로퍼티는 무시
		// 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

		// Example
		// Probe와 ExampleMatcher로 구성, 쿼리를 생성하는데 사용
		Example<Member> example = Example.of(member, matcher);

		List<Member> result = memberRepository.findAll(example);
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void projections() throws Exception {
		Team teamA = Team.createTeam().name("teamA").build();
		em.persist(teamA);

		Member member1 = Member.createMember().username("dikaleeA").age(0).build();
		Member member2 = Member.createMember().username("dikaleeB").age(0).build();
		// 팀설정
		member1.applyTeam(teamA);
		member2.applyTeam(teamA);

		em.persist(member1);
		em.persist(member2);

		em.flush();
		em.clear();

		List<UsernameOnly> result = memberRepository.findProjectionsByUsername("dikaleeB");
		
		result.forEach(t -> {
			System.out.println(t.getUsername());
			System.out.println(t.getAge());
		});
		
		assertThat(result.size()).isEqualTo(1);
	}

}
