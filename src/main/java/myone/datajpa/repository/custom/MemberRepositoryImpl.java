package myone.datajpa.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import myone.datajpa.entity.Member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
	
	private final EntityManager em;
	
	@Override
	public List<Member> findAllCustom() {
		// TODO Auto-generated method stub
		return em.createQuery("select m From Member m left join fetch m.team", Member.class).getResultList();
	}

}
