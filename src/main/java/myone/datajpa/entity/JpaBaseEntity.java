package myone.datajpa.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.Getter;

@MappedSuperclass
@Getter
public class JpaBaseEntity {
	
	@Column(updatable = false)
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	
	@PrePersist
	public void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		this.createDate = now;
		this.updateDate = now;
	}
	
	@PreUpdate
	public void preUpdate() {
		LocalDateTime now = LocalDateTime.now();
		this.updateDate = now;
	}
	
}
