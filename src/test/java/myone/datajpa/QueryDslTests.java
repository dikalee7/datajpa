package myone.datajpa;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.querydsl.jpa.impl.JPAQueryFactory;

import myone.datajpa.entity.Hello;
import myone.datajpa.entity.QHello;

@SpringBootTest
@Transactional
@Rollback(false)
public class QueryDslTests {
	@PersistenceContext
	EntityManager em;

	@Test
	void test() {
		Hello hello = new Hello();
		em.persist(hello);
		
		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qhello = QHello.hello;
		
		Hello result = query.selectFrom(qhello).fetchOne();
		
		assertThat(result).isEqualTo(hello);
	}
}
