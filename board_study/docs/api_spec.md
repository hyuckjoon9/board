# API 명세서

## 공통 정보

| 항목 | 내용 |
|------|------|
| Base URL | `http://localhost:8080` |
| Content-Type | `application/json` |
| 인증 방식 | JWT Bearer Token |

### 인증 헤더

인증이 필요한 API는 모든 요청에 아래 헤더를 포함해야 합니다.

```
Authorization: Bearer {JWT_TOKEN}
```

### 공통 응답 코드

| 상태 코드 | 설명 |
|----------|------|
| 200 | 요청 성공 |
| 400 | 잘못된 요청 (필수값 누락, 타입 오류 등) |
| 401 | 인증 실패 (토큰 없음, 만료, 잘못된 자격증명) |
| 403 | 접근 권한 없음 (본인 작성 콘텐츠가 아님) |
| 404 | 리소스 없음 |
| 500 | 서버 내부 오류 |

### 에러 응답 형식

```json
{
  "status": 401,
  "message": "인증이 필요합니다."
}
```

---

## Auth

### 1. 회원가입

| 항목 | 내용 |
|------|------|
| Method | `POST` |
| URL | `/auth/signup` |
| 인증 | 불필요 |
| 설명 | 사용자명과 비밀번호로 회원가입한다. |

**Request Body**

| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| username | String | Y | 사용자 이름 (로그인 ID) |
| password | String | Y | 비밀번호 |

```json
{
  "username": "user1",
  "password": "password123"
}
```

**Response**

| 상태 코드 | 설명 |
|----------|------|
| 200 | 성공 |
| 400 | 이미 존재하는 사용자명 |

---

### 2. 로그인

| 항목 | 내용 |
|------|------|
| Method | `POST` |
| URL | `/auth/login` |
| 인증 | 불필요 |
| 설명 | 자격증명 검증 후 JWT 토큰을 반환한다. |

**Request Body**

| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| username | String | Y | 사용자 이름 |
| password | String | Y | 비밀번호 |

```json
{
  "username": "user1",
  "password": "password123"
}
```

**Response `200`**

| 필드 | 타입 | 설명 |
|------|------|------|
| token | String | JWT 토큰 |

```json
{
  "token": "eyJ..."
}
```

**Response `401`**

```json
{
  "status": 401,
  "message": "인증이 필요합니다."
}
```

---

## Post

### 1. 게시글 목록 조회

| 항목 | 내용 |
|------|------|
| Method | `GET` |
| URL | `/post` |
| 인증 | 불필요 |
| 설명 | 게시글 목록을 페이징하여 반환한다. keyword가 있으면 제목 기반 검색을 수행한다. |

**Query Parameters**

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|:----:|--------|------|
| page | int | N | 0 | 페이지 번호 (0부터 시작) |
| keyword | String | N | - | 제목 검색 키워드 |

**Response `200`**

| 필드 | 타입 | 설명 |
|------|------|------|
| content | List | 게시글 목록 |
| content[].id | Long | 게시글 ID |
| content[].title | String | 게시글 제목 |
| content[].content | String | 게시글 내용 |
| content[].username | String | 작성자 |
| content[].createdAt | String | 작성시간 (yyyy-MM-dd HH:mm) |
| totalPages | int | 전체 페이지 수 |
| totalElements | Long | 전체 게시글 수 |
| number | int | 현재 페이지 번호 |
| size | int | 페이지당 게시글 수 |
| first | boolean | 첫 번째 페이지 여부 |
| last | boolean | 마지막 페이지 여부 |

```json
{
  "content": [
    {
      "id": 1,
      "title": "제목입니다.",
      "content": "내용입니다.",
      "username": "user1",
      "createdAt": "2024-01-01 12:00"
    }
  ],
  "totalPages": 1,
  "totalElements": 1,
  "number": 0,
  "size": 10,
  "first": true,
  "last": true
}
```

---

### 2. 게시글 단건 조회

| 항목 | 내용 |
|------|------|
| Method | `GET` |
| URL | `/post/{id}` |
| 인증 | 불필요 |
| 설명 | 게시글 ID로 단건 조회한다. 댓글 목록을 포함하여 반환한다. |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|:----:|------|
| id | Long | Y | 게시글 ID |

**Response**

| 상태 코드 | 설명 |
|----------|------|
| 200 | 성공 |
| 404 | 게시글 없음 |

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 게시글 ID |
| title | String | 게시글 제목 |
| content | String | 게시글 내용 |
| username | String | 작성자 |
| createdAt | String | 작성시간 (yyyy-MM-dd HH:mm) |
| comments | List | 댓글 목록 |
| comments[].id | Long | 댓글 ID |
| comments[].content | String | 댓글 내용 |
| comments[].username | String | 댓글 작성자 |
| comments[].createdAt | String | 댓글 작성시간 (yyyy-MM-dd HH:mm) |

