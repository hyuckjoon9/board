import { getPost, deletePost, createComment, deleteComment } from './api.js';
import {
  qs,
  toast,
  getQueryParam,
  confirmDialog,
  enableCursorSpotlight,
} from './utils.js';

const id = getQueryParam('id');
let currentComments = [];

async function load() {
  const box = qs('#detail');
  if (!id) {
    box.innerHTML =
      '<div class="content">????????????? ID?? ??????.</div>';
    return;
  }
  qs('#detailTitle').textContent = 'Loading...';
  qs('#detailContent').textContent = '';
  qs('#commentsList').innerHTML = '';
  try {
    const post = await getPost(id);
    qs('#detailTitle').textContent = post.title || '';
    qs('#detailContent').textContent = post.content || '';
    currentComments = Array.isArray(post.comments) ? post.comments : [];
    renderComments();
  } catch (e) {
    box.innerHTML = `<div class="content">?????? ???: ${escapeHtml(
      e.message
    )}</div>`;
  }
}

function escapeHtml(str) {
  return String(str).replace(
    /[&<>\"]/g,
    (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c])
  );
}

function bindActions() {
  qs('#listBtn').addEventListener(
    'click',
    () => (location.href = 'index.html')
  );
  qs('#editBtn').addEventListener(
    'click',
    () => (location.href = `post.html?id=${id}`)
  );
  qs('#deleteBtn').addEventListener('click', async () => {
    if (!confirmDialog('정말 삭제하시겠습니까?')) return;
    try {
      await deletePost(id);
      toast('삭제되었습니다');
      setTimeout(() => (location.href = 'index.html'), 600);
    } catch (e) {
      toast(`삭제 실패: ${e.message}`, 'error');
    }
  });
}

function renderComments() {
  const list = qs('#commentsList');
  if (!currentComments || currentComments.length === 0) {
    list.innerHTML = '';
    list.style.display = 'none';
    return;
  }
  list.style.display = 'flex';
  list.innerHTML = currentComments
    .map(
      (comment) =>
        `<div class="comment-item" data-id="${comment.id}">
          <div class="comment-content">${escapeHtml(comment.content || '')}</div>
          <button class="comment-delete" type="button">삭제</button>
        </div>`
    )
    .join('');
}

function bindCommentForm() {
  const input = qs('#commentInput');
  const submit = qs('#commentSubmit');

  const submitComment = async () => {
    const content = input.value.trim();
    if (!content) {
      toast('?? ??? ??? ???.', 'error');
      return;
    }
    submit.disabled = true;
    try {
      const created = await createComment(id, { content });
      if (created && (created.content || created.id)) {
        currentComments = [...currentComments, created];
        renderComments();
      } else {
        await load();
      }
      input.value = '';
    } catch (e) {
      toast(`?? ?? ??: ${e.message}`, 'error');
    } finally {
      submit.disabled = false;
    }
  };

  submit.addEventListener('click', submitComment);
  input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      submitComment();
    }
  });
}

function bindCommentDelete() {
  const list = qs('#commentsList');
  list.addEventListener('click', async (e) => {
    const button = e.target.closest('.comment-delete');
    if (!button) return;
    const item = button.closest('.comment-item');
    if (!item) return;
    const commentId = item.getAttribute('data-id');
    if (!commentId) return;
    if (!confirmDialog('댓글을 삭제하시겠습니까?')) return;

    button.disabled = true;
    try {
      await deleteComment(id, commentId);
      currentComments = currentComments.filter(
        (comment) => String(comment.id) !== String(commentId)
      );
      renderComments();
    } catch (e) {
      toast(`댓글 삭제 실패: ${e.message}`, 'error');
      button.disabled = false;
    }
  });
}

bindActions();
bindCommentForm();
bindCommentDelete();
load();
enableCursorSpotlight();
