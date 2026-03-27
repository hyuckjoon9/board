export const qs = (sel, scope = document) => scope.querySelector(sel);
export const qsa = (sel, scope = document) =>
  Array.from(scope.querySelectorAll(sel));

export function toast(message, type = 'success', timeout = 2000) {
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.textContent = message;
  document.body.appendChild(el);
  setTimeout(() => {
    el.remove();
  }, timeout);
}

export function getQueryParam(name) {
  return new URLSearchParams(location.search).get(name);
}

export function confirmDialog(message) {
  return window.confirm(message);
}

export function enableCursorSpotlight() {
  const root = document.documentElement;
  function onMove(e) {
    const x = (e.clientX / window.innerWidth) * 100;
    const y = (e.clientY / window.innerHeight) * 100;
    root.style.setProperty('--mx', `${x}%`);
    root.style.setProperty('--my', `${y}%`);
  }
  window.addEventListener('mousemove', onMove);
}

export function truncateText(text, max = 10) {
  const s = text == null ? '' : String(text);
  return s.length > max ? s.slice(0, max) + '...' : s;
}
