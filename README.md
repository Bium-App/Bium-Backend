# Bium Backend

> **필요한 정보는 필요한 만큼만, 중요한 정보는 오래도록.**

정보의 수명에 따라 메모를 **FIRE / ICE / TRASH**로 구분하여 관리하는 모바일 메모 애플리케이션 **비움(Bium)**의 Backend Repository입니다.

## Team

- Team: 비움
- GitHub Organization: Bium-App
- Repository: Bium-Backend

## 프로젝트 소개

기존 메모 애플리케이션에서는 일시적으로 필요한 정보와 장기간 보관할 정보가 같은 방식으로 누적되면서, 사용자가 직접 메모를 분류하거나 삭제해야 하는 관리 부담이 발생할 수 있습니다.

**비움(Bium)**은 이러한 문제를 개선하기 위해 메모에 **정보 수명** 개념을 적용한 모바일 메모 애플리케이션입니다.

서비스에서는 메모를 **FIRE / ICE / TRASH**로 구분해 정보가 필요한 기간과 보관 목적에 따라 관리합니다.

- **FIRE**: 일정 시간 동안 필요한 정보를 관리하는 메모
- **ICE**: 장기간 보관할 정보를 관리하는 메모
- **TRASH**: 만료되거나 삭제된 메모를 복구하거나 영구 삭제할 수 있는 영역

개인 중심의 메모 관리 기능과 함께 **TeamSpace**를 통해 팀 단위 정보 관리로 확장할 수 있는 구조를 제공합니다.

Backend는 **Spring Boot** 기반 REST API 서버로 구성되어 있으며, 메모 수명 관리, 인증 및 사용자 관리, TeamSpace 협업 기능, 알림, 검색 및 파일 업로드 등을 처리합니다.

## 시스템 구조

```text
React Native Client
        │
        │ REST API / JWT
        ▼
Spring Security
        │
        ▼
   Controller
        │
        ▼
     Service
        │
        ▼
   Repository
        │
        ▼
      MySQL

Spring Boot ───── AWS S3
                Presigned URL
```

서버는 Controller / Service / Repository 계층으로 역할을 분리하여 구성했습니다.

- **Controller**: HTTP 요청과 응답 처리
- **Service**: 인증, 상태 변경, 협업 기능 등 주요 비즈니스 로직 처리
- **Repository**: Spring Data JPA 기반 데이터 접근
- **Domain**: User, Memo, TeamSpace 등 주요 데이터 모델 관리
- **DTO**: API 요청 및 응답 데이터 구조 분리

## 핵심 도메인

### Memo

메모의 상태와 정보 수명을 관리하는 핵심 도메인입니다.

현재 Memo Entity의 `status`는 다음 두 값을 사용합니다.

```text
FIRE
ICE
```

TRASH는 별도의 MemoStatus 값으로 저장하지 않고 `deletedAt`을 이용해 관리합니다.

- `deletedAt == null`: 일반 메모
- `deletedAt != null`: TRASH에 있는 메모

따라서 메모가 TRASH로 이동하더라도 기존 FIRE / ICE 상태는 유지되며, 복구 시 `deletedAt`을 제거해 다시 일반 메모 목록으로 복귀합니다.

FIRE 메모의 만료 시각은 `expiredAt`으로 관리합니다.

### TRASH

만료되거나 사용자가 삭제한 메모를 관리하는 영역입니다.

- TRASH 메모 목록 조회
- 메모 복구
- 여러 메모 선택
- 선택 메모 영구 삭제
- TRASH 이동 후 24시간이 지난 메모 자동 영구 삭제

Bium에서는 **TRASH 이동과 영구 삭제를 서로 다른 동작으로 구분합니다.**

### User

사용자 계정과 앱 설정을 관리합니다.

- 일반·Google 로그인 정보
- 프로필
- 언어 및 날짜 형식
- 알림 설정
- 2단계 인증 설정
- 로그인 기기 및 Refresh Token

### TeamSpace

개인 중심의 정보 관리 구조를 팀 단위로 확장하기 위한 협업 도메인입니다.

TeamSpace와 TeamMember를 중심으로 다음 데이터를 연결합니다.

