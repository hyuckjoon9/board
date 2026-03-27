const API_BASE = localStorage.getItem('apiBaseUrl') || 'http://localhost:8080';

async function request(path, options = {}) {
  const url = `${API_BASE}${path}`;
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options,
  });
  const text = await res.text();
  const isJson = (res.headers.get('content-type') || '').includes(
    'application/json'
  );
  const data = isJson && text ? JSON.parse(text) : text;
  if (!res.ok) {
    throw new Error(
      typeof data === 'string' ? data || res.statusText : JSON.stringify(data)
    );
  }
  return data;
}

export function getApiBase() {
  return API_BASE;
}
export function setApiBase(base) {
  localStorage.setItem('apiBaseUrl', base);
}

export async function getPosts(page = 0, keyword = '') {
  const params = new URLSearchParams();
  params.set('page', page);
  if (keyword && keyword.trim()) {
    params.set('keyword', keyword.trim());
  }
  return request(`/post?${params.toString()}`);
}
export async function getPost(id) {
  return request(`/post/${id}`);
}
export async function createPost({ title, content }) {
  return request('/post/create', {
    method: 'POST',
    body: JSON.stringify({ title, content }),
  });
}
export async function updatePost(id, { title, content }) {
  return request(`/post/${id}`, {
    method: 'PUT',
    body: JSON.stringify({ title, content }),
  });
}
export async function deletePost(id) {
  return request(`/post/${id}`, { method: 'DELETE' });
}

export async function createComment(postId, { content }) {
  return request(`/post/${postId}/comments`, {
    method: 'POST',
    body: JSON.stringify({ content }),
  });
}

export async function deleteComment(postId, commentId) {
  return request(`/post/${postId}/comments/${commentId}`, {
    method: 'DELETE',
  });
}