```json
{
  "id": 1,
  "title": "제목입니다.",
  "content": "내용입니다.",
  "username": "user1",
  "createdAt": "2024-01-01 12:00",
  "comments": [
    {
      "id": 1,
      "content": "댓글 내용입니다.",
      "username": "user2",
      "createdAt": "2024-01-01 12:05"
    }
  ]
}
```

---

### 3. 게시글 생성

| 항목 | 내용 |
|------|------|
| Method | `POST` |
| URL | `/post/create` |
| 인증 | **필요** |
| 설명 | 게시글을 생성한다. |

**Request Headers**

| 헤더 | 필수 | 설명 |
|------|:----:|------|
| Authorization | Y | `Bearer {JWT_TOKEN}` |

**Request Body**

| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| title | String | Y | 게시글 제목 |
| content | String | Y | 게시글 내용 |

```json
{
  "title": "제목입니다.",
  "content": "내용입니다."
}
```

**Response**

| 상태 코드 | 설명 |
|----------|------|
| 200 | 성공 |
| 400 | 잘못된 요청 |
| 401 | 인증 필요 |

---

### 4. 게시글 수정

| 항목 | 내용 |
|------|------|
| Method | `PUT` |
| URL | `/post/{id}` |
| 인증 | **필요 (작성자 본인만 가능)** |
| 설명 | 게시글을 수정한다. |

**Request Headers**

| 헤더 | 필수 | 설명 |
|------|:----:|------|
| Authorization | Y | `Bearer {JWT_TOKEN}` |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|:----:|------|
| id | Long | Y | 게시글 ID |

**Request Body**

| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| title | String | Y | 수정할 제목 |
| content | String | Y | 수정할 내용 |

```json
{
  "title": "수정된 제목입니다.",
  "content": "수정된 내용입니다."
}
```

**Response**

| 상태 코드 | 설명 |
|----------|------|
| 200 | 성공 |
| 401 | 인증 필요 |
| 403 | 접근 권한 없음 (본인 게시글 아님) |
| 404 | 게시글 없음 |

---

### 5. 게시글 삭제

| 항목 | 내용 |
|------|------|
| Method | `DELETE` |
| URL | `/post/{id}` |
| 인증 | **필요 (작성자 본인만 가능)** |
| 설명 | 게시글을 삭제한다. |

**Request Headers**

| 헤더 | 필수 | 설명 |
|------|:----:|------|
| Authorization | Y | `Bearer {JWT_TOKEN}` |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|:----:|------|
| id | Long | Y | 게시글 ID |

**Response**

| 상태 코드 | 설명 |
|----------|------|
| 200 | 성공 |
| 401 | 인증 필요 |
| 403 | 접근 권한 없음 (본인 게시글 아님) |
| 404 | 게시글 없음 |

---

## Comment

### 1. 댓글 작성

| 항목 | 내용 |
|------|------|
| Method | `POST` |
| URL | `/post/{postId}/comments` |
| 인증 | **필요** |
| 설명 | 특정 게시글에 댓글을 작성한다. |

**Request Headers**

| 헤더 | 필수 | 설명 |
|------|:----:|------|
| Authorization | Y | `Bearer {JWT_TOKEN}` |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|:----:|------|
| postId | Long | Y | 게시글 ID |

**Request Body**

| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| content | String | Y | 댓글 내용 |

```json
{
  "content": "댓글 내용입니다."
}
```

**Response**

| 상태 코드 | 설명 |
|----------|------|
| 200 | 성공 |
| 401 | 인증 필요 |
| 404 | 게시글 없음 |

---

### 2. 댓글 삭제

| 항목 | 내용 |
|------|------|
| Method | `DELETE` |
| URL | `/post/{postId}/comments/{commentId}` |
| 인증 | **필요 (작성자 본인만 가능)** |
| 설명 | 특정 게시글의 댓글을 삭제한다. |

**Request Headers**

| 헤더 | 필수 | 설명 |
|------|:----:|------|
| Authorization | Y | `Bearer {JWT_TOKEN}` |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|:----:|------|
| postId | Long | Y | 게시글 ID |
| commentId | Long | Y | 댓글 ID |

**Response**

| 상태 코드 | 설명 |
|----------|------|
| 200 | 성공 |
| 401 | 인증 필요 |
| 403 | 접근 권한 없음 (본인 댓글 아님) |
| 404 | 게시글 또는 댓글 없음 |
