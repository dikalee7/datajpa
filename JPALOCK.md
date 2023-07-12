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
