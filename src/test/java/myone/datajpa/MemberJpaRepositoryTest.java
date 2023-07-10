package myone.datajpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import myone.datajpa.entity.Member;
import myone.datajpa.entity.Team;
import myone.datajpa.repository.MemberJpaRepository;
import myone.datajpa.repository.TeamJpaRepository;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {
	
	@Autowired MemberJpaRepository memberJpaRepository;
	@Autowired TeamJpaRepository teamJpaRepository;
	@PersistenceContext EntityManager em;

	@Test
	void testMember() {
		registTeam();
		
		Team  teamA = teamJpaRepository.findByName("teamA").get();
		Member member1 = Member.createMember().username("member1").build();
		memberJpaRepository.save(member1);
		member1.applyTeam(teamA);

		Member member2 = Member.createMember().username("member2")
				.age(5).build();
		memberJpaRepository.save(member2);
		member2.applyTeam(teamA);
		
		Member member2_1 = Member.createMember().username("member2")
				.age(10).build();
		memberJpaRepository.save(member2_1);
		
		//단건 조회 검증
		Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
		Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);
		findMember1.changeMember("member!!!!!", findMember1.getAge());
		
		//리스트 조회 검증
		List<Member> findAll = memberJpaRepository.findAll();
		assertThat(findAll.size()).isEqualTo(3);
		
		//카운트 조회 검증
		long count = memberJpaRepository.count();
		assertThat(count).isEqualTo(3);
		
		//삭제
//		memberJpaRepository.delete(findMember1);
//		memberJpaRepository.delete(findMember2);
//		assertThat(memberJpaRepository.count()).isEqualTo(0);
		
		List<Member> test = memberJpaRepository.findByUsernameAndAgeGreaterThan("member2", 6);
		System.out.println(test);
	}

	private void registTeam() {
		// TODO Auto-generated method stub
		String[] teamNames=new String[]{"teamA", "teamB", "teamC"};
		
		for (String teamName : teamNames) {
			Team team = Team.createTeam().name(teamName).build();
			teamJpaRepository.save(team);
		}
	}

	

}
