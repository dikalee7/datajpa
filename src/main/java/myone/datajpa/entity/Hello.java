package myone.datajpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Hello {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
}