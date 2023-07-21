# QueryDsl

> 관련 Gradle 설정 추가사항
- build.gradle 설정에 QueryDsl 관련하여 추가함
  - buildsript 추가

  ```
  buildscript {
     ext {
        queryDslVersion = "5.0.0"
     }
  }
  ```

  - plugins 추가

  ```
  plugins {
     ...(생략)...
     id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
     ...(생략)...
  }
  ```

  - dependencies 추가

  ```
  dependencies {
     ...(생략)...
	  implementation "com.querydsl:querydsl-jpa:${queryDslVersion}"
    implementation "com.querydsl:querydsl-apt:${queryDslVersion}"
     ...(생략)...
  }
  ```

  - 빌드정보 추가

  ```
  def querydslDir = "$buildDir/generated/querydsl"

  querydsl {
     jpa = true
     querydslSourcesDir = querydslDir
  }
  sourceSets {
     main.java.srcDir querydslDir
  }
  compileQuerydsl{
     options.annotationProcessorPath = configurations.querydsl
  }
  configurations {
     compileOnly {
        extendsFrom annotationProcessor
     }
     querydsl.extendsFrom compileClasspath
  }
  ```

  - 빌드시 테스트 제외

  ```
  test {
    exclude '**/*'
  }
  ```
<br>

> Q클래스 인스턴스 사용법
- 별칭 직접 지정 방식은 같은 테이블을 JOIN 해야 하는 경우에만 쓰이
- 2번 인스턴스 방식으로 사용하면 됨
  
  ```
  // 1. 별칭 직접 지정
  QMember qMember = new QMember("m");

  // 2. 기본 인스턴스 사용 => static import를 사용하면 좀더 깔끔하게 사용할 수 있
  QMember qMember = QMember.member; 
  ```

<br>

> 기본 select

  ```
    //import 사용 
    import myone.datajpa.entity.QMember;

	@Test
	public void basicSelectExam() {
		String strUsername = "querydsl1_0000";
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		QMember qMember = QMember.member;
		Member findMember = queryFactory.selectFrom(qMember)
				.where(qMember.username.eq(strUsername))
				.fetchOne();
		assertThat(findMember.getUsername()).isEqualTo(strUsername);
	}

    //static import 사용 
    import static myone.datajpa.entity.QMember.member;

	@Test
	public void basicSelectExam() {
		String strUsername = "querydsl1_0000";
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		Member findMember = queryFactory.selectFrom(member)
				.where(member.username.eq(strUsername))
				.fetchOne();
		assertThat(findMember.getUsername()).isEqualTo(strUsername);
	}
  ```

- 조건문 JPAQueryFactory의 where안에 체인형태로 지정

  ```
  // username이 "querydsl1_0000"인 조건문
  Member findMember = queryFactory.selectFrom(member)
				.where(member.username.eq("querydsl1_0000"))
				.fetchOne();

  // username이 "querydsl1_0000"이고 age가 10인 조건문
  where(member.username.eq("querydsl1_0000").and(member.age.eq(10)))

  // and 조건만 존재한다면 where에 `Predicate... o` 방식의 parameter로 넘겨도 됨
  where(member.username.eq("querydsl1_0000"), member.age.eq(10))
  ```

- 검색조건
    
|검색조건|예시|실행|
|------|---|---|
|eq|member.username.eq("member1")|username = 'member1'|
|ne|member.username.ne("member1")|username != 'member1'|
|eq not()|member.username.eq("member1").not()|username != 'member1'|
|isNotNull|member.username.isNotNull()|username is not null|		
|in|member.age.in(10, 20)|age in (10,20)|		
|notIn|member.age.notIn(10, 20)|age not in (10, 20)|
|between|member.age.between(10,30)|between 10, 30|
|goe|member.age.goe(30)|age >= 30|
|gt|member.age.gt(30)|age > 30|
|loe|member.age.loe(30)|age <= 30|
|lt|member.age.lt(30)|age < 30|
|like|member.username.like("member%")|like 'member%' 검색|
|contains|member.username.contains("member")|like '%member%' 검색|
|startsWith|member.username.startsWith("member")|like 'member%' 검색|
  
