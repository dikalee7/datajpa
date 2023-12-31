# Jpa Lock 

|락 모드|타입|설명|
|------|---|---|
|낙관적 락|OPTIMISTIC|낙관적 락을 사용한다.|
|낙관적 락|OPTIMISTIC_FORCE_INCREMENT|낙관적 락 + 버전정보를 강제로 증가한다.	
|비관적 락|PESSIMISTIC_READ|비관적 락, 읽기 락을 사용한다.|
|비관적 락|PESSIMISTIC_WRITE|비관적 락, 쓰기 락을 사용한다.|		
|비관적 락|PESSIMISTIC_FORCE_INCREMENT|비관적 락 + 버전 정보를 강제로 증가한다.|		
|기타|NONE|락을 걸지 않는다.|
|기타|READ|JPA 1.0 호환 기능으로 OPTIMISTIC과 동일|
|기타|WRITE|JPA 1.0 호환 기능으로 OPTIMISTIC_FORCE_INCREMENT와 동일|
<br>

> 낙관적 락
- @Version 사용
- None
  - 락 옵션을 적용하지 않아도 Entity에 @Verwsion이 적용된 필드가 있으면 낙관적 락이 적용됨
  ```
  @Entity
  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public class Member {
    @Version
    private int version;
    ...(중략)...
  }
  ```
  - 엔티티를 수정할 때 버전을 증가, 이때 데이터베이스의 버전 값이 현재 버전이 아니면 예외 발생
- OPTIMISTIC
  - @Version만 적용했을 때는 엔티티를 수정해야 버전을 체크하지만 이 옵션을 추가하면 엔티티를 조회만 해도 버전 체크함
  - 조회한 엔티티는 트랜잭션이 끝날 때까지 다른 트랜잭션에 의해 변경되지 않아야 한다. 조회 시점부터 트랜잭션이 끝날 때까지 조회한 엔티티가 변경되지 않음을 보장
  ```
  @Query("select m from Member m")
  @Lock(LockModeType.OPTIMISTIC)
  Optional<Member> findByIdForUpdate(Long id);
  ```

- OPTIMISTIC_FORCEJNCREMENT
  - Entity를 수정하지 않아도 버전을 강제로 증가시킴
  - 연관관계에 있는 다른곳이 수정되었어도 해당 Entity가 버전업 되어야 한다면 사용
  ```
  @Query("select m from Member m")
  @Lock(LockModeType.OPTIMISTIC_FORCEJNCREMENT)
  Optional<Member> findByIdForUpdate(Long id);
  ```
<br>

> 비관적 락
- 데이터베이스 트랜잭션 락 메커니즘에 의존하는 방식
- PESSIMICTIC_WRITE
  - 데이터베이스에 쓰기 락
  - 데이터베이스 select for update를 사용
  ```
  @Query("select m from Member m")
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Member> findByIdForUpdate(Long id);

  //호출되는 select문에 for update 붙음
  select
        member0_.member_id as member_i1_0_,
        member0_.age as age2_0_,
        member0_.team_id as team_id5_0_,
        member0_.username as username3_0_,
        member0_.version as version4_0_ 
  from
        member member0_ for update
            of member0_
  ```
- PESSIMICTIC_READ
  - 데이터를 반복 읽기만 하고 수정하지 않는 용도
  - 일반적으로 사용되지 않음
  - 데이터베이스 대부분 방언에 의해 PESSIMISTIC_WRITE로 동작
- PESSIMISTIC_FORCE_INCREMENT
  - 비관적 락중 유일하게 버전 정보를 사용
  - 때문에 Entity에 version을 위한 필드를 (@Version) 두지 않으면 오류 발생
    - org.hibernate.AssertionFailure: cannot force version increment on non-versioned entity
- 타임 아웃
  - 비관적 락 사용 시 락을 위한 트랜잭션 무한 대기를 방지하기 위해 설정
  ```
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="10000")})
  Optional<Member> findForUpdateById(Long id);
  ```
