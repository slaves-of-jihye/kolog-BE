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
  storage/                   도메인 무관 공용 파일 저장 헬퍼 (LocalFileStorageSupport). 도메인별 어댑터가 구성해서 씀
```

> 과거에는 `usecase/logCreate/`, `usecase/logGet/`, `usecase/logList/`처럼 **유스케이스 하나당 패키지 하나**였다.
> 이후 `crud`(생성/조회/목록/수정), `auth`(로그인/가입) 같은 **관심사 단위 패키지**로 재편했다 — 관련 유스케이스가
> 늘어날수록 패키지가 무한히 갈라지는 것을 막고, 같은 그룹 안에서 `LogResponse.from(log)`처럼 응답 조립 로직을
> 공유하기 쉽게 하기 위함이다. 새 유스케이스를 추가할 때 그 도메인에 이미 있는 관심사 그룹(crud/auth 등)에
> 속하는지 먼저 확인하고, 없으면 새 그룹을 만들되 유스케이스 이름 그대로의 1:1 패키지는 만들지 않는다.

> `Comment`(로그 댓글)는 `domains/comment`라는 **별도 최상위 도메인**으로 만든다 — `Chat`을 한 번
> `domains/log` 안에 합쳐서 만들었다가 구조를 다시 잡으려고 전부 걷어낸 적이 있다(관련 기록은 본 문서
> 히스토리에서 삭제됨). 지금은 "로그의 하위 리소스라고 무조건 log 도메인에 합친다"는 규칙을 두지 않는다 —
> 별다른 지시가 없으면 새 리소스는 각자 자기 도메인으로 만든다. `domains/comment`도 `domain/`,
> `application/{external,exception,usecase/crud/dto/{request,response}}`, `infrastructure/{jpa,adapter}`,
> `web/{controller/dto,adviser}` 구조를 그대로 따른다.

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

### 3. DTO/도메인 레코드 — Lombok `@Builder` 필수
- 모든 요청/응답 DTO와 `domain/`의 도메인 레코드(`User`, `Log`, `Comment`)는 `@Builder`를 붙이고 `X.builder()....build()`로만 생성한다. 위치 기반 생성자(`new X(a, b, c)`) 직접 호출 금지.
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
- 다른 도메인 리소스를 요약해서 보여줘야 할 때(예: 로그 응답 안의 업로더 정보) 그 도메인 전용으로 축약 DTO를 새로 만들지 않고, 소유 도메인의 기존 응답 DTO를 그대로 참조한다 (`LogResponse.uploader`가 `user` 도메인의 `UserResponse`를 그대로 씀. 예전엔 `log` 쪽에 `LogUploaderResponse`를 중복 정의했었다 — 제거함). `UserResponse.from(User)` 정적 팩토리가 있으니 `LogResponse`/`CommentResponse` 등 다른 도메인에서도 이걸로 조립하고, `.builder()...build()`를 각자 또 베껴 쓰지 않는다.
- 같은 그룹(`crud` 등) 안에서 생성/조회/목록/수정이 응답을 조립하는 로직이 겹치면 응답 DTO에 `XResponse.from(domain)` 정적 팩토리를 두고 재사용한다. 유스케이스 안에서 `builder()`를 직접 필드별로 채우는 코드를 복붙하지 않는다.

### 9. API가 바뀌면 `docs/api-spec.yaml`을 같은 작업 안에서 갱신한다
- 엔드포인트를 추가/변경/삭제하면(경로, 메서드, 요청/응답 바디, 상태 코드) **그 자리에서** `docs/api-spec.yaml`도 함께 고친다. 나중으로 미루지 않는다.
- 실제로 구현되지 않았거나 삭제된 엔드포인트를 문서에만 남겨두지 않는다 — 문서와 컨트롤러가 어긋나면 문서 쪽을 실제 코드에 맞춰 고친다.
- 지금은 문서에만 있고 실제로 구현 안 된 엔드포인트를 의도적으로 남겨두는 경우가 없다 —
  `Chat`/`Emotion` 둘 다 자리표시자였다가 `Comment`/`Emotion`으로 실제 구현되면서 정리됐다.
  새로 그런 자리표시자를 남기게 되면 여기 그 이유와 태그를 적어 둔다.

### 10. `Comment`/`Emotion`은 작성자를 `user` 도메인의 `User`를 그대로 소유한다 — 전용 축약 타입을 만들지 않는다
- `Comment(id, content, logId, author: User)`, `Emotion(id, content, logId, author: User)` — 처음엔
  `CommentAuthor(id, nickname, profileImageUrl)`라는 comment 전용 값 객체를 도메인 레코드에 뒀었는데, 이건
  규칙 8이 응답 DTO에 대해 말하는 것과 같은 이유로 도메인 레벨에서도 **잘못된 중복**이었다 — `user` 도메인의
  `User`를 그대로 쓰면 되는데 굳이 부분집합 타입을 또 만든 것. 지금은 `CommentMapper`/`EmotionMapper`의
  `toDomain`이 `UserMapper.toDomain(entity.getAuthor())`로 `User`를 그대로 만들어 넣는다. `Emotion`을 다시
  만들 때도 이 패턴을 그대로 따랐다 — 전용 축약 타입을 또 만들지 않는다.
- **전용 축약은 DTO(응답) 단에서만 필요하면 만든다** — 여기서는 그마저도 필요 없다: `CommentResponse.author`/
  `EmotionResponse.author`는 `user` 도메인의 기존 `UserResponse`를 그대로 쓴다(`UserResponse.from(comment.author())`).
  `LogResponse.uploader`도 마찬가지로 `UserResponse`를 재사용한다.
- `CommentJpaEntity.author`/`EmotionJpaEntity.author`가 `FetchType.EAGER`라서 각 `Mapper.toDomain`이 매핑
  시점에 `User`를 바로 채워 넣을 수 있고, 그래서 응답을 만들 때 유스케이스가 별도로 `UserRepository`를
  조회할 필요가 없다 — 처음 Chat을 만들 때는 `authorId`만 갖고 다니다가 응답 조립 단계에서
  `UserRepository.findAllById`로 다시 채워 넣었는데, 그 왕복을 없앤 결정이다.
- **`CommentResponse`/`EmotionResponse`는 `logId`를 절대 담지 않는다** — 댓글/감정표현은 항상 로그 응답 안
  (`LogResponse.comments`/`LogResponse.emotions`)처럼 로그 문맥 안에서만 노출되므로, 이미 아는 로그 id를
  매번 반복해서 돌려주지 않는다. 댓글과 마찬가지로 감정표현도 단독 목록 조회 API는 만들지 않는다 — 로그를
  조회하면 필드로 같이 오므로 중복이다.
- **`Log` 도메인 레코드가 자기 댓글/감정표현을 직접 소유한다** (`Log(id, videoUrl, caption, date, hour, uploader,
  comments: List<Comment>, emotions: List<Emotion>)`) — 응답 조립 전용 별도 컴포넌트(`LogResponseFactory` 같은)를
  두지 않는다. `LogRepositoryAdapter`(log 도메인의 infra 계층)가 `CommentJpaRepository`/`CommentMapper`,
  `EmotionJpaRepository`/`EmotionMapper`(각각 comment/emotion 도메인)를 직접 주입받아, `Log`를 만들 때마다
  둘 다 채워 넣는다. 그래서 `LogResponse.from(log)`는 순수 정적 매핑으로 끝난다(`log.comments()`/`log.emotions()`를
  그대로 `CommentResponse::from`/`EmotionResponse::from`으로 변환) — `LogCreateCase`/`LogGetUseCase`/
  `LogListUseCase`/`LogUpdateUseCase`는 리포지토리 하나(`LogRepository`)만 갖고도 댓글·감정표현까지 채워진
  응답을 만든다. 도메인 레코드가 다른 도메인 레코드(`Comment`, `Emotion`)를 직접 참조하는 것도, infra 계층이
  다른 도메인의 JPA 리포지토리를 직접 참조하는 것도 허용된 이동이다.
- **로그를 지울 땐 그 로그의 댓글·감정표현부터 지운다** — `comments.log_id`/`emotions.log_id` FK에
  `ON DELETE CASCADE`가 없어서, 자식 행이 하나라도 달린 로그를 그냥 `logs.deleteById(id)`만 호출하면
  Hibernate가 flush 시점에 `TransientPropertyValueException`을 던지며 500이 난다(실제로 겪은 사고).
  `LogRepositoryAdapter.deleteById`가 `CommentJpaRepository.deleteByLog_Id(id)`와
  `EmotionJpaRepository.deleteByLog_Id(id)`로 자식부터 지운 다음 `logs.deleteById(id)`를 부른다.
- `Emotion`은 한 유저가 같은 로그에 감정표현을 두 번 남길 수 없다 — `EmotionJpaEntity`에 `(log_id, user_id)`
  유니크 제약을 걸고, `EmotionCreateUseCase`가 저장 전에 `existsByLogIdAndAuthorId`로 먼저 확인해 409로
  거부한다.
- **JPA 엔티티/DTO 필드명은 도메인 레코드 필드명과 그대로 맞춘다** — 예전엔 `LogJpaEntity`가 관계 필드를
  `user`라 부르고 도메인 `Log.uploader`와 이름이 어긋나 있었는데, `uploader`로 맞췄다(`LogMapper`도 함께 수정).
  같은 이유로 `Emotion`의 감정표현 값 필드도 원래 `emotionId`였다가 `Comment.content`와 짝을 맞춰 `content`로
  통일했다(도메인 레코드/`EmotionJpaEntity`/`EmotionCreateRequest`/`EmotionCreateWebRequest`/`EmotionResponse`/
  `EmotionMapper`/`EmotionCreateUseCase`/예외 메시지/`docs/api-spec.yaml` 전부 동시에 고쳐야 한다).

### 11. 인증은 화이트리스트 방식 — signup/login/refresh + 정적 리소스만 예외, 나머지는 전부 `authenticated()`
- `SecurityConfig.authorizeHttpRequests`는 엔드포인트를 하나씩 나열해 `authenticated()`를 붙이지 않는다.
  `POST /api/v1/users/signup`, `POST /api/v1/users/login`, `POST /api/v1/users/refresh`,
  `GET /resources/**`만 `permitAll()`이고 나머지는 `anyRequest().authenticated()`다. `refresh`가 여기 껴
  있는 이유: `BearerTokenFilter`는 액세스 토큰만 이해하므로, 액세스 토큰이 만료돼 재발급받으러 온 요청에
  `authenticated()`를 걸면 애초에 재발급이 불가능해진다. `GET /resources/**`(업로드된 영상/이미지 정적 파일,
  `ResourcesConfig` 참고)가 여기 껴 있는 이유: 응답 JSON에 담겨 나가는 `videoUrl`/`profileImageUrl`은
  `<video>`/`<img>` 태그나 별도 다운로드로 인증 헤더 없이 바로 접근되는 게 정상이다 — **이 화이트리스트를
  처음 도입했을 때(규칙 도입 커밋) 이걸 빠뜨려서 모든 업로드 파일이 401로 막히는 회귀가 있었고, PATCH
  /users/me의 프로필 이미지 URL을 실제로 fetch해보는 스모크 테스트에서야 발견됐다** — 새 정적/공개 리소스
  경로를 추가할 때 반드시 인증 없이 실제로 fetch까지 해서 확인한다(JSON 필드에 URL이 들어있다고 끝난 게
  아니다). 새 컨트롤러/엔드포인트를 추가해도 `SecurityConfig`를 따로 안 건드려야 기본적으로 인증이 걸린다
  — 공개 API가 필요하면 이 목록 옆에 명시적으로 추가한다.
- CORS 허용 origin은 `APP_CORS_ALLOWED_ORIGINS` 환경변수(`app.cors.allowed-origins` 프로퍼티)로 설정한다.
  콤마로 여러 개 지정 가능(`http://localhost:3000,https://kolog.example.com`), `SecurityConfig`의
  `parseAllowedOrigins`가 트림·공백 제거 후 `setAllowedOriginPatterns`에 넣는다. 하드코딩된 `"*"` 패턴을
  쓰지 않는다.
- 값 자체가 없으면(`APP_CORS_ALLOWED_ORIGINS` 미설정) 프로퍼티 플레이스홀더 해석 실패로 기동이 죽는다.
  값이 빈 문자열이거나 콤마/공백뿐이면(파싱 결과가 빈 리스트) `SecurityConfig.validateAllowedOrigins`
  (`@PostConstruct`)가 `IllegalStateException`을 던져 기동을 막는다 — JWT 시크릿 검증과 같은 이유
  (규칙 "시크릿/설정 기본값 은닉 금지"): 빈 CORS 설정을 조용히 통과시키면 모든 브라우저 크로스오리진
  요청이 이유 없이 403으로 막히는 걸 배포 후에나 알게 된다.

### 12. 리프레시 토큰으로 액세스 토큰만 재발급한다 — 리프레시 토큰은 재발급하지 않는다(로테이션 없음)
- `AuthTokenProvider.refreshTokenUserDetail(jwt)`가 `accessTokenUserDetail`과 대칭으로 존재한다 — 같은
  파싱 로직인데 서명 검증에 `accessSigningKey` 대신 `refreshSigningKey`를 쓴다는 것만 다르다.
- `UserRefreshUseCase`가 리프레시 토큰 파싱 실패(서명 불일치/만료/형식 오류 — `JwtException`/
  `IllegalArgumentException`)와 "토큰은 유효한데 그 유저가 이제 없음"을 구분하지 않고 둘 다
  `InvalidRefreshTokenException`(401)으로 합친다 — `UserLoginUseCase`가 "이메일 없음"과 "비밀번호 틀림"을
  구분 없이 `InvalidCredentialsException`으로 합치는 것과 같은 이유(실패 사유를 세분화해서 알려주지 않는다).
- 응답은 `UserRefreshResponse(accessToken)` 하나뿐이다 — 리프레시 토큰 로테이션(재발급 시 리프레시 토큰도
  같이 새로 발급)은 하지 않는다. 요청받은 범위(액세스 토큰 재발급)를 벗어나는 별도 결정이라 필요해지면
  그때 다시 설계한다.

### 13. 파일 저장은 카테고리별 하위 디렉토리로 분리한다 — video는 `videos/`, 이미지는 `images/`
- `global/storage/LocalFileStorageSupport`가 디렉토리 생성/검증, `serverUrl` 검증, UUID 파일명 할당,
  `/resources/{category}/{filename}` 공개 URL 조립·역파싱, 커밋/롤백 시점 파일 삭제 등록을 전담한다 —
  `file.upload-dir`/`file.server-url`은 그대로 재사용하고 `category`("videos"/"images")만 인자로 받아
  `{upload-dir}/{category}/`를 만든다. 새 파일 카테고리가 생기면 이 클래스를 새 `category`로 재사용하고,
  검증/URL 조립 로직을 또 베껴 쓰지 않는다.
- `LogFileStorage`/`VideoFileStorage`(log 도메인, ffmpeg 트랜스코딩 포함)와 `UserFileStorage`/
  `ImageFileStorage`(user 도메인, 검증만 하고 그대로 저장)는 각자 도메인의 포트/어댑터로 따로 두고
  `LocalFileStorageSupport`만 공유한다 — 도메인 경계를 넘는 공통 인터페이스로 합치지 않는다.
  `global/config/web/ResourcesConfig`의 `/resources/**` 핸들러는 `{upload-dir}` 전체를 그대로 매핑하므로
  하위 디렉토리가 늘어나도 별도 설정이 필요 없다.
- `PATCH /api/v1/users/me`(닉네임/프로필 이미지 수정)는 main 브랜치의 `UserProfileUpdateCase`에서 찾은
  비즈니스 로직(닉네임 변경 시 중복 확인 후 갱신, 이미지 교체 시 저장 후 이전 파일 커밋 후 삭제)을 그대로
  가져오되, main처럼 `userId` 경로 변수로 아무 유저나 수정 가능하게 열어두지 않는다 — `@AuthenticationPrincipal`
  로만 본인 것만 수정 가능(경로에 `userId` 없음), `GET /users/me`와 짝을 맞춘 자기 자신 전용 엔드포인트다.
  `LogUpdateUseCase`/`LogUpdateWebRequest`와 같은 패턴: nickname/profileImage 둘 다 없으면 웹 계층에서
  `InvalidProfileUpdateException`(400)을 던지고, 이미지 형식 검증은 `UserProfileImageValidator`(Tika 기반,
  `LogVideoValidator`와 대칭)가 맡는다.

### 14. `GET /logs`의 `userId`는 서버 단 필터일 뿐이다 — 없으면 그 date·hour의 전체 로그
- `LogListRequest.userId`(nullable)가 지정되면 `LogListUseCase.list`가 date/hour로 가져온 목록을
  `log.uploader().id().equals(userId)`로 걸러낸다. 생략하면 필터링 없이 기존 동작(그 date·hour의 모든
  유저 로그) 그대로다 — 프런트가 "우리반 전체" 조회와 "내 로그만" 조회를 같은 엔드포인트로 표현할 수
  있게 하기 위함(별도 `/logs/mine` 같은 엔드포인트를 새로 만들지 않았다).
- 리포지토리에 새 쿼리 메서드(`findByDateAndHourAndUploaderId` 등)를 추가하지 않고 기존
  `findByDate`/`findByDateAndHour` 결과에 애플리케이션 레벨 스트림 필터를 얹었다 — 그룹 하나의 로그
  수가 애초에 적어서(반 단위) DB 레벨 인덱스가 필요할 규모가 아니기 때문. 규모가 커지면 그때 리포지토리
  메서드로 내려도 된다.

## 피해야 할 안티패턴

- **공통 HTTP 예외 계층 부활 금지**: `global/exception` 같은 패키지에 상태코드 아는 부모 예외를 다시 만들지 않는다.
- **전역 `@ControllerAdvice`로 되돌리지 않는다**: 컨트롤러별 `assignableTypes` 유지.
- **애매한 파라미터 조합에 기본값 은닉 금지**: 명시적 400이 낫다.
- **`ddl-auto=update`를 맹신하지 않는다**: 과거 엔티티 필드명 변경(`term`→`hour`)이 실제 DB 컬럼에 반영되지 않아 로그 관련 기능 전체가 500으로 죽은 사고가 있었다. 필드/컬럼명을 바꾸면 개발 DB 스키마도 함께 확인·마이그레이션해야 한다.
- **시크릿/설정 기본값 은닉 금지**: 비어 있으면 반드시 기동 실패.
- **컨트롤러에서 체크 예외 try/catch 금지**: DTO 변환부/유스케이스로 위임.
- **인터페이스·DTO에 여러 멤버가 있는 파일을 라인 범위로 수정할 때 기존 멤버를 실수로 지우기 쉽다** — 특히 `save()` 같은 기존 메서드를 새 메서드 추가 편집 중 통째로 날린 사고가 두 번 있었다. 범위 수정 후 반드시 전체 파일을 다시 읽어 확인한다.
- **API 문서(`docs/api-spec.yaml`)는 자동 생성되지 않는다**: 규칙 9 참고. 현재 남아있는 미구현 초안은 스키마
  `LogGetListResponse`/`LogGetByHourResponse`/`LogGetByHourListResponse` 뿐이다(`/users/{userId}/profile`
  초안은 `PATCH /users/me`로 실제 구현되면서 정리됨). 문서를 그대로 신뢰하지 말고 실제 컨트롤러 코드를
  기준으로 판단한다.
- **유스케이스 이름 그대로 1:1 패키지(`logCreate/`, `logGet/`, `logUpdate/` ...) 만들지 않는다**: `crud`/`auth` 같은 관심사 그룹 아래 flat하게 둔다.
- **다른 도메인 응답을 요약하려고 도메인별 축약 DTO(`LogUploaderResponse` 같은) 중복 정의 금지**: 소유 도메인의 응답 DTO를 참조한다.

## 운영/검증 메모

- 로컬 컨테이너: `docker compose --env-file deploy/env/.env -f deploy/build/docker-compose.yml up -d --build`.
  백엔드 코드를 고치면 반드시 재빌드 후 실제로 컨테이너가 최신 상태인지 확인하고 나서 "적용됨"이라 말한다.
- 동작 검증은 실제 HTTP 스모크 테스트(가입→토큰→호출)로 하고, 만든 임시 계정/로그/업로드 파일은 검증 후 삭제한다.
- 명시적 요청이 없으면 새 테스트 파일을 만들지 않는다. 회귀 검증이 필요하면 일회성 스크립트나 기존 자동화 테스트 스위트(`./gradlew test`)로 확인한다.
