# 콘텐츠 통합 관리 API
- 서비스

## 사용 기술

- JDK 17
- Kotlin
    - Kotlin Coroutine
- Spring Boot
    - WebFlux
    - Docs Openapi
- Test
    - junit5
    - mockk
    - kluent
- Datasource
    - MongoDB
    - Redis
        - redisson
            - DB와 WRITE_BEHIND 동기화

##  프로젝트 구성

### 유의사항
M1의 경우 build.gradle.kts dependency 에 추가
(현재 추가 상태)
```
runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.77.Final:osx-aarch_64")
```

IDE Ktlint 설정
![img.png](img.png)
### Swagger API 테스트 경로

```
http://localhost:8080/swagger-ui.html
```

### 빌드 정보
- root에서 실행
```
./gradlew :application:api:clean :application:api:bootJar -x test 
```