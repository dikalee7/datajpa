package myone.datajpa.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.domain.Persistable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Item extends BaseTimeEntity implements Persistable<String>{	// 
	@Id 
	private String id;
	
	private String name;
	
	
	@Override
	public boolean isNew() {
		return getCreateDate()==null;
	}

	@Builder(builderClassName = "ItemBuilder", builderMethodName = "createItem")
	public Item(String id, String name) {
		this.id = id;
		this.name = name;
	}
}
