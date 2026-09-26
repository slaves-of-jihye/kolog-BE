# AGENTS.md — kolog-backend

이 문서는 백엔드 아키텍처 규칙과 지금까지의 작업에서 확정된, **반드시 유지해야 할** 결정 사항을 기록한다. 새 코드를 작성하거나 기존 코드를 고칠 때 먼저 이 문서를 확인한다.

## 구조 (도메인별 계층)

```
domains/<domain>/
  domain/                     순수 도메인 레코드 (User, Log). 프레임워크 의존 없음.
  application/
    external/                아웃바운드 포트(인터페이스): Repository, FileStorage, TokenProvider, Validator 등
      dto/                   포트 경계에서 오가는 DTO (UserDetail, RefreshToken)
    usecase/<concern>/       유스케이스를 "이름"이 아니라 "관심사 그룹"으로 묶는다 (crud, auth ...). 그룹 안에는
                             관련 유스케이스 클래스를 flat하게 둔다 (예: crud/LogCreateCase, LogGetUseCase,
                             LogListUseCase, LogUpdateUseCase가 모두 같은 crud/ 아래).
      dto/request/           그 그룹의 요청 DTO. 요청/응답이 둘 다 있으면 request/response로 나눈다.
      dto/response/          그 그룹의 응답 DTO. 요청 없이 응답 DTO 하나뿐이면 굳이 response/ 하위로 안 나누고
                             dto/ 바로 아래 둬도 된다 (예: user/crud/dto/UserResponse.java).
    exception/               도메인 전용 RuntimeException. HTTP 상태 모름 (아래 "예외 처리" 참조)
  infrastructure/
    adapter/                 application/external 포트의 구현체 (Repository/Storage/TokenProvider 구현)
    jpa/                     JPA 엔티티 + Spring Data 리포지토리 + 정적 Mapper (엔티티 <-> 도메인 레코드 변환)
  web/
    controller/              REST 컨트롤러. 얇게 유지, 유스케이스에 바로 위임
    controller/dto/           웹 계층 요청 DTO. 여기서 application DTO로 변환(변환 중 도메인 예외 던질 수 있음)
    adviser/                 컨트롤러 전용 @RestControllerAdvice (아래 "예외 처리" 참조)

global/
  config/
    web/                     WebMvcConfigurer 등 전역 웹 설정 (정적 리소스 서빙 등)
    security/                SecurityConfig + filter/ (BearerTokenFilter). 컨트롤러 진입 전 인증 실패는 여기서만 처리
```

> 과거에는 `usecase/logCreate/`, `usecase/logGet/`, `usecase/logList/`처럼 **유스케이스 하나당 패키지 하나**였다.
> 이후 `crud`(생성/조회/목록/수정), `auth`(로그인/가입) 같은 **관심사 단위 패키지**로 재편했다 — 관련 유스케이스가
> 늘어날수록 패키지가 무한히 갈라지는 것을 막고, 같은 그룹 안에서 `LogResponse.from(log)`처럼 응답 조립 로직을
> 공유하기 쉽게 하기 위함이다. 새 유스케이스를 추가할 때 그 도메인에 이미 있는 관심사 그룹(crud/auth 등)에
> 속하는지 먼저 확인하고, 없으면 새 그룹을 만들되 유스케이스 이름 그대로의 1:1 패키지는 만들지 않는다.

## 지켜야 할 패턴

### 1. 예외 처리 — application은 HTTP를 모른다
- 도메인 예외는 **`RuntimeException`을 직접 상속**한다. 공통 `ApiException`/`BadRequestException` 같은 상태코드 인지 상위 클래스를 **두지 않는다** (한 번 만들었다가 명시적으로 제거된 이력 있음).
- 상태 코드 매핑은 오직 `web/adviser`의 도메인별 `@RestControllerAdvice(assignableTypes = XController.class)`에서, 예외 타입 하나하나를 `@ExceptionHandler`로 명시해 `ProblemDetail`로 응답한다.
- **전역 `@RestControllerAdvice`는 쓰지 않는다.** 컨트롤러(도메인)마다 자신의 adviser를 가진다.
- 컨트롤러 진입 전(Spring Security 필터 단계)에서 발생하는 인증 실패(토큰 없음/무효)는 도메인 adviser가 아니라 `SecurityConfig`/`BearerTokenFilter`에서 `response.sendError(...)`로 직접 처리한다. 이 부분만 예외적으로 도메인 계층 밖에서 처리된다.
- IOException 등 체크 예외는 컨트롤러에서 try/catch 하지 않는다. 웹 DTO의 변환 메서드(`toApplication` 등) 또는 유스케이스가 스트림 수명주기를 갖고 도메인 예외로 변환한다.

