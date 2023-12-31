# Spring Data Jpa 


> gradle 의존관계 보기  
> ./gradlew dependencies --configuration compileClasspath    
<br>

> Assertj  
> https://joel-costigliola.github.io/assertj/index.html  
<br>


> Spring Data Jpa 구현체
- org.springframework.data.jpa.repository.support.SimpleJpaRepository
  - Default implementation of the {@link org.springframework.data.repository.CrudRepository} interface
- save New 판단 기준
  - Entity의 Key가 되는 값의 NULL 체크로 판단함
  - 때문에, @GeneratedValue의 경우에는 무관하나 직접 키값을 정의해야하는 Entity의 경우에는 NULL이 아니므로 save 함수의 persist가 아닌 merge 로직을 수행하며,
  - 결과적으로는 데이터는 insert되지만 insert 이전에 불필요한 select문이 발생하여 비효율적임
  - 해결책 ->  org.springframework.data.domain.Persistable 인터페이스를 상속받아 `override메소드 isNew`를 재정의해주어야 함
  
  ```
  @Entity
  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @ToString(of = {"id", "name"})
  public class Item extends BaseTimeEntity implements Persistable<String>{	// 
	@Id 
	private String id;
	
	private String name;
	
	
	@Override
	public boolean isNew() {
		return getCreateDate()==null;
	}

	@Builder(builderClassName = "ItemBuilder", builderMethodName = "createItem")
	public Item(String id, String name) {
		this.id = id;
		this.name = name;
	}
  }  
  ```
<br>


> JPA Entity에 기본 생성자가 필요한 이유   
 - 데이터를 DB에서 조회해 온 뒤 객체를 생성할 때 Java Reflection을 사용
 - Reflection은 클래스 이름만 알면 생성자, 필드, 메서드 등 클래스의 모든 정보에 접근이 가능 하지만 생성자의 매개변수는 알 수 없기때문에 기본 생성자로 객체를 생성하고 필드 값을 강제로 매핑해주는 방식을 사용
<br>


> JPA Entity에 기본 생성자는 protected로 설정     
 - Entity 객체만 생각하면 private로 설정해도 문제 없으나 지연로딩과 그를 위한 Proxy 객체를 고려하면 protected로 설정하여 Entity 객체를 상속받는 Proxy 객체에서 부모의 생성자로 접근이 가능하도록 하여야 함  
<br>


> JpaRepository  
 - JpaRepository를 상속받은 interface는 Spring Data Jpa가 구현체를 Proxy 객체로 생성하여 injection 함
 - Repository <- CrudRepository <- PagingAndSortingRepository <- JpaRepository
<br>


> Query Creation  
> https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
<br>


> @NamedQuery   
 - Entity 객체에 정의 하여 사용
 - JPQL을 미리 정의 하여 두고 지정한 name으로 호출하여 사용함
 - Spring Data Jpa에서는 Repository interface에서 @Query의 name 속성에 해당 @NamedQuery name 값을 적용하여 호출
 - @NamedQuery의 name을 EntityClassName.RepositoryMethodName 으로 명명한 경우 Repository interface의 @Query 어노테이션을 생략하여도 됨
 - 여러개의 @NamedQuery 적용을 위해서는 @NamedQueries 이용
 - 하지만 실무에서는 Entity 객체에 선언하여 사용하는 @NamedQuery 기능은 거의 사용하지 않음
 - Spring Data Jpa에서 Repository interface에서 직접 JPQL 작성하여 사용하는 @Query 어노테이션 이용함
<br>


> @Query  
 - Repository interface 메소드에 직접 적용
 - parameter는 @Param으로 받을 수 있음
 - em.createQuery를 이용하여 호출하는 JPQL은 문자열로 사전 검증되지 않으나 @NamedQuery와 @Query는 JPQL 쿼리문이 어플리케이션 구동시 검증됨
```
//parameter 예시
@Query("select m From Member m where m.username = :username and m.age = :age")
List<Member> findQueryMember(@Param("username") String username, @Param("age") int age);

@Query("select m from Member m where m.username in :usernames")
List<Member> findByNames(@Param("usernames") Collection<String> usernames);
```
```
//dto로 결과를 받을 경우 new사용하여 패키지경로까지 모두 적어주어야 
@Query("select new myone.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
List<MemberDto> findMemberDto();
```
<br>


> 유연한 반환타입 지정  
> https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-return-types  
 - 컬렉션
```
List<Member> findListByUsername(String username);
no result : empty collection
```
 - 단건
```
Member findMemberByUsername(String username);
no result : null
```
 - 단건 Optional
