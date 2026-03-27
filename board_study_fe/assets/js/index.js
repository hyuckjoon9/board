import { getPosts, getApiBase, setApiBase } from './api.js';
import { qs, qsa, enableCursorSpotlight, truncateText } from './utils.js';

let currentPage = 0;
let totalPages = 0;
let currentKeyword = '';

async function load(page = 0) {
  currentPage = page;
  const grid = qs('#grid');
  const pagination = qs('#pagination');
  grid.innerHTML =
    '<div class="card"><div class="content">불러오는 중...</div></div>';
  if (pagination) pagination.innerHTML = '';
  try {
    let response = await getPosts(page, currentKeyword);

    // 응답이 배열이면 그대로 사용, 객체면 content 필드에서 추출
    let posts = Array.isArray(response)
      ? response
      : response.content || response.data || [];

    const sizeGuess =
      response?.size || response?.pageable?.pageSize || posts.length || 10;
    const totalElements =
      response?.totalElements || response?.total || response?.total_count;
    totalPages = getTotalPages(response, posts.length, sizeGuess);

    if (!posts || posts.length === 0) {
      grid.innerHTML =
        '<div class="card"><div class="content">게시글이 없습니다.</div></div>';
      return;
    }
    grid.innerHTML = posts
      .map((p) => {
        const title = truncateText(p.title || '', 30);
        return `
      <div class="card clickable" data-id="${p.id}">
        <h3>${escapeHtml(title)}</h3>
      </div>
    `;
      })
      .join('');
    renderPagination(totalPages || Math.ceil(totalElements / sizeGuess) || 1);
    bindCardClicks();
    bindTiltToCards();
    bindPagination();
  } catch (e) {
    grid.innerHTML = `<div class="card"><div class="content">불러오기 실패: ${escapeHtml(
      e.message
    )}</div></div>`;
  }
}

function escapeHtml(str) {
  return String(str).replace(
    /[&<>"]/g,
    (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c])
  );
}

function getTotalPages(response, count, sizeGuess) {
  if (!response) return count ? 1 : 0;
  const candidates = [
    response.totalPages,
    response.total_pages,
    response.totalPage,
    response.total_page,
  ].filter((n) => Number.isInteger(n) && n >= 0);
  if (candidates.length > 0) return Math.max(...candidates);

  const totalElements =
    response.totalElements || response.total_count || response.total;
  if (Number.isInteger(totalElements) && sizeGuess) {
    return Math.max(1, Math.ceil(totalElements / sizeGuess));
  }
  return count ? 1 : 0;
}

function bindHeader() {
  qs('#newBtn').addEventListener('click', () => (location.href = 'post.html'));
  qs('#refreshBtn').addEventListener('click', () => load(currentPage));
  const baseInput = qs('#apiBase');
  baseInput.value = getApiBase();
  qs('#saveBase').addEventListener('click', () => {
    setApiBase(baseInput.value.trim());
    toast('API 주소가 저장되었습니다');
  });
}

function bindSearch() {
  const toggle = qs('#searchToggle');
  const panel = qs('#searchPanel');
  const input = qs('#searchInput');
  const searchBtn = qs('#searchBtn');
  const clearBtn = qs('#searchClear');

  const openPanel = () => {
    panel.classList.add('open');
    input.focus();
    input.select();
  };

  const closePanel = () => {
    panel.classList.remove('open');
  };

  const applySearch = () => {
    currentKeyword = input.value.trim();
    load(0);
  };

  toggle.addEventListener('click', (e) => {
    e.preventDefault();
    if (panel.classList.contains('open')) {
      closePanel();
    } else {
      openPanel();
    }
  });

  searchBtn.addEventListener('click', applySearch);
  clearBtn.addEventListener('click', () => {
    const hadKeyword = Boolean(currentKeyword);
    input.value = '';
    currentKeyword = '';
    if (hadKeyword) {
      load(0);
    }
  });

  input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      applySearch();
    }
  });

  document.addEventListener('click', (e) => {
    if (!panel.contains(e.target) && !toggle.contains(e.target)) {
      closePanel();
    }
  });
}

function bindGrid() {
  const grid = qs('#grid');
  grid.addEventListener('click', (e) => {
    const card = e.target.closest('.card.clickable');
    if (card) {
      const id = card.getAttribute('data-id');
      location.href = `view.html?id=${id}`;
    }
  });
}

function bindCardClicks() {
  qsa('.card.clickable').forEach((c) => {
    c.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        location.href = `view.html?id=${c.getAttribute('data-id')}`;
      }
    });
    c.setAttribute('tabindex', '0');
  });
}

function bindTiltToCards() {
  const max = 4;
  qsa('.card.clickable').forEach((card) => {
    card.addEventListener('mousemove', (e) => {
      const rect = card.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;
      const rx = (y / rect.height - 0.5) * -2 * max;
      const ry = (x / rect.width - 0.5) * 2 * max;
      card.style.transform = `perspective(800px) rotateX(${rx.toFixed(
        2
      )}deg) rotateY(${ry.toFixed(2)}deg)`;
    });
    card.addEventListener('mouseleave', () => {
      card.style.transform = 'perspective(800px) rotateX(0) rotateY(0)';
    });
  });
}

function renderPagination(total) {
  const pagination = qs('#pagination');
  if (!pagination) return;
  pagination.innerHTML = '';
  if (!total || total <= 1) return;

  const blockSize = 10;
  const currentBlock = Math.floor(currentPage / blockSize);
  const blockStart = currentBlock * blockSize;
  const blockEnd = Math.min(blockStart + blockSize, total);

  let html = '';
  if (blockStart > 0) {
    html += `<button class="button" data-page="${blockStart - 1}">◀</button>`;
  }

  for (let i = blockStart; i < blockEnd; i += 1) {
    const active = i === currentPage ? 'page-number active' : 'page-number';
    html += `<button class="button ${active}" data-page="${i}">${
      i + 1
    }</button>`;
  }

  if (blockEnd < total) {
    html += `<button class="button" data-page="${blockEnd}">▶</button>`;
  }

  pagination.innerHTML = html;
}

function bindPagination() {
  const pagination = qs('#pagination');
  if (!pagination || pagination.dataset.bound) return;
  pagination.dataset.bound = 'true';
  pagination.addEventListener('click', (e) => {
    const btn = e.target.closest('button[data-page]');
    if (btn) {
      const page = Number(btn.getAttribute('data-page'));
      if (!Number.isNaN(page)) load(page);
    }
  });
}

bindHeader();
bindSearch();
bindGrid();
load();
enableCursorSpotlight();
