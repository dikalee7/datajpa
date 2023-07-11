# Spring Data Jpa 


> gradle 의존관계 보기  
> ./gradlew dependencies --configuration compileClasspath    
<br>

> Assertj  
> https://joel-costigliola.github.io/assertj/index.html  
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
