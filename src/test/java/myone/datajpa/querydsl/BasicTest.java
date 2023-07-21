package myone.datajpa.querydsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.querydsl.jpa.impl.JPAQueryFactory;

import myone.datajpa.entity.Member;
import static myone.datajpa.entity.QMember.member;
import myone.datajpa.entity.Team;
import myone.datajpa.repository.MemberRepository;
import myone.datajpa.repository.TeamRepository;

@SpringBootTest
@Transactional
@Rollback(false)
public class BasicTest {
	@PersistenceContext
	EntityManager em;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	TeamRepository teamRepository;
	
	@Test
	public void test() {
		JPAQueryFactory query = new JPAQueryFactory(em);
		
		List<Member> fetchAll = query.selectFrom(member).where(member.username.like("querydsl%")).fetch();
		assertThat(fetchAll.size()).isEqualTo(20);
	}
	
	@Test
	public void startJPQL() {
		String qlString = "select m from Member m where m.username = :username";
		String strUsername = "querydsl1_0000";
		Member findMember = em.createQuery(qlString, Member.class)
			.setParameter("username", strUsername).getSingleResult();
		
		assertThat(findMember.getUsername()).isEqualTo(strUsername);
	}
	
	@Test
	public void basicSelectExam() {
		String strUsername = "querydsl1_0000";
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		Member findMember = queryFactory.selectFrom(member)
				.where(member.username.eq(strUsername))
				.fetchOne();
		assertThat(findMember.getUsername()).isEqualTo(strUsername);
	}
	
	@Test
	public void search() {
		String searchName = "querydsl1_0000";
		int searchAge = 10;
		
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		Member findMember = queryFactory.selectFrom(member)
			.where(
					member.username.eq(searchName),
					member.age.eq(searchAge)
			).fetchOne();
		
		assertThat(findMember.getUsername()).isEqualTo(searchName);
		assertThat(findMember.getAge()).isEqualTo(searchAge);
	}
	
	/**
	 * 회원 정렬 순서 1. 회원 나이 내림차순(desc) 2. 회원 이름 올림차순(asc) 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last) 
	 */
	@Test
	public void sort() {
		em.persist(Member.createMember().age(10).build());
		em.persist(Member.createMember().username("member5").age(10).build());
		em.persist(Member.createMember().username("member6").age(10).build());
		 
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		List<Member> result = queryFactory.selectFrom(member).where(member.age.eq(10))
				.orderBy(member.age.desc(), member.username.asc().nullsLast()).fetch();
		Member member5 = result.get(0);
		Member member6 = result.get(1);
		Member memberNull = result.get(result.size()-1);
		assertThat(member5.getUsername()).isEqualTo("member5");
		assertThat(member6.getUsername()).isEqualTo("member6");
		assertThat(memberNull.getUsername()).isNull();
	}

	@BeforeEach
	public void before() {
		// 팀 등록
		registTeam();

		// 멤버 등록
		registMember();

		em.flush();
		em.clear();
	}

	private void registTeam() {
		// TODO Auto-generated method stub
		String[] teamNames = new String[] { "teamA", "teamB", "teamC" };

		for (String teamName : teamNames) {
			Team team = Team.createTeam().name(teamName).build();
			teamRepository.save(team);
		}
	}

	private void registMember() {
		Team teamA = teamRepository.findByName("teamA").get();
		Team teamB = teamRepository.findByName("teamB").get();
		for (int i = 0; i < 10; i++) {
			Member smember1 = Member.createMember()
					.username("querydsl1_" + String.format("%04d", i))
					.age(10)
					.team(teamA).build();
			memberRepository.save(smember1);

			Member smember2 = Member.createMember()
					.username("querydsl2_" + String.format("%04d", i))
					.age(20)
					.team(teamB).build();
			memberRepository.save(smember2);
		}
	}
}
