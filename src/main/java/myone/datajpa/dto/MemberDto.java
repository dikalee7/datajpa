package myone.datajpa.dto;

import java.util.Optional;

import lombok.Data;
import myone.datajpa.entity.Member;
import myone.datajpa.entity.Team;

@Data
public class MemberDto {
	private Long id;
	private String username;
	private int age;
	private String teamName;
	
	public MemberDto(Long id, String username, int age, String teamName) {
		this.id = id;
		this.username = username;
		this.age = age;
		this.teamName = teamName;
	}
	
	public MemberDto(Member member) {
		this.id = member.getId();
		this.username = member.getUsername();
		this.age = member.getAge();
		this.teamName = Optional.ofNullable(member.getTeam()).orElseGet(()->Team.createTeam().build()).getName();
	}
}
