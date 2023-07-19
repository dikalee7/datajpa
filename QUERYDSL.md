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
  
