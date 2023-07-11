package myone.datajpa;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import myone.datajpa.entity.Member;

class EtcTest {

	@Test
	void test() {
		Member member = Member.createMember().username("dikalee").build();
		
		System.out.println(Optional.ofNullable(member).isPresent()?member:null);
	}

}
