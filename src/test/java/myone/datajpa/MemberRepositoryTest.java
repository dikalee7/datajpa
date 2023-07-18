package myone.datajpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import myone.datajpa.common.MoBusinessException;
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
	void fetchJoin() {
		initData();
		
		// fetch join list
		List<Member> findAllFech = memberRepository.findAllFetch();
		for (Member member : findAllFech) {
			Team team = Optional.ofNullable(member.getTeam()).orElse(Team.createTeam().build());
			System.out.println(member.getUsername() + " :: " + team.getName());
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
	
	@Test
	void queryByCollection() {
		initData();
		
		List<Member> findMembers = memberRepository.findByNames(Arrays.asList("member1", "member2"));
		
		System.out.println(findMembers);
	}
	
	@Test
	void reType() {
		initData();
		List<Member> findListByUsername = memberRepository.findListByUsername("member1");
		System.out.println(findListByUsername);
		
		Member findMemberByUsername = memberRepository.findMemberByUsername("member2");
		System.out.println(findMemberByUsername);
		
		Optional<Member> findOptionalByUsername = memberRepository.findOptionalByUsername("member2_1");
		System.out.println(findOptionalByUsername.get());
	}
	
	@Test
	void pagingTest() {
		initData();
		PageRequest pr = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "username"));
		Page<Member> findByPage = memberRepository.findByAge(10, pr);
		Page<MemberDto> memberDtoPage = findByPage.map( member -> 
			new MemberDto(member.getId(), member.getUsername(), member.getAge(), Optional.ofNullable(member.getTeam()).orElseGet(()-> Team.createTeam().build()).getName())
		);
		
		List<MemberDto> content = memberDtoPage.getContent();
		long totalElements = memberDtoPage.getTotalElements();
		int totalPages = memberDtoPage.getTotalPages();
		System.out.println(content);
		content.forEach(m -> {
			System.out.println(m.getUsername());
		});
		System.out.println("totalElements == " + totalElements);
		System.out.println("totalPages == " + totalPages);
	}
	
	@Test
	void bulkUpdate() {
		initData();
		Member addMember = Member.createMember().username("add kim").age(20).build();
		memberRepository.save(addMember);
		memberRepository.bulkAgePlus(20);
		Member member = memberRepository.findOptionalByUsername("add kim").orElseThrow(MoBusinessException::new);
		assertThat(member.getAge()).isEqualTo(21);
	}
	
	@Test
	void fetchJoinTest() {
		initData();
		List<Member> findMemberFetchJoin = memberRepository.findAll();
		
		for (Member member : findMemberFetchJoin) {
			System.out.println(member);
			System.out.println(Optional.ofNullable(member.getTeam()).orElseGet(()->Team.createTeam().build()).getName());
			System.out.println("===================================");
		}
	}
	
	@Test
	void jpaHintId() {
		Member member = Member.createMember()
				.username("jpahint")
				.age(15)
				.build();
		Member saveMember = memberRepository.save(member);
		em.flush();
		em.clear();
		
		Member findMember = memberRepository.findMemberReadonlyById(saveMember.getId())
				.orElseThrow(() -> new MoBusinessException("There is no member!!!"));
		
		assertThat(findMember.getId()).isEqualTo(saveMember.getId());
		
		findMember.changeMember("readonly", findMember.getAge());
		em.flush();
	}
	
	@Test
	void versionTest() {
		Team team = Team.createTeam().name("marketing").build();
		teamRepository.save(team);
		Member member = Member.createMember()
				.username("jpalock")
				.age(15)
				.build();
//		member.applyTeam(team);
		Member saveMember = memberRepository.save(member);
		em.flush();
		em.clear();
//		Member findMember = memberRepository.findById(saveMember.getId()).orElseThrow(() -> new MoBusinessException("There is no member!!!"));
		Member findMember = memberRepository.findByIdForUpdate(saveMember.getId()).orElseThrow(() -> new MoBusinessException("There is no member!!!"));
//		findMember.changeMember("jpalock1", findMember.getAge());
		findMember.applyTeam(team);
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
		Team teamB = teamRepository.findByName("teamB").get();
		for (int i = 0; i < 10; i++) {
			Member smember1 = Member.createMember().username("member1_"+String.format("%04d", i)).age(10).build();
			smember1.applyTeam(teamA);
			memberRepository.save(smember1);
			
			
			Member smember2 = Member.createMember().username("member2_"+String.format("%04d", i)).age(20).build();
			smember2.applyTeam(teamB);
			memberRepository.save(smember2);
		}
		
		Member member1 = Member.createMember().username("member1").age(10).build();
		member1.applyTeam(teamA);
		memberRepository.save(member1);
		
		Member member2 = Member.createMember().username("member2").age(5).build();
		member2.applyTeam(teamA);
		memberRepository.save(member2);
		
		Member member2_1 = Member.createMember().username("member2_1").age(10).build();
		memberRepository.save(member2_1);		
	}
}
