# Pagination and sorting 


> JdbcTemplate 
- spring-data-commons dependency 추가
  - Spring Boot / Spring Data Jpa를 사용하는 경우 org.springframework.boot:spring-boot-starter-data-jpa에 spring-data-commons이 포함되어 있음
- Sort
  - org.springframework.data.domain.Sort 이용
  
  ```
	public List<MemberDto> findAllJdbcTemplateSort(Sort sort) {
		Order order = sort.toList().get(0);

		StringBuffer query = new StringBuffer();
		query.append(" select");
		query.append("  m.member_id as member_id,");
		query.append("  m.username as username,");
		query.append("  m.age as age,");
		query.append("  t.name as team_name");
		query.append(" from member m");
		query.append(" left outer join team t");
		query.append("    on m.team_id=t.team_id");
		query.append(" order by " + order.getProperty() + " " + order.getDirection().name());
		return this.jdbcTemplate.query(query.toString(),
				(rs, rowNum) -> new MemberDto(Long.parseLong(rs.getString("member_id")), rs.getString("username"),
						rs.getInt("age"), rs.getString("team_name")));
	}
	
	
	@Test
	void test() {
		initData();
		
		// By user name in descending order
	    Sort sort = Sort.by(Direction.fromString("DESC"), "USERNAME");
	    
	    // Sorted Members
	    List<MemberDto> sortedMembers = memberRepository.findAllJdbcTemplateSort(sort);
	    sortedMembers.forEach(member -> System.out.println(member));
	    
	}	
  ```
- Paging and Sort
  - org.springframework.data.domain.PageRequest/Pageable/PageImpl/Page/Sort 이용
  ```
	public Page<MemberDto> findAllPageable(Pageable page) {
		Order order = !page.getSort().isEmpty() ? page.getSort().toList().get(0) : Order.by("ID");
		
		StringBuffer query = new StringBuffer();
		query.append(" select");
		query.append("  m.member_id as member_id,");
		query.append("  m.username as username,");
		query.append("  m.age as age,");
		query.append("  t.name as team_name");
		query.append(" from member m");
		query.append(" left outer join team t");
		query.append("    on m.team_id=t.team_id");
		query.append(" order by " + order.getProperty() + " " + order.getDirection().name() + " LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset());
		
		
		List<MemberDto> members = jdbcTemplate.query(query.toString(),
				(rs, rowNum) -> new MemberDto(Long.parseLong(rs.getString("member_id")), rs.getString("username"),
						rs.getInt("age"), rs.getString("team_name")));
		
	    return new PageImpl<MemberDto>(members, page, jdbcTemplate.queryForObject("select count(*) from member", Integer.class));
	}
	
	@Test
	void paging() {
		initData();
		
		// By user name in descending order
	    PageRequest pageable = PageRequest.of(0, 5, Direction.fromString("DESC"), "USERNAME");
	    
	    // Paged and Sorted Members
	    Page<MemberDto> pagedMembers = memberRepository.findAllPageable(pageable);
	}
  ```


<br>


