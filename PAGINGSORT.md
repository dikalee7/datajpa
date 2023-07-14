# Pagination and sorting 

> Spring Data Core
  - org.springframework.data.domain package 이용
    - spring-data-commons dependency 필요
    - 스프링 데이터 코어 기능으로 JPA, Jdbc Template, MyBatis 등 어느곳에서든 사용가능 
  - org.springframework.boot:spring-boot-starter-data-jpa 
    - spring-data-commons 포함되어 있음
    
> 주 사용 클래스
  - org.springframework.data.domain.Sort
  - org.springframework.data.domain.PageRequest
  - org.springframework.data.domain.Pageable
  - org.springframework.data.domain.PageImpl
  - org.springframework.data.domain.Page

<br>


> Sort
  - Spring Data Jpa
  - org.springframework.data.domain.Sort를 파라미터값으로 전달해주기만 하면 됨
  
  ```
    //Web에서 호출 시 {host}:{port}/sortedmembers?sort=id,desc&sort=username,asc
    @GetMapping("/sortedmembers")
    public List<MemberDto> sortedList(Sort sort) {
      return memberRepository.findAll(sort).stream().map(member->new MemberDto(member.getId(), member.getUsername(), member.getAge(), null)).toList();
    }
  ```
  
  - JdbcTemplate 
  
  ```
	@Test
	void sort() {
          initData();

          // Java에서 직접 Sort 정보 생성 시
          Sort sort1 = Sort.by("username").ascending();
          Sort sort2 = Sort.by("member_id").descending();
          Sort sortAll = sort1.and(sort2);
	    
          // Sorted Members
          List<MemberDto> sortedMembers = memberRepository.findAllJdbcTemplateSort(sortAll);
          sortedMembers.forEach(member -> System.out.println(member));
	}
	
	//Web에서 호출 시 {host}:{port}/members/jdbctemplate?sort=member_id,desc&sort=username,asc
	@GetMapping("/members/jdbctemplate")
	public List<MemberDto> list(Sort sort) {
          return memberRepository.findAllJdbcTemplateSort(sort);
	}
	
	@Override
	public List<MemberDto> findAllJdbcTemplateSort(Sort sort) {
          StringBuffer query = new StringBuffer();
          query.append(" select");
          query.append("  m.member_id as member_id,");
          query.append("  m.username as username,");
          query.append("  m.age as age,");
          query.append("  t.name as team_name");
          query.append(" from member m");
          query.append(" left outer join team t");
          query.append("    on m.team_id=t.team_id");

          if (!sort.isUnsorted()) {
            query.append(" order by ");
            for (Iterator<Order> iterator = sort.toList().iterator(); iterator.hasNext();) {
              Order order = (Order) iterator.next();
              query.append(" " + order.getProperty() + " " + order.getDirection().name());
              if (iterator.hasNext()) query.append(", ");
            }
          }

          return this.jdbcTemplate.query(query.toString(),
              (rs, rowNum) -> new MemberDto(Long.parseLong(rs.getString("member_id")), rs.getString("username"),
                   rs.getInt("age"), rs.getString("team_name")));
	}
	
  ```

<br>

> Paging
  - Spring Data Jpa
  - org.springframework.data.domain.Pageable을 파라미터값으로 전달해주기만 하면 됨
  
  ```
    //Web에서 호출 시 {host}:{port}/members?page=0&size=10&sort=id,desc&sort=username,desc
	public Page<MemberDto> list(Pageable pageable) {
		Page<Member> pages = memberRepository.findAll(pageable);
		return getPage(pages, pageable,  member -> new MemberDto(member.getId(), member.getUsername(), member.getAge(), null));
	}

	// List to Page
	private <T, M> PageImpl<T> getPage(Page<M> pages, Pageable pageable , Function<M, T> mapper) {
		return new PageImpl<T>(
				pages.getContent().stream().map(mapper).collect(Collectors.toList()), 
				pageable, 
				pages.getTotalElements());
	}
  ```
  
  - JdbcTemplate
   
  ```
	@Test
	void paging() {
		initData();
		
		// set Sort
		Sort sort1 = Sort.by("username").descending();
		Sort sort2 = Sort.by("member_id").ascending();
	    Sort sortAll = sort1.and(sort2);
	    
		PageRequest pageable = PageRequest.of(0, 5, sortAll);
	    
	    // Paged and Sorted Members
	    Page<MemberDto> pagedMembers = memberRepository.findAllPageable(pageable);
	}
	
	//Web에서 호출 시 {host}:{port}/paging/jdbctemplate?page=1&size=5&sort=member_id,asc&sort=username,desc
	@GetMapping("/paging/jdbctemplate")
	public Page<MemberDto> listPaging(Pageable pageable) {
		return memberRepository.findAllPageable(pageable);
	}
	
	public Page<MemberDto> findAllPageable(Pageable page) {
		StringBuffer query = new StringBuffer();
		query.append(" select");
		query.append("  m.member_id as member_id,");
		query.append("  m.username as username,");
		query.append("  m.age as age,");
		query.append("  t.name as team_name");
		query.append(" from member m");
		query.append(" left outer join team t");
		query.append("    on m.team_id=t.team_id");
		
		Sort sort = page.getSort();
		if (!sort.isUnsorted()) {
			query.append(" order by ");

			for (Iterator<Order> iterator = sort.toList().iterator(); iterator.hasNext();) {
				Order order = (Order) iterator.next();
				query.append(" " + order.getProperty() + " " + order.getDirection().name());
				if (iterator.hasNext())
					query.append(", ");
			}
		}
		query.append(" LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset());
		
		
		List<MemberDto> members = jdbcTemplate.query(query.toString(),
				(rs, rowNum) -> new MemberDto(Long.parseLong(rs.getString("member_id")), rs.getString("username"),
						rs.getInt("age"), rs.getString("team_name")));
		
	    return new PageImpl<MemberDto>(members, page, jdbcTemplate.queryForObject("select count(*) from member", Integer.class));
	}
  ```