# API

| Method | Path | Blank |
| --- | --- | --- |
| POST | `/api/v1/users/signup` |
| POST | `/api/v1/users/login` |
| GET | `/api/v1/users/profile` |
| PATCH | `/api/v1/users/profile` |
| POST | `/api/v1/logs/video` |
| GET | `/api/v1/logs/{date}` |
| GET | `/api/v1/logs/hour` |
| PATCH | `/api/v1/logs/{logId}/caption` |
| GET | `/api/v1/logs/hours` |
| DELETE | `/api/v1/logs/delete/{logId}` |
| POST | `/api/v1/video/emotion` | 로그에 감정표현 남기기 |
| POST | `/api/v1/video/chat` | 댓글 작성 |
| GET | `/api/v1/video/{logId}/chat` | 댓글 조회 | 
