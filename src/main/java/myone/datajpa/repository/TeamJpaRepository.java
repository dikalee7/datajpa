package myone.datajpa.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import myone.datajpa.entity.Team;

@Repository
public class TeamJpaRepository {
	@PersistenceContext
	private EntityManager em;
	
	public Team save(Team team) {
		em.persist(team);
		return team;
	}
	
	public void delete(Team tema) {
		em.remove(tema);
	}
	
	public List<Team> findAll() {
		return em.createQuery("select t from Team t", Team.class).getResultList();
	}
	
	public Optional<Team> findById(Long id) {
		Team team = em.find(Team.class, id);
		return Optional.ofNullable(team);
	}
	
	public long count() {
		return em.createQuery("select count(t) from Team t", Long.class).getSingleResult();
	}
	
	public Optional<Team> findByName(String name) {
		return Optional.ofNullable(em.createQuery("select t from Team t where t.name = :name", Team.class)
				.setParameter("name", name)
				.getSingleResult());
	}
}