```
Optional<Member> findOptionalByUsername(String username); //단건 Optional
no result : Optional.empty
```
<br>


> 페이징과 정렬
 - 파라미터
```
org.springframework.data.domain.Sort : 정렬기능  
org.springframework.data.domain.Pageable : 페이징 기능(내부에 Sort포함)
```
 - 반환타입
```
> org.springframework.data.domain.page : count 쿼리 결과를 포함하는 페이징  
> org.springframework.data.domain.Slice : count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1 조회)
> List : count 쿼리 없이 결과만 반환
```
 - count query 분리
```
//countQuery를 별도 지정하지 않으면 Entity 연관관계로 인해 불필요한 Join 발생하여 성능에 영향을 줌
@Query( value = "select m from Member m left join fetch m.team where m.age = :age",
        countQuery = "select count(m.username) from Member m where m.age = :age")
Page<Member> findByAge(@Param("age") int age, Pageable pageable);
```
<br>


> 벌크성 수정
 - @Query 이용하여 처리함
 - 단, 반드시 @Modifying을 함께 사용하여야 함
```
@Modifying
@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
int bulkAgePlus(@Param("age") int age);
```
 - `주의사항` 벌크 쿼리는 JPA의 영속성 컨텍스트를 무시하고 직접 DB에 반영되므로 벌크 프로세스 처리 시 영속성 컨텍스트 초기화 해주어야 한다.
 - @Modifying의 clearAutomatically속성을 true로 설정
 - 또한 DB를 직접 핸들링 하는 경우(예: MyBatis, Jdbc template 등)에도 JPA와 함께 사용한다면 EntityManager 반영 및 초기화에 신경써야 한다.
```
@Modifying(clearAutomatically = true)
@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
int bulkAgePlus(@Param("age") int age);
```
<br>

> @EntityGraph  
 - Fetch join 어노테이션
 - attributePaths 속송에 fetch join 대상 정의
 - left join으로 제공되며 inner join 설정은 불가능
```
// 기존에 제공되는 메서드를 override 하여 적용
@Override
@EntityGraph(attributePaths = {"team"})
List<Member> findAll();

// @Query JPQL과 함께 적용
@Query("select m from Member m")
@EntityGraph(attributePaths = {"team"})
List<Member> findAllMembers();
```
<br>

> @NamedEntityGraph  
 - @NamedQuery와 마찬가지로 Entity에 정의 하여 사용하지만 실무에서는 많이 사용하지 않는다.
```
@NamedEntityGraph(name = "Member.findAllMembers", attributeNodes = @NamedAttributeNode("team"))
public class Member {
  ...(생략)...
}

@Query("select m from Member m")
@EntityGraph("Member.findAllMembers")
List<Member> findAllMembers();
```
<br>

> JPA hint
 - SQL hint가 아니라 JPA 구현체에게 제공하는 hint
   1. Readonly
      - JPA는 영속성 컨텍스트에 관리(after save or find)되는 Entity에 대해 `변경 감지` 기능을 제공한다.
      - flush 하는 시점에 변경된 부분에 대해 update 쿼리를 발생시킨다.
      - 이러한 기능을 제공하기 위해 비교할 수 있는 snapshot을 가지고 있는데, 이 또한 변경없이 조회만 하는 경우에는 불필요한 비용이며, 이때 사용되는 hint `org.hibernate.readOnly`
      - hibernate에서 제공하는 해당 hint를 사용하면 snapshot을 만들지 않는다.
      ```
      @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
      Optional<Member> findMemberReadonlyById(Long id);
      ```
   2. Lock
      - JPALOCK.md 별도 파일에 정리
<br>

> 사용자 정의 Repository 구현
  - JpaRepository를 상속받은 interface만으로 구현이 불가능할때, 예를 들어 JDBC TEMPLATE이나 QueryDSL을 사용
  - JpaRepository를 상속받은 interface를 이용하여 구현하기에는 관련 모든 메소드를 override 해야하므로 적합하지 않음
  - 별도 interface를 만들고 해당 interface를 대상으로 구현체를 생성한 후에 JpaRespository와 함께 상속받는 것으로 처리함
   `JPA와 무관하게 별도 스프링 repository로 구현해도 무방함. 성격에 맞추어 생성하여 사용하면 됨`
  
    ```
    // 사용자 interface 생성
    public interface MemberRepositoryCustom {
      List<Member> findAllCustom();
    }

    // 구현체 생성
    @RequiredArgsConstructor
    public class MemberRepositoryImpl implements MemberRepositoryCustom {
      private final EntityManager em;
      @Override
      public List<Member> findAllCustom() {
        return em.createQuery("select m From Member m left join fetch m.team", Member.class).getResultList();
      }
    }

    // 사용자 정의 interface 상속
    public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
      ...(중략)...
    }
    
    ```
