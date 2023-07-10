package myone.datajpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import myone.datajpa.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long>{

	Optional<Team> findByName(String string);
}
