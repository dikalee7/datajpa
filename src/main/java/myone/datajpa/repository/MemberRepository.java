package myone.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import myone.datajpa.dto.MemberDto;
import myone.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	
	@Query(value = "select m from Member m left join fetch m.team")
	List<Member> findAllFetch();
	
	List<Member> findByUsername(String username);
	
	List<Member> findByUsernameLikeAndAgeGreaterThan(String username, int age);
	
	
//	@Query(name = "Member.findByNamedQuery")
	List<Member> findByNamedQuery(@Param("username") String username);
	
	@Query(value = "select m from Member m where m.username = :username and m.age = :age")
	List<Member> findUser(@Param("username") String username, @Param("age") int age);
	
	@Query("select m.username from Member m")
	List<String> findUsernameList();
	
	@Query("select new myone.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m left join m.team t order by m.id")
	List<MemberDto> findMemberDto();
	
	@Query("select m from Member m where m.username in :usernames")
	List<Member> findByNames(@Param("usernames") Collection<String> usernames);
	
	List<Member> findListByUsername(String username); //컬렉션
	Member findMemberByUsername(String username); //단건
	Optional<Member> findOptionalByUsername(String username); //단건 Optional

	@Query( value = "select m from Member m left join fetch m.team where m.age = :age",
			countQuery = "select count(m.username) from Member m where m.age = :age")
	Page<Member> findByAge(@Param("age") int age, Pageable pageable);
	
	@Modifying(clearAutomatically = true)
	@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
	int bulkAgePlus(@Param("age") int age);
}