<br>

>  Auditing
  - Entity 공통 field에 대한 처리
  - CreatedBy, CreatedDate, LastModifiedBy, LastModifiedDate
  - Entitiy가 상속받아야 사용하여야 하므로 @MappedSuperclass로 지정
    - 순수 JPA의 경우 @PrePersist, @PreUpdate 이용하여 구현함
    ```
    @MappedSuperclass
    @Getter
    public class JpaBaseEntity {
	
	    @Column(updatable = false)
	    private LocalDateTime createDate;
	    private LocalDateTime updateDate;
	
	    @PrePersist
	    public void prePersist() {
		    LocalDateTime now = LocalDateTime.now();
		    this.createDate = now;
		    this.updateDate = now;
	    }
	
	    @PreUpdate
	    public void preUpdate() {
		    LocalDateTime now = LocalDateTime.now();
		    this.updateDate = now;
	    }
	
    }
    ```
    - Spring Data JPA의 경우 @CreatedBy, @CreatedDate, @LastModifiedBy, @LastModifiedDate 이용하여 구현
    - Spring Boot 구동 Class에 @EnableJpaAuditing 설정 필요
    - Auditing Class에 @EntityListeners(AuditingEntityListener.class) 설정 필요
    ```
    @EntityListeners(AuditingEntityListener.class)
    @MappedSuperclass
    @Getter
    public class BaseTimeEntity {
	
	    @CreatedDate
	    @Column(updatable = false)
	    private LocalDateTime createDate;
	
	    @LastModifiedDate
	    private LocalDateTime lastModifiedDate;
	
    }

    @EntityListeners(AuditingEntityListener.class)

    @MappedSuperclass
    @Getter
    public class BaseEntity extends BaseTimeEntity{
	
        @CreatedBy
        @Column(updatable = false)
        private String createBy;
	
        @LastModifiedBy
        private String lastModifiedBy;
	
    }
    ```
    - @CreatedBy, @LastModifiedBy에 값을 담기 위해서는 AuditorAware Bean 등록하여야 함
    - 아래 예시는 단순 샘플이며, 프로젝트안에서 구현되어 있는 User 세션값이 설정될 수 있도록 처리하여야 함
    ```
    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> Optional.of(UUID.randomUUID().toString());
    }
    ```

<br>

> Pagination and Sort
- PAGINGSORT.md
<br>

> 그외 기능들
- Specifications (명세)
  - JPA Criteria를 활용하여 제공
    - 실무에서는 사용되지 않음 QueryDSL을 사용함, 아래 내용은 참고만 할 것
  - 명세 기능 사용법 : repository interface에서 org.springframework.data.jpa.repository.JpaSpecificationExecutor 상속
  
  ```
  public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom
      , JpaSpecificationExecutor<Member>{
  }
  ```
  
  - org.springframework.data.jpa.domain.Specification 클래스로 명세 정의
  - MemberSpec 명세 정의 코드
    
  ```
  public class MemberSpec {
	public static Specification<Member> teamName(final String teamName) {
		return (Specification<Member>) (root, query, builder) -> {
			if (!StringUtils.hasText(teamName)) {
				return null;
			}
			Join<Member, Team> t = root.join("team", JoinType.INNER); 
			return builder.equal(t.get("name"), teamName);
		};
	}

	public static Specification<Member> username(final String username) {
		// Specification interface 구현 toPredicate
		// 구현 메소드가 하나만 존재하면 아래와 같이 람다식으로 표현 가능
		return (Specification<Member>) (root, query, builder) -> builder.equal(root.get("username"), username);
	}
  }  
  ```

  ```
	@Test
	void specTest() {
		Team teamA = Team.createTeam().name("teamA").build();
		em.persist(teamA);

		Member member1 = Member.createMember().username("dikalee1").age(0).build();
		Member member2 = Member.createMember().username("dikalee2").age(0).build();
		// 팀설정
		member1.applyTeam(teamA);
		member2.applyTeam(teamA);

		em.persist(member1);
		em.persist(member2);

		em.flush();
		em.clear();

		Specification<Member> specUsername = MemberSpec.username("dikalee1");
		Specification<Member> specTeamname = MemberSpec.teamName("teamA");

		// username과 팀 name을 and 조건으로 검색
		List<Member> result = memberRepository.findAll(specUsername.and(specTeamname));

		assertThat(result.size()).isEqualTo(1);

	}
  ```
