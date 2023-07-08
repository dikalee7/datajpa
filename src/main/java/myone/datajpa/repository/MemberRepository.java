package myone.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import myone.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
}
