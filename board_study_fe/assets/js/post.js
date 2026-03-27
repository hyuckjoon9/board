import { getPost, createPost, updatePost } from './api.js';
import { qs, toast, getQueryParam } from './utils.js';

const id = getQueryParam('id');

async function init() {
  if (id) {
    qs('#mode').textContent = '글 수정';
    qs('#submitBtn').textContent = '수정하기';
    try {
      const post = await getPost(id);
      qs('#title').value = post.title || '';
      qs('#content').value = post.content || '';
    } catch (e) {
      toast(`불러오기 실패: ${e.message}`, 'error');
    }
  } else {
    qs('#mode').textContent = '새 글 작성';
    qs('#submitBtn').textContent = '등록하기';
  }
}

async function submitForm(e) {
  e.preventDefault();
  const title = qs('#title').value.trim();
  const content = qs('#content').value.trim();
  if (!title || !content) {
    toast('제목과 내용을 입력하세요', 'error');
    return;
  }
  try {
    if (id) {
      await updatePost(id, { title, content });
      toast('수정되었습니다');
    } else {
      await createPost({ title, content });
      toast('등록되었습니다');
    }
    setTimeout(() => (location.href = 'index.html'), 600);
  } catch (e) {
    toast(`저장 실패: ${e.message}`, 'error');
  }
}

qs('#form').addEventListener('submit', submitForm);
qs('#backBtn').addEventListener('click', () => (location.href = 'index.html'));

init();