### 2. 설정값(`@Value`) — 비어 있으면 기동 자체가 실패해야 한다
- 모든 `@Value` 주입 값은 생성자 또는 `@PostConstruct`에서 `null`/blank(그 외 유효성: URL 형식, 최소 길이 등)를 검증하고 `IllegalArgumentException`/`IllegalStateException`을 던진다.
- 기본값으로 조용히 넘어가지 않는다. Spring 컨텍스트 생성 실패 → 컨테이너/앱이 뜨지 않는 것이 목표다.
- 현재 대상: `file.upload-dir`, `file.server-url` (`VideoFileStorage`, `ResourcesConfig`), `jwt.secret.access-token`/`jwt.secret.refresh-token` (`JwtAuthTokenProvider`). 새 `@Value`를 추가하면 같은 패턴을 따른다.

### 3. DTO — Lombok `@Builder` 필수
- 모든 요청/응답 DTO(record)는 `@Builder`를 붙이고 `X.builder()....build()`로만 생성한다. 위치 기반 생성자(`new X(a, b, c)`) 직접 호출 금지.
- 서비스/컴포넌트는 `@RequiredArgsConstructor` + `private final` 필드로 의존성 주입한다.

### 4. 인증 주체(`UserDetail`)는 확장 가능하게 통째로 들고 다닌다
- `BearerTokenFilter`가 JWT를 검증해 만든 `UserDetail`을 컨트롤러 → 웹 DTO → 유스케이스 요청 DTO까지 **그대로 전달**한다. 중간에 `userId()`만 미리 꺼내 넘기지 않는다.
- 리포지토리 조회처럼 원시값이 꼭 필요한 지점에서만 `.userId()`를 꺼낸다. 이후 `UserDetail`에 필드가 늘어나도 중간 계층을 고칠 필요가 없어야 한다.
- JWT 클레임 파싱은 `Claims` 객체를 먼저 받고 나서 필요한 필드를 `UserDetail.builder()`에 명시적으로 매핑한다 (`JwtAuthTokenProvider.accessTokenUserDetail`).

### 5. 비밀번호
- Spring Security `BCryptPasswordEncoder`만 사용한다. salt는 인코딩 결과 문자열에 자동 포함되므로 별도 시드/키를 주입하지 않는다.

### 6. 목록 조회 파라미터
- 선택적 쿼리 파라미터 조합이 의미상 애매하면(예: `hour`만 있고 `date`는 없음) **조용히 기본값을 채우지 말고 400으로 명시 거부**한다 (`InvalidLogQueryException` 참고).
- 모든 목록 응답은 정렬 기준을 명시적으로 정하고 유스케이스에서 정렬한다 (예: date → hour).

### 7. Repository 포트
- `application/external`의 Repository 인터페이스는 유스케이스가 실제로 쓰는 메서드만 최소로 둔다. 새 조회가 필요하면 포트에 메서드를 추가하고, `infrastructure/jpa`에 Spring Data derived query, `infrastructure/adapter`에서 정적 `Mapper`로 도메인 레코드 변환까지 구현한다.

### 8. 도메인 간에 겹치는 응답 모양은 소유 도메인의 DTO를 재사용한다
- 다른 도메인 리소스를 요약해서 보여줘야 할 때(예: 로그 응답 안의 업로더 정보) 그 도메인 전용으로 축약 DTO를 새로 만들지 않고, 소유 도메인의 기존 응답 DTO를 그대로 참조한다 (`LogResponse.uploader`가 `user` 도메인의 `UserResponse`를 그대로 씀. 예전엔 `log` 쪽에 `LogUploaderResponse`를 중복 정의했었다 — 제거함).
- 같은 그룹(`crud` 등) 안에서 생성/조회/목록/수정이 응답을 조립하는 로직이 겹치면 응답 DTO에 `XResponse.from(domain)` 정적 팩토리를 두고 재사용한다. 유스케이스 안에서 `builder()`를 직접 필드별로 채우는 코드를 복붙하지 않는다.

