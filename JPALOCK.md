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
