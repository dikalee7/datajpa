package myone.datajpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import myone.datajpa.dto.MemberDto;
import myone.datajpa.entity.Member;
import myone.datajpa.entity.Team;
import myone.datajpa.repository.MemberRepository;
import myone.datajpa.repository.TeamRepository;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;
	@Autowired
	TeamRepository teamRepository;

	@PersistenceContext
	EntityManager em;

	@Test
	void testMember() {
		initData();
		
		Member member1 = memberRepository.findByUsername("member1").get(0);
		Member member2 = memberRepository.findByUsername("member2").get(0);

		// 단건 조회 검증
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);

		// 리스트 조회 검증
		List<Member> findAll = memberRepository.findAll();
		assertThat(findAll.size()).isEqualTo(3);

		// 카운트 조회 검증
		long count = memberRepository.count();
		assertThat(count).isEqualTo(3);

		// 삭제
//		memberRepository.delete(findMember1);
//		memberRepository.delete(findMember2);
//		assertThat(memberRepository.count()).isEqualTo(0);
		
		List<String> findUsernameList = memberRepository.findUsernameList();
		System.out.println(findUsernameList);
	}
	
	@Test
	void QueryCreation() {
		initData();
		
		List<Member> test = memberRepository.findByUsernameLikeAndAgeGreaterThan("member2", 6);
		System.out.println(test);
	}
	
	@Test
	void fetchJoinTest() {
		initData();
		
		// fetch join list
		List<Member> findAllFech = memberRepository.findAllFetch();
		for (Member member : findAllFech) {
			Optional.ofNullable(member.getTeam()).ifPresentOrElse(v -> {
				System.out.println(member.getUsername() + " :: " + v.getName());
			}, () -> {
				System.out.println(member.getUsername() + " :: no team");
			});
		}
	}
	
	@Test
	void namedQueryTest() {
		initData();
		
		List<Member> namedQueryTest = memberRepository.findByNamedQuery("member1");
		System.out.println(namedQueryTest);
	}
	
	@Test
	void queryDtoTest() {
		initData();
		
		List<MemberDto> findMemberDto = memberRepository.findMemberDto();
		System.out.println(findMemberDto);
	}
	
	private void initData() {
		// 팀 등록
		registTeam();

		// 멤버 등록
		registMember();

		em.flush();
		em.clear();
	}
	
	private void registTeam() {
		// TODO Auto-generated method stub
		String[] teamNames = new String[] { "teamA", "teamB", "teamC" };

		for (String teamName : teamNames) {
			Team team = Team.createTeam().name(teamName).build();
			teamRepository.save(team);
		}
	}

	private void registMember() {
		Team teamA = teamRepository.findByName("teamA").get();
		Member member1 = Member.createMember().username("member1").age(10).build();
		memberRepository.save(member1);
		member1.applyTeam(teamA);

		Member member2 = Member.createMember().username("member2").age(5).build();
		memberRepository.save(member2);
		member2.applyTeam(teamA);
		
		Member member2_1 = Member.createMember().username("member2_1").age(10).build();
		memberRepository.save(member2_1);		
	}
}