- Memo
- Member
- Notice
- Todo
- Schedule
- File

## 주요 기능

### 인증 및 사용자

- 일반 회원가입
- 일반 로그인
- Google 소셜 로그인
- Access Token / Refresh Token 발급
- Access Token 재발급
- 현재 기기 로그아웃
- 전체 기기 로그아웃
- 로그인 기기 조회
- 개별 기기 로그아웃
- 아이디 찾기
- 사용자 프로필 조회 / 수정
- 사용자 설정 조회 / 수정
- 회원 탈퇴

일반 로그인 비밀번호는 `BCryptPasswordEncoder`를 이용해 암호화된 비밀번호와 비교합니다.

Google 소셜 로그인은 클라이언트에서 전달받은 Google ID Token을 서버에서 검증한 뒤, 검증된 Google 사용자 정보를 기준으로 계정을 조회하거나 생성하고 Bium Access Token / Refresh Token을 발급하는 방식으로 구성되어 있습니다.

### JWT 인증

Bium Backend는 **Spring Security + JWT** 기반 인증 구조를 사용합니다.

인증이 필요한 요청은 다음 Header를 사용합니다.

```text
Authorization: Bearer {Access_Token}
```

현재 Token 유효기간은 다음과 같습니다.

```text
Access Token  : 30분
Refresh Token : 14일
```

Access Token은 API 요청 인증에 사용합니다.

Refresh Token은 로그인 기기 정보와 함께 저장하며, Access Token 재발급 시 새로운 Refresh Token으로 갱신합니다.

Spring Security는 서버 세션을 생성하지 않는 Stateless 방식으로 구성되어 있으며, JWT 인증 필터를 통해 요청의 사용자 정보를 확인합니다.

### 2단계 인증

2단계 인증 설정과 검증을 위한 처리 흐름이 구성되어 있습니다.

현재 서버에서는 다음 기능을 처리합니다.

- 인증 코드 생성
- 인증 코드 유효시간 관리
- 재요청 제한
- 인증 실패 횟수 제한
- 인증 코드 검증
- 2단계 인증 설정 상태 변경

현재 2단계 인증의 임시 인증 세션은 서버 메모리에서 관리합니다.

### 메모 관리

- 개인 메모 생성
- TeamSpace 메모 생성
- 개인 메모 목록 조회
- TeamSpace 메모 목록 조회
- 메모 상세 조회
- Rich Text 내용 저장 / 수정
- 메모 이미지 정보 포함 상세 조회
- FIRE ↔ ICE 상태 변경
- ICE 메모 고정 / 고정 해제
- 메모 TRASH 이동
- TRASH 목록 조회
- TRASH 메모 복구
- 선택 메모 영구 삭제

메모 상태와 고정 여부 변경은 다음 API의 `action` 값으로 구분합니다.

```text
PATCH /api/memos/{memoId}/status
```

```text
action=STATUS
action=PIN
```

### 메모 만료 및 TRASH 처리

FIRE 메모의 만료 시각은 `expiredAt`으로 관리합니다.

서버에서는 Spring Scheduling을 활성화하여 **1분 단위로 만료 시각을 확인**합니다.

현재 스케줄러는 `expiredAt`이 설정되어 있고 만료 시각이 지난 일반 메모에 `deletedAt`을 기록해 TRASH로 이동 처리합니다.

```text
FIRE
 ↓
만료 시각 도달
 ↓
TRASH
 ↓
24시간 경과
 ↓
영구 삭제
```

TRASH에 있는 메모 중 `deletedAt`을 기준으로 24시간이 지난 메모는 스케줄러가 영구 삭제합니다.

복구 시에는 `deletedAt`을 제거하며, 만료 시각이 설정된 메모는 현재 시각을 기준으로 만료 시각을 12시간 연장합니다.

### 메모 이미지

메모와 이미지 정보를 별도의 Entity로 관리합니다.

- 메모 이미지 등록
- 메모별 이미지 목록 조회
- 메모 상세 응답에 이미지 정보 포함
- 이미지 정보 삭제

이미지 파일 자체는 AWS S3에 저장하며, 데이터베이스에는 이미지 URL과 해당 메모의 연결 정보를 저장합니다.

