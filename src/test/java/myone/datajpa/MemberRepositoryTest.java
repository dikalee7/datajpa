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
import myone.datajpa.repository.MemberRepository;
import myone.datajpa.repository.TeamRepository;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
	
	@Autowired MemberRepository memberRepository;
	@Autowired TeamRepository teamRepository;
	
	@PersistenceContext
	 EntityManager em;
	

	@Test
	void testMember() {
		Team teamA = Team.createTeam().name("teamA").build();
		Team saveTeamA = teamRepository.save(teamA);
		
		Member memberA = Member.createMember().username("memberA").build();
		memberA.applyTeam(saveTeamA);
		Member saveMemberA = memberRepository.save(memberA);
		
		Member memberB = Member.createMember().username("memberB").build();
		Member saveMemberB = memberRepository.save(memberB);
		teamA.addMember(saveMemberB);
		
		// 초기화
		em.flush();
		em.clear();
		// 확인
		List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
		for (Member member : members) {
			System.out.println("member=" + member);
			System.out.println("-> member.team=" + member.getTeam());
		}
			
		
	}

	

}
