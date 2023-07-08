package myone.datajpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    private String name;
    
    @OneToMany(mappedBy = "team")
    List<Member> members = new ArrayList<>();

    @Builder(builderClassName = "TeamBuilder", builderMethodName = "createTeam")
	public Team(String name) {
		this.name = name;
	}
    
    public void changeName(String name) {
		this.name = name;
	}
    
    public void addMember(Member m) {
    	this.getMembers().add(m);
    	m.applyTeam(this);
    }
    
}