### 친구

- 친구 목록 조회
- 사용자 검색
- 친구 요청 전송
- 받은 친구 요청 조회
- 보낸 친구 요청 조회
- 친구 요청 수락
- 친구 요청 거절
- 친구 요청 취소

자기 자신에게 친구 요청을 보내거나 동일한 사용자 사이에 대기 중 또는 수락된 요청이 이미 존재하는 경우 중복 요청을 제한합니다.

친구 요청이 생성되면 요청을 받은 사용자를 대상으로 Notification 데이터도 생성합니다.

### TeamSpace

- TeamSpace 생성
- 참여 중인 TeamSpace 조회
- TeamSpace 상세 조회
- TeamSpace 이름 수정
- TeamSpace 삭제
- TeamMember 추가 / 조회
- TeamMember 역할 변경
- TeamMember 제거

TeamSpace를 생성한 사용자는 해당 공간의 `LEADER` 역할을 가진 TeamMember로 등록됩니다.

현재 TeamMember 역할은 다음과 같습니다.

```text
LEADER
MEMBER
```

### Team Notice

- 공지 생성
- TeamSpace별 공지 조회
- 공지 상세 조회
- 공지 수정
- 공지 삭제
- 새 공지 등록 시 팀원 알림 생성

공지 작성 시 작성자를 제외한 TeamSpace 구성원에게 새 공지 Notification을 생성합니다.

### Team Todo

- 할 일 생성
- TeamSpace별 할 일 조회
- 할 일 상세 조회
- 할 일 수정
- 할 일 삭제

### Schedule

- 일정 생성
- 개인 일정 조회
- TeamSpace 일정 조회
- 월 단위 일정 조회
- 일정 상세 조회
- 일정 수정
- 일정 삭제

### Team File

- TeamSpace 파일 정보 저장
- TeamSpace 파일 목록 조회
- 파일명 수정
- 파일 정보 삭제

실제 파일 업로드는 S3 Presigned URL을 이용하며, TeamFile에는 업로드된 파일의 메타데이터를 저장합니다.

### 통합 검색

현재 Backend 검색 로직은 키워드를 기준으로 다음 데이터를 조회합니다.

- Memo
- Team Todo
- Schedule

검색 결과는 하나의 응답 구조로 반환합니다.

### 알림

- 사용자 알림 목록 조회
- 알림 읽음 처리
- 알림 삭제
- 친구 요청 관련 알림 생성
- TeamSpace 공지 관련 알림 생성

### 문의 및 서비스 공지

- 문의 / 제안 등록
- 내 문의 내역 조회
- 서비스 공지 조회

## 파일 업로드

파일 업로드에는 **AWS S3 Presigned URL** 방식을 사용합니다.

```text
1. Client
   │
   │ Presigned URL 요청
   ▼
2. Bium Backend
   │
   │ 업로드용 URL 발급
   ▼
3. Client
   │
   │ PUT
   ▼
4. AWS S3
```

Backend가 제한된 시간 동안 사용할 수 있는 PUT URL을 생성하고, Client가 해당 URL을 이용해 S3에 직접 파일을 업로드합니다.

업로드 이후 필요한 파일 URL과 메타데이터를 Backend에 저장합니다.

## 기술 스택

| 구분 | 기술 | 버전 |
| --- | --- | --- |
| Language | Java | `21` |
| Framework | Spring Boot | `3.3.0` |
| Build Tool | Gradle Wrapper | `8.7` |
| Dependency Management | io.spring.dependency-management | `1.1.7` |
| Web | Spring Web | Spring Boot 관리 |
| Security | Spring Security | Spring Boot 관리 |
| Authentication | JJWT | `0.11.5` |
| Password | BCrypt | Spring Security |
| ORM | Spring Data JPA | Spring Boot 관리 |
| Database | MySQL Connector/J | Spring Boot 관리 |
| Cloud Storage | AWS S3 | - |
| AWS SDK | aws-java-sdk-s3 | `1.12.715` |
| Boilerplate | Lombok | Spring Boot 관리 |
| Test | Spring Boot Test / Spring Security Test | Spring Boot 관리 |

