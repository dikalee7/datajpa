package myone.datajpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import myone.datajpa.entity.Member;
import myone.datajpa.repository.MemberJpaRepository;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {
	
	@Autowired MemberJpaRepository memberJpaRepository;

	@Test
	void testMember() {
		Member member = Member.createMember().username("memberA").build();
		Member saveMember = memberJpaRepository.save(member);
		
		Member findMember = memberJpaRepository.find(saveMember.getId());
		
		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember).isEqualTo(member);
	}

	

}
