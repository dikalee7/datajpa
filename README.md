# datajpa
study spring data jpa  

`- gradle 의존관계 보기`  
 - ./gradlew dependencies --configuration compileClasspath  

`- Assertj`  
 - https://joel-costigliola.github.io/assertj/index.html

`- JPA Entity에 기본 생성자가 필요한 이유` 
 - 데이터를 DB에서 조회해 온 뒤 객체를 생성할 때 Java Reflection을 사용
 - Reflection은 클래스 이름만 알면 생성자, 필드, 메서드 등 클래스의 모든 정보에 접근이 가능 하지만 생성자의 매개변수는 알 수 없기때문에 기본 생성자로 객체를 생성하고 필드 값을 강제로 매핑해주는 방식을 사용

`- JPA Entity에 기본 생성자는 protected로 설정`   
 - Entity 객체만 생각하면 private로 설정해도 문제 없으나 지연로딩과 그를 위한 Proxy 객체를 고려하면 protected로 설정하여 Entity 객체를 상속받는 Proxy 객체에서 부모의 생성자로 접근이 가능하도록 하여야 함  

`- JpaRepository`  
 - JpaRepository를 상속받은 interface는 Spring Data Jpa가 Proxy 객체로 생성하여 내부적으로 구현체를 생성함  
