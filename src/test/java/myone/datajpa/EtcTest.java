package myone.datajpa;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import myone.datajpa.entity.Member;
import myone.datajpa.entity.Team;

class EtcTest {

	@Test
	void test() {
		Member member = Member.createMember().username("dikalee").build();
		member.applyTeam(Team.createTeam().name("ddd").build());
		
		System.out.println(Optional.ofNullable(member.getTeam()).orElseGet(()-> Team.createTeam().build()).getName());
	
	}

}