<br>

> Query By Example
- 장점
  - 동적 쿼리를 편리하게 처리
  - 도메인 객체를 그대로 사용
  - 데이터 저장소를 RDB에서 NOSQL로 변경해도 코드 변경이 없게 추상화 되어 있음
  - 스프링 데이터 JPA JpaRepository 인터페이스에 이미 포함
- 단점
  - 조인은 가능하지만 내부 조인(INNER JOIN)만 가능함 외부 조인(LEFT JOIN) 안됨
  - 다음과 같은 중첩 제약조건 안됨
    - `firstname = ?0 or (firstname = ?1 and lastname = ?2)`
  - 매칭 조건이 매우 단순함
    - 문자는 starts/contains/ends/regex
    - 다른 속성은 정확한 매칭( = )만 지원
- 실무에서 사용하기에는 매칭 조건이 너무 단순하고, LEFT 조인이 안됨
- 실무에서는 QueryDSL
      
  ```
	@Test
	void QueryByExampleTest() {
		Team teamA = Team.createTeam().name("teamA").build();
		em.persist(teamA);

		Member member1 = Member.createMember().username("dikalee1").age(0).build();
		Member member2 = Member.createMember().username("dikalee2").age(0).build();
		// 팀설정
		member1.applyTeam(teamA);
		member2.applyTeam(teamA);

		em.persist(member1);
		em.persist(member2);

		em.flush();
		em.clear();

		// Probe 생성
		// 필드에 데이터가 있는 실제 도메인 객체
		Member member = Member.createMember().username("dikalee1").age(0).build();
		Team team = Team.createTeam().name("teamA").build(); // 내부조인으로 teamA 가능
		member.applyTeam(team);

		// ExampleMatcher 생성, age 프로퍼티는 무시
		// 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

		// Example
		// Probe와 ExampleMatcher로 구성, 쿼리를 생성하는데 사용
		Example<Member> example = Example.of(member, matcher);

		List<Member> result = memberRepository.findAll(example);
		assertThat(result.size()).isEqualTo(1);
	}
  ```
<br>

> Projections
- 엔티티 대신에 DTO를 편리하게 조회할 때 사용
- 인터페이스 기반 Closed Projections
- 프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공
- SpEL문법을 사용하면, DB에서 엔티티 필드를 다 조회해온 다음에 계산, 따라서 JPQL SELECT 절 최적화가 안된다.

  ```
  public interface UsernameOnly {
    @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")  //스프링의 SpEL 문법도 지원
    String getUsername();
    String getAge();
  }

  public interface MemberRepository ... {
   List<UsernameOnly> findProjectionsByUsername(String username);
  }

	@Test
	public void projections() throws Exception {
		Team teamA = Team.createTeam().name("teamA").build();
		em.persist(teamA);

		Member member1 = Member.createMember().username("dikaleeA").age(0).build();
		Member member2 = Member.createMember().username("dikaleeB").age(0).build();
		// 팀설정
		member1.applyTeam(teamA);
		member2.applyTeam(teamA);

		em.persist(member1);
		em.persist(member2);

		em.flush();
		em.clear();

		List<UsernameOnly> result = memberRepository.findProjectionsByUsername("dikaleeB");
		
		result.forEach(t -> {
			System.out.println(t.getUsername());
			System.out.println(t.getAge());
		});
		
		assertThat(result.size()).isEqualTo(1);
	}

  ```

- 클래스 기반 Projection
  - 인터페이스가 아닌 구체적인 DTO 형식도 가능
  - 생성자의 파라미터 이름으로 매칭

  ```
  public class UsernameOnlyDto {
   private final String username;
   public UsernameOnlyDto(String username) {
     this.username = username;
   }
   public String getUsername() {
     return username;
   }
  }  
  ```

- 동적 Projections
  - Generic type을 주면, 동적으로 프로젝션 데이터 번경 가능

  ```
  <T> List<T> findProjectionsByUsername(String username, Class<T> type);

  // 사용코드
  List<UsernameOnly> result = memberRepository.findProjectionsByUsername("dikaleeB", UsernameOnly.class);
  ```

- 중첩 구조 처리

  ```
  public interface NestedClosedProjection {
   String getUsername();
   TeamInfo getTeam();
   interface TeamInfo {
     String getName();
   }
  }
  ```
  
  - 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
  - 프로젝션 대상이 ROOT가 아니면 LEFT OUTER JOIN 처리, 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
  - 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안됨
  - 실무의 복잡한 쿼리를 해결하기에는 한계
  -  QueryDSL을 사용하자
