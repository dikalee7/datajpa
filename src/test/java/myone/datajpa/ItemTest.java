package myone.datajpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import myone.datajpa.entity.Item;
import myone.datajpa.repository.ItemRepository;

@SpringBootTest
class ItemTest {

	@Autowired ItemRepository itemRepository;
	
	@Test
	void test() {
		Item item = Item.createItem()
				.id("item0001")
				.name("아이템0001")
				.build();
		
		Item saveItem = itemRepository.save(item);
		assertThat(item).isEqualTo(saveItem);
	}

}
