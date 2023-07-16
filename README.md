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
  - 때문에, @GeneratedValue의 경우에는 무관하나 직접 키값을 정의해야하는 Entity의 경우에는  org.springframework.data.domain.Persistable 인터페이스를 상속받아 `override메소드 isNew`를 재정의해주어야 함 

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
