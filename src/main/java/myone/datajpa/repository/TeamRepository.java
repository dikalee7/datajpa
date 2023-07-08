package myone.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import myone.datajpa.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long>{
}
