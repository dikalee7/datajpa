package myone.datajpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQueries({
    @NamedQuery(name = "Member.findByNamedQuery", query = "select m from Member m left join fetch m.team where m.username = :username")
})
@NamedEntityGraph(name = "Member.findAllMembers", attributeNodes = @NamedAttributeNode("team"))
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

	public void changeMember(String username, int age) {
		this.username = username;
		this.age = age;
	}
	
	public void applyTeam(Team t) {
		this.team = t;
		t.getMembers().add(this);
	}
}