## 프로젝트 구조

```text
Bium-Backend/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── memo/example/demo/
│   │           ├── DTO/
│   │           │   ├── request/       # API 요청 DTO
│   │           │   └── response/      # API 응답 DTO
│   │           │
│   │           ├── Exception/         # 공통 예외 및 에러 응답
│   │           ├── config/            # Spring Security 및 애플리케이션 설정
│   │           │   └── jwt/           # JWT 발급 / 검증 / 인증 필터
│   │           ├── controller/        # REST API Controller
│   │           ├── domain/            # JPA Entity
│   │           ├── repository/        # Spring Data JPA Repository
│   │           ├── service/           # 비즈니스 로직
│   │           └── DemoApplication.java
│   │
│   └── test/                           # 테스트 코드
│
├── gradle/
│   └── wrapper/
├── .env.example                       # 환경 설정 참고 예시
├── .gitignore
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

## 주요 API

현재 사용 중인 주요 API를 기준으로 정리했습니다.

| 기능 | 주요 경로 |
| --- | --- |
| 인증 | `/api/auth/signup`, `/api/auth/login`, `/api/auth/social-login`, `/api/auth/refresh`, `/api/auth/logout`, `/api/auth/find`, `/api/auth/2fa` |
| 로그인 기기 | `/api/auth/devices`, `/api/auth/devices/{deviceId}` |
| 사용자 | `/api/users/me`, `/api/users/me/settings`, `/api/users/search` |
| 메모 | `/api/memos`, `/api/memos/{memoId}`, `/api/memos/{memoId}/status` |
| 메모 이미지 | `/api/memos/{memoId}/images`, `/api/memos/images/{imageId}` |
| TRASH | `/api/trash`, `/api/trash/{memoId}/restore` |
| 친구 | `/api/friends`, `/api/friends/requests`, `/api/friends/requests/{requestId}` |
| TeamSpace | `/api/team-spaces`, `/api/team-spaces/{teamSpaceId}` |
| TeamMember | `/api/team-spaces/{teamSpaceId}/members`, `/api/team-members/team/{teamSpaceId}`, `/api/team-members/{memberId}` |
| Team Notice | `/api/team-spaces/{teamSpaceId}/notices`, `/api/notices`, `/api/notices/{noticeId}` |
| Team Todo | `/api/team-spaces/{teamSpaceId}/todos`, `/api/todos`, `/api/todos/{todoId}` |
| 일정 | `/api/schedules`, `/api/schedules/{scheduleId}` |
| Team File | `/api/team-spaces/{teamSpaceId}/files`, `/api/team-files/{fileId}` |
| 알림 | `/api/notifications`, `/api/notifications/{id}/read`, `/api/notifications/{id}` |
| 통합 검색 | `/api/search` |
| 문의 | `/api/inquiries`, `/api/inquiries/me` |
| 서비스 공지 | `/api/service-notices` |
| 파일 업로드 | `/api/files/presigned-url` |

세부 Request / Response 구조는 프로젝트의 API 명세를 기준으로 관리합니다.

## 주요 Entity

현재 Backend에는 다음 주요 Entity가 구성되어 있습니다.

```text
User
Device
Friend

Memo
MemoImage

TeamSpace
TeamMember
TeamNotice
TeamTodo
TeamFile
Schedule

Notification
Inquiry
ServiceNotice
```

Memo와 TeamSpace 관련 Entity는 사용자 및 팀 정보를 관계형 데이터로 연결하여 관리합니다.

## 시간 처리

서버 기본 시간대는 다음과 같이 설정합니다.

```text
Asia/Seoul
```

애플리케이션 시작 시 JVM 기본 시간대를 KST로 설정하여 날짜와 시간 처리 기준을 통일합니다.

메모 만료 처리와 일정 데이터 역시 서버 시간 기준으로 처리합니다.

## 환경 설정

Repository에는 실제 서비스 비밀번호나 인증정보를 포함하지 않는 것을 원칙으로 합니다.

`.env.example`은 개발 및 배포 환경에서 필요한 설정 항목을 확인하기 위한 참고 파일이며, Spring Boot가 해당 파일을 직접 읽도록 구성되어 있지는 않습니다.

현재 `.env.example`에는 다음 항목이 정리되어 있습니다.

```text
DB_URL
DB_USERNAME
DB_PASSWORD

