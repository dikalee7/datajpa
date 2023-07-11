package myone.datajpa;

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
		initData();
		List<Member> findByPage = memberJpaRepository.findByPage(10, 0, 20);
		findByPage.forEach(m -> {
			System.out.println(m.getUsername());
		});
	}
	
	@Test
	void bulkUpdate() {
		initData();
		memberJpaRepository.bulkAgePlus(20);
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
			teamJpaRepository.save(team);
		}
	}

	private void registMember() {
		Team teamA = teamJpaRepository.findByName("teamA").get();
		Team teamB = teamJpaRepository.findByName("teamB").get();
		for (int i = 0; i < 100; i++) {
			Member member1 = Member.createMember().username("member1_"+String.format("%04d", i)).age(10).build();
			member1.applyTeam(teamA);
			memberJpaRepository.save(member1);
			
			
			Member member2 = Member.createMember().username("member2_"+String.format("%04d", i)).age(20).build();
			member2.applyTeam(teamB);
			memberJpaRepository.save(member2);
		}
		
		
//		Team teamA = teamJpaRepository.findByName("teamA").get();
//		Member member1 = Member.createMember().username("member1").age(10).build();
//		memberJpaRepository.save(member1);
//		member1.applyTeam(teamA);
//
//		Member member2 = Member.createMember().username("member2").age(5).build();
//		memberJpaRepository.save(member2);
//		member2.applyTeam(teamA);
//		
//		Member member2_1 = Member.createMember().username("member2_1").age(10).build();
//		memberJpaRepository.save(member2_1);		
	}

}
