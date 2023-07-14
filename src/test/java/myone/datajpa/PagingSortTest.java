package myone.datajpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
class PagingSortTest {
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	TeamRepository teamRepository;

	@PersistenceContext
	EntityManager em;

	@Test
	void sort() {
		initData();
		
		// By user name in descending order
	    Sort sort = Sort.by(Direction.fromString("DESC"), "USERNAME");
	    
	    // Sorted Members
	    List<MemberDto> sortedMembers = memberRepository.findAllJdbcTemplateSort(sort);
	    sortedMembers.forEach(member -> System.out.println(member));
	    
	}
	
	@Test
	void paging() {
		initData();
		
		// By user name in descending order
	    PageRequest pageable = PageRequest.of(0, 5, Direction.fromString("DESC"), "USERNAME");
	    
	    // Paged and Sorted Members
	    Page<MemberDto> pagedMembers = memberRepository.findAllPageable(pageable);
	}

	private void initData() {
		// TODO Auto-generated method stub
		Team teamA = Team.createTeam().name("teamA").build();
		teamRepository.save(teamA);
		
		Team teamB = Team.createTeam().name("teamB").build();
		teamRepository.save(teamB);
		
		for (int i = 0; i < 10; i++) {
			Member member1 = Member.createMember().username("dikalee"+String.format("%02d", i)).age(47).build();
			member1.applyTeam(teamA);
			memberRepository.save(member1);
			
			Member member2 = Member.createMember().username("bbok"+String.format("%02d", i)).age(49).build();
			member2.applyTeam(teamA);
			memberRepository.save(member2);
			
			Member member3 = Member.createMember().username("ssandol"+String.format("%02d", i)).age(19).build();
			member3.applyTeam(teamB);
			memberRepository.save(member3);
		}
		
		em.flush();
		em.clear();
	}
}
