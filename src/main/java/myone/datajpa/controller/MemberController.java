package myone.datajpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import myone.datajpa.common.MoBusinessException;
import myone.datajpa.entity.Member;
import myone.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberRepository memberRepository;
	
	@GetMapping("/member/{id}")
	public String findMember(@PathVariable("id") long id) {
		Member member =  memberRepository.findById(id).orElseThrow(() -> new MoBusinessException("No result!!!"));
		return member.getUsername();
	}
	
	@GetMapping("/member2/{id}")
	public String findMember2(@PathVariable("id") Member member) {
//		Member member =  memberRepository.findById(id).orElseThrow(() -> new MoBusinessException("No result!!!"));
		return member.getUsername();
	}
	
	@GetMapping("/members")
	public Page<Member> list(Pageable pageable) {
		return memberRepository.findAll(pageable);
	}
	
	@PostConstruct
	public void init() {
		for (int i = 0; i < 10; i++) {
			memberRepository.save(Member.createMember().username("dikalee"+i).age(47).build());
		}
	}
}