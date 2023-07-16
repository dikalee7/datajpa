package myone.datajpa.controller;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import myone.datajpa.common.MoBusinessException;
import myone.datajpa.dto.MemberDto;
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
	
	@GetMapping("/members/jdbctemplate")
	public List<MemberDto> list(Sort sort) {
		return memberRepository.findAllJdbcTemplateSort(sort);
	}
	
	@GetMapping("/paging/jdbctemplate")
	public Page<MemberDto> listPaging(Pageable pageable) {
		return memberRepository.findAllPageable(pageable);
	}
	
	@GetMapping("/sortedmembers")
	public List<MemberDto> sortedL(Sort sort) {
		return memberRepository.findAll(sort).stream().map(member->new MemberDto(member.getId(), member.getUsername(), member.getAge(), null)).toList();
	}
	

	@GetMapping("/members")
	public Page<MemberDto> list(@PageableDefault(size = 5) @SortDefaults({
		@SortDefault(sort = {"id"}, direction = Sort.Direction.DESC),
		@SortDefault(sort = {"username"}, direction = Sort.Direction.DESC)
	}) Pageable pageable) {
		Page<Member> pages = memberRepository.findAll(pageable);
		return getPage(pages, pageable,  member -> new MemberDto(member.getId(), member.getUsername(), member.getAge(), null));
	}
	
	// List to Page
	private <T, M> PageImpl<T> getPage(Page<M> pages, Pageable pageable , Function<M, T> mapper) {
		return new PageImpl<T>(
				pages.getContent().stream().map(mapper).collect(Collectors.toList()), 
				pageable, 
				pages.getTotalElements());
	}
	
	@GetMapping("members2")
	public Page<MemberDto> listMemberDto(@PageableDefault(size = 5) @SortDefaults({
		@SortDefault(sort = {"id"}, direction = Sort.Direction.DESC),
		@SortDefault(sort = {"username"}, direction = Sort.Direction.DESC)
	}) Pageable pageable) {
		return memberRepository.findAll(pageable).map(MemberDto::new);
	}

	

	@PostConstruct
	public void init() {
		for (int i = 0; i < 100; i++) {
			memberRepository.save(Member.createMember().username("dikalee"+i).age(47).build());
		}
	}
}