JWT_SECRET

AWS_REGION
S3_BUCKET

CORS_ALLOWED_ORIGIN_PATTERNS

JPA_DDL_AUTO
JPA_SHOW_SQL
```

현재 Repository에서는 `application.properties`, `application-local.properties`, `application-prod.properties`를 Git 관리 대상에서 제외합니다.

실제 Spring Boot 실행 환경에서는 데이터베이스, JWT, AWS S3 및 CORS 등에 필요한 설정을 외부 환경 변수 또는 별도의 로컬 설정 파일을 통해 제공해야 합니다.

Backend 코드에서 사용하는 주요 Spring 설정 항목은 다음과 같습니다.

```text
spring.datasource.url
spring.datasource.username
spring.datasource.password

jwt.secret

cloud.aws.credentials.access-key
cloud.aws.credentials.secret-key
cloud.aws.region.static
cloud.aws.s3.bucket

app.cors.allowed-origin-patterns

spring.jpa.hibernate.ddl-auto
spring.jpa.show-sql
```

AWS S3 Presigned URL 기능을 사용하려면 Region과 Bucket 정보뿐만 아니라 AWS Credential 설정도 실행 환경에서 별도로 제공해야 합니다.

실제 비밀번호, JWT Secret, AWS Access Key / Secret Key 등의 값은 공개 Repository에 작성하지 않습니다.

## 실행 방법

### 요구 환경

```text
Java 21
MySQL
```

Gradle은 Repository에 포함된 **Gradle Wrapper 8.7**을 사용합니다.

### Repository Clone

```bash
git clone https://github.com/Bium-App/Bium-Backend.git
cd Bium-Backend
```

### 환경 설정

데이터베이스, JWT, AWS S3 및 CORS 설정을 개발 환경에 맞게 구성합니다.

`.env.example`은 필요한 설정 항목을 확인하기 위한 참고 자료로 사용하며, 실제 Spring Boot 설정은 실행 환경에 맞게 별도로 제공해야 합니다.

민감정보가 포함된 설정 파일은 Git에 추가하지 않습니다.

### 애플리케이션 실행

macOS / Linux / Git Bash:

```bash
./gradlew bootRun
```

Windows Command Prompt / PowerShell:

```bat
gradlew.bat bootRun
```

### 테스트

macOS / Linux / Git Bash:

```bash
./gradlew test
```

Windows Command Prompt / PowerShell:

```bat
gradlew.bat test
```

### 빌드

macOS / Linux / Git Bash:

```bash
./gradlew build
```

Windows Command Prompt / PowerShell:

```bat
gradlew.bat build
```

빌드 결과물은 다음 경로에 생성됩니다.

```text
build/
```

## 브랜치 정책

### `main`

최종 제출 및 공개 확인용 브랜치입니다.

검토와 안정화가 완료된 코드를 반영합니다.

### `dev`

실제 개발 작업을 진행하는 브랜치입니다.

기능 추가와 수정 내용을 먼저 반영하고, 검토가 완료된 이후 `main`에 반영합니다.

## Repository Policy

공개 저장소에는 서비스 운영에 사용되는 민감정보를 포함하지 않는 것을 원칙으로 합니다.

GitHub에 포함하지 않는 항목은 다음과 같습니다.

- 데이터베이스 비밀번호
- JWT Secret
- Access Token / Refresh Token
- AWS Access Key / Secret Access Key
- Private Key
- `.pem` / `.key` 파일
- 실제 운영 환경설정
- IDE별 로컬 설정
- Gradle Build 결과물

민감정보는 환경 변수 또는 Git에서 제외된 별도의 설정 파일을 통해 관리합니다.

## 향후 계획

- Android / iOS 정식 스토어 배포 및 모바일 환경 지원 확대
- TeamSpace 세부 협업 기능과 알림 연동 고도화
- 사용자 메모 작성 패턴을 활용한 FIRE / ICE 상태 선택 보조 기능 검토