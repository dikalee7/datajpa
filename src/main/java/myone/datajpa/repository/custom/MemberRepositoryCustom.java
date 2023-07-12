package myone.datajpa.repository.custom;

import java.util.List;

import myone.datajpa.entity.Member;

public interface MemberRepositoryCustom {
	List<Member> findAllCustom();
}
