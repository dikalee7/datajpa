package myone.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import myone.datajpa.entity.Item;

public interface ItemRepository extends JpaRepository<Item, String>{
}
