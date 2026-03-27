# API 명세서

## 공통 정보

| 항목 | 내용 |
|------|------|
| Base URL | `http://localhost:8080` |
| Content-Type | `application/json` |
| 인증 방식 | 없음 (추후 JWT 예정) |

### 공통 응답 코드

| 상태 코드 | 설명 |
|----------|------|
| 200 | 요청 성공 |
| 400 | 잘못된 요청 (필수값 누락, 타입 오류 등) |
| 404 | 리소스 없음 |
| 500 | 서버 내부 오류 |

---

## Post

### 1. 게시글 목록 조회

| 항목 | 내용 |
|------|------|
| Method | `GET` |
| URL | `/post` |
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
      "content": "내용입니다."
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
| comments | List | 댓글 목록 |
| comments[].id | Long | 댓글 ID |
| comments[].content | String | 댓글 내용 |

```json
{
  "id": 1,
  "title": "제목입니다.",
  "content": "내용입니다.",
  "comments": [
    {
      "id": 1,
      "content": "댓글 내용입니다."
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
| 설명 | 게시글을 생성한다. |

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

---

### 4. 게시글 수정

| 항목 | 내용 |
|------|------|
| Method | `PUT` |
| URL | `/post/{id}` |
| 설명 | 게시글을 수정한다. |

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
| 400 | 잘못된 요청 |
| 404 | 게시글 없음 |

---

### 5. 게시글 삭제

| 항목 | 내용 |
|------|------|
| Method | `DELETE` |
| URL | `/post/{id}` |
| 설명 | 게시글을 삭제한다. |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|:----:|------|
| id | Long | Y | 게시글 ID |

**Response**

| 상태 코드 | 설명 |
|----------|------|
| 200 | 성공 |
| 404 | 게시글 없음 |

---

## Comment

### 1. 댓글 작성

| 항목 | 내용 |
|------|------|
| Method | `POST` |
| URL | `/post/{postId}/comments` |
| 설명 | 특정 게시글에 댓글을 작성한다. |

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
| 400 | 잘못된 요청 |
| 404 | 게시글 없음 |

---

### 2. 댓글 삭제

| 항목 | 내용 |
|------|------|
| Method | `DELETE` |
| URL | `/post/{postId}/comments/{commentId}` |
| 설명 | 특정 게시글의 댓글을 삭제한다. |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|:----:|------|
| postId | Long | Y | 게시글 ID |
| commentId | Long | Y | 댓글 ID |

**Response**

| 상태 코드 | 설명 |
|----------|------|
| 200 | 성공 |
| 404 | 게시글 또는 댓글 없음 |