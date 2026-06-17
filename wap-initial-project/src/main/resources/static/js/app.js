(function(){
  // Frontend integration z backendem: paginacja, filtry, upload, download
  const assetsEl = document.getElementById('assets');
  const countEl = document.getElementById('count');
  const searchInput = document.getElementById('searchInput');
  const refreshBtn = document.getElementById('refreshBtn');
  const gridViewBtn = document.getElementById('gridView');
  const listViewBtn = document.getElementById('listView');
  const modal = document.getElementById('modal');
  const modalBody = document.getElementById('modalBody');
  const closeModal = document.getElementById('closeModal');
  const uploadForm = document.getElementById('uploadForm');
  const categoriesList = document.getElementById('categoriesList');
  const tagsList = document.getElementById('tagsList');
  const paginationEl = document.getElementById('pagination');
  const template = document.getElementById('assetCardTemplate');

  let assets = [];
  let page = 0;
  const size = 12;
  let total = 0;
  let currentCategory = null;
  let currentTag = null;

  async function loadAssets(p = 0){
    page = p;
    const q = encodeURIComponent(searchInput.value.trim());
    const params = [`page=${page}`, `size=${size}`];
    if(q) params.push(`q=${q}`);
    if(currentCategory) params.push(`category=${currentCategory}`);
    if(currentTag) params.push(`tag=${encodeURIComponent(currentTag)}`);
    const url = '/api/assets?' + params.join('&');
    try{
      const res = await fetch(url);
      if(!res.ok) throw new Error('Network');
      const data = await res.json();
      assets = data.content || data;
      total = data.totalElements || assets.length;
      renderAssets();
      renderPagination();
    }catch(err){
      console.warn('Błąd pobierania assets, używam lokalnych mocków', err);
      assets = [];
      total = 0;
      renderAssets();
    }
  }

  async function loadFilters(){
    try{
      const [catsRes, tagsRes] = await Promise.all([fetch('/api/categories'), fetch('/api/tags')]);
      if(catsRes.ok){
        const cats = await catsRes.json();
        categoriesList.innerHTML = '';
        const li = document.createElement('li');
        li.innerHTML = `<button data-cat="">Wszystkie</button>`;
        categoriesList.appendChild(li);
        cats.forEach(c=>{
          const item = document.createElement('li');
          item.innerHTML = `<button data-cat="${c.id}">${escapeHtml(c.name)}</button>`;
          categoriesList.appendChild(item);
        });
        categoriesList.querySelectorAll('button').forEach(b=> b.addEventListener('click', ()=>{ currentCategory = b.dataset.cat || null; loadAssets(0); }));
      }
      if(tagsRes.ok){
        const tags = await tagsRes.json();
        tagsList.innerHTML = '';
        const all = document.createElement('li'); all.innerHTML = `<button data-tag="">Wszystkie</button>`; tagsList.appendChild(all);
        tags.forEach(t=>{ const item = document.createElement('li'); item.innerHTML = `<button data-tag="${escapeHtml(t.name)}">${escapeHtml(t.name)}</button>`; tagsList.appendChild(item); });
        tagsList.querySelectorAll('button').forEach(b=> b.addEventListener('click', ()=>{ currentTag = b.dataset.tag || null; loadAssets(0); }));
      }
    }catch(e){ console.warn('Nie udało się załadować filtrów', e); }
  }

  function renderAssets(){
    assetsEl.innerHTML = '';
    countEl.textContent = total;
    for(const a of assets){
      const node = template.content.cloneNode(true);
      const img = node.querySelector('.asset-thumb');
      const title = node.querySelector('.asset-title');
      const meta = node.querySelector('.asset-meta');
      const viewBtn = node.querySelector('.viewBtn');
      const dlBtn = node.querySelector('.downloadBtn');

      if (a.mimeType && a.mimeType.startsWith('image/')) {
        // Jeśli plik to obraz, pobierz go bezpiecznie z endpointu
        img.src = `/api/assets/${a.id}/download`;
      } else {
        // Generowanie losowej grafiki zastępczej z ID dla innych plików np. PDF, Wideo
        img.src = `https://picsum.photos/320/200?random=${a.id}`;
      }
      title.textContent = a.title;
      meta.textContent = `${a.mimeType || ''} • ${a.fileSizeBytes || ''}`;

      viewBtn.addEventListener('click', ()=> openModal(a));
      dlBtn.addEventListener('click', ()=> downloadAsset(a));

      assetsEl.appendChild(node);
    }
  }

  function renderPagination(){
    paginationEl.innerHTML = '';
    const pages = Math.max(1, Math.ceil(total/size));
    for(let i=0;i<pages;i++){
      const btn = document.createElement('button');
      btn.textContent = (i+1).toString();
      if(i===page) btn.disabled = true;
      btn.addEventListener('click', ()=> loadAssets(i));
      paginationEl.appendChild(btn);
    }
  }

  function openModal(asset){
    modal.classList.remove('hidden');
    modalBody.innerHTML = `<h2>${escapeHtml(asset.title)}</h2>` +
      (asset.mimeType && asset.mimeType.startsWith('image') ? `<img src="${asset.storagePath || asset.thumbnailPath}" alt="${escapeHtml(asset.title)}" style="max-width:100%"/>` : `<pre>${escapeHtml(JSON.stringify(asset, null, 2))}</pre>`);
  }

  function closeModalFn(){ modal.classList.add('hidden'); modalBody.innerHTML = ''; }

  async function downloadAsset(a){
    try{
      const res = await fetch(`/api/assets/${a.id}/download`);
      if(!res.ok) throw new Error('download failed');
      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a'); link.href = url; link.download = a.originalFilename || a.title || 'download'; document.body.appendChild(link); link.click(); link.remove(); URL.revokeObjectURL(url);
    }catch(e){ alert('Nie udało się pobrać pliku'); }
  }

  function escapeHtml(s){ return String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":"&#39;"})[c]); }

  // Events
  searchInput.addEventListener('input', ()=> loadAssets(0));
  refreshBtn.addEventListener('click', ()=> loadAssets(page));
  gridViewBtn.addEventListener('click', ()=>{ assetsEl.className='assets grid'; gridViewBtn.classList.add('active'); listViewBtn.classList.remove('active'); });
  listViewBtn.addEventListener('click', ()=>{ assetsEl.className='assets list'; listViewBtn.classList.add('active'); gridViewBtn.classList.remove('active'); });
  closeModal.addEventListener('click', closeModalFn);
  modal.addEventListener('click', e=>{ if(e.target===modal) closeModalFn(); });

  uploadForm.addEventListener('submit', async (e)=>{
    e.preventDefault();
    const file = document.getElementById('fileInput').files[0];
    if(!file){ alert('Wybierz plik'); return; }
    const title = document.getElementById('titleInput').value || file.name;
    const ownerId = document.getElementById('ownerInput').value || '1';
    const fd = new FormData(); fd.append('file', file); fd.append('title', title); fd.append('ownerId', ownerId);
    try{
      const res = await fetch('/api/assets/upload', { method: 'POST', body: fd });
      if(!res.ok) throw new Error('upload failed');
      await loadAssets(0);
      await loadFilters();
      alert('Plik przesłany');
    }catch(err){
      console.error('Upload error', err); alert('Upload nie powiódł się');
    }
  });

  async function init(){ await loadFilters(); await loadAssets(0); }
  init();
})();


