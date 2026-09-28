# KOLOG BE

## CONTRIBUTION CONVENTION
### 기본 브랜치 구조
- main (배포)
- dev (개발 및 테스트)
- feature/기능명 (기능 추가)
- fix/수정내용 (수정)

### 개발 프로세스
1. main -> dev 브랜치 생성
2. dev -> 하위 브랜치 생성
3. 하위 브랜치에서 개발 수행
4. 하위 브랜치를 dev에 merge 이후 테스트
5. dev -> main PR 생성 후 병합

## COMMIT CONVENTION
| Type     | Description |
|----------|-----|
| feat     | 새로운 기능 추가 |
| fix      | 버그 수정 |
| docs     | 문서 관련 |
| style    | 코드 스타일 변경 |
| refactor | 코드 리팩토링 |
| test     | 테스트 관련 코드 |
| build    | 빌드 관련 파일 수정 |
| ci       | CI 설정 파일 수정 |
| perf     | 성능 개선 |
| chore    | 그 외 자잘한 수정 |

## FILE UPLOADS

- Video uploads accept MP4 and WebM input; new files are transcoded with FFmpeg
  to silent H.264 (`yuv420p`) MP4. Existing uploads are not migrated.
- Files are stored under `FILE_UPLOAD_DIR`; URLs use `FILE_SERVER_URL/resources/`.
- `FILE_UPLOAD_DIR`, `FILE_SERVER_URL`, and both JWT secrets are required at
  startup. Invalid storage settings or JWT secrets prevent startup.
- A database rollback removes its new video file. A process crash can still
  leave orphan files; no durable cleanup queue is provided.
