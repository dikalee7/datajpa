package myone.datajpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String username;
    
    private int age;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;


    @Builder(builderClassName = "MemberBuilder", builderMethodName = "createMember")
	public Member(String username, int age, Team team) {
		this.username = username;
		this.age = age;
		this.team = team;
	}

	public void changeMember(Member m) {
		this.username = m.getUsername();
		this.age = m.getAge();
		this.team = m.getTeam();
	}
	
	public void applyTeam(Team t) {
		this.team = t;
		t.getMembers().add(this);
	}
}