### 9. API가 바뀌면 `docs/api-spec.yaml`을 같은 작업 안에서 갱신한다
- 엔드포인트를 추가/변경/삭제하면(경로, 메서드, 요청/응답 바디, 상태 코드) **그 자리에서** `docs/api-spec.yaml`도 함께 고친다. 나중으로 미루지 않는다.
- 실제로 구현되지 않았거나 삭제된 엔드포인트를 문서에만 남겨두지 않는다 — 문서와 컨트롤러가 어긋나면 문서 쪽을 실제 코드에 맞춰 고친다.
- 예외: `Chat`/`Emotion` 태그의 `/video/chat`, `/video/{logId}/chat`, `/video/emotion`은 **아직 구현 전이지만 곧 만들 기능이라 의도적으로 남겨둔 자리표시자**다. 실제 구현 전까지는 지우지 않는다.

## 피해야 할 안티패턴

- **공통 HTTP 예외 계층 부활 금지**: `global/exception` 같은 패키지에 상태코드 아는 부모 예외를 다시 만들지 않는다.
- **전역 `@ControllerAdvice`로 되돌리지 않는다**: 컨트롤러별 `assignableTypes` 유지.
- **애매한 파라미터 조합에 기본값 은닉 금지**: 명시적 400이 낫다.
- **`ddl-auto=update`를 맹신하지 않는다**: 과거 엔티티 필드명 변경(`term`→`hour`)이 실제 DB 컬럼에 반영되지 않아 로그 관련 기능 전체가 500으로 죽은 사고가 있었다. 필드/컬럼명을 바꾸면 개발 DB 스키마도 함께 확인·마이그레이션해야 한다.
- **시크릿/설정 기본값 은닉 금지**: 비어 있으면 반드시 기동 실패.
- **컨트롤러에서 체크 예외 try/catch 금지**: DTO 변환부/유스케이스로 위임.
- **인터페이스·DTO에 여러 멤버가 있는 파일을 라인 범위로 수정할 때 기존 멤버를 실수로 지우기 쉽다** — 특히 `save()` 같은 기존 메서드를 새 메서드 추가 편집 중 통째로 날린 사고가 두 번 있었다. 범위 수정 후 반드시 전체 파일을 다시 읽어 확인한다.
- **API 문서(`docs/api-spec.yaml`)는 자동 생성되지 않는다**: 규칙 9 참고. 현재 남아있는 미구현 초안은 `/users/{userId}/profile`, 스키마 `LogGetListResponse`/`LogGetByHourResponse`/`LogGetByHourListResponse` 뿐이며, `Chat`/`Emotion` 관련 경로는 곧 구현할 자리표시자다. 문서를 그대로 신뢰하지 말고 실제 컨트롤러 코드를 기준으로 판단한다.
- **유스케이스 이름 그대로 1:1 패키지(`logCreate/`, `logGet/`, `logUpdate/` ...) 만들지 않는다**: `crud`/`auth` 같은 관심사 그룹 아래 flat하게 둔다.
- **다른 도메인 응답을 요약하려고 도메인별 축약 DTO(`LogUploaderResponse` 같은) 중복 정의 금지**: 소유 도메인의 응답 DTO를 참조한다.

## 운영/검증 메모

- 로컬 컨테이너: `docker compose --env-file external/env/.env -f external/build/docker-compose.yml up -d --build`. 백엔드 코드를 고치면 반드시 재빌드 후 실제로 컨테이너가 최신 상태인지 확인하고 나서 "적용됨"이라 말한다.
- 동작 검증은 실제 HTTP 스모크 테스트(가입→토큰→호출)로 하고, 만든 임시 계정/로그/업로드 파일은 검증 후 삭제한다.
- 명시적 요청이 없으면 새 테스트 파일을 만들지 않는다. 회귀 검증이 필요하면 일회성 스크립트나 기존 자동화 테스트 스위트(`./gradlew test`)로 확인한다.
