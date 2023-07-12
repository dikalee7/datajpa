package myone.datajpa.dto;

import lombok.Data;

@Data
public class MemberDto {
	private Long id;
	private String username;
	private int age;
	private String teamName;
	
	public MemberDto(Long id, String username, int age, String teamName) {
		super();
		this.id = id;
		this.username = username;
		this.age = age;
		this.teamName = teamName;
	}
}
