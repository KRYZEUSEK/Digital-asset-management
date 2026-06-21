(function(){
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
  const categorySelect = document.getElementById('categorySelect');
  const uploadTagsList = document.getElementById('uploadTagsList');
  const categoryForm = document.getElementById('categoryForm');
  const tagForm = document.getElementById('tagForm');
  const newCategoryName = document.getElementById('newCategoryName');
  const newTagName = document.getElementById('newTagName');
  const categoriesList = document.getElementById('categoriesList');
  const tagsList = document.getElementById('tagsList');
  const paginationEl = document.getElementById('pagination');
  const template = document.getElementById('assetCardTemplate');

  let assets = [];
  let categories = [];
  let tags = [];
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
    try{
      const res = await fetch('/api/assets?' + params.join('&'));
      if(!res.ok) throw new Error('Network');
      const data = await res.json();
      assets = data.content || data;
      total = data.totalElements || assets.length;
      renderAssets();
      renderPagination();
    }catch(err){
      console.warn('Blad pobierania assetow', err);
      assets = [];
      total = 0;
      renderAssets();
      renderPagination();
    }
  }

  async function loadFilters(){
    try{
      const [catsRes, tagsRes] = await Promise.all([fetch('/api/categories'), fetch('/api/tags')]);
      categories = catsRes.ok ? await catsRes.json() : [];
      tags = tagsRes.ok ? await tagsRes.json() : [];
      renderFilters();
      renderUploadTaxonomy();
    }catch(e){
      console.warn('Nie udalo sie zaladowac filtrow', e);
    }
  }

  function renderFilters(){
    categoriesList.innerHTML = '';
    const allCategories = document.createElement('li');
    allCategories.innerHTML = `<button data-cat="" class="${currentCategory ? '' : 'active-filter'}">Wszystkie</button>`;
    categoriesList.appendChild(allCategories);
    categories.forEach(category => {
      const item = document.createElement('li');
      item.innerHTML = `<button data-cat="${category.id}" class="${String(currentCategory) === String(category.id) ? 'active-filter' : ''}">${escapeHtml(category.name)}</button>`;
      categoriesList.appendChild(item);
    });
    categoriesList.querySelectorAll('button').forEach(button => button.addEventListener('click', () => {
      currentCategory = button.dataset.cat || null;
      renderFilters();
      loadAssets(0);
    }));

    tagsList.innerHTML = '';
    const allTags = document.createElement('li');
    allTags.innerHTML = `<button data-tag="" class="${currentTag ? '' : 'active-filter'}">Wszystkie</button>`;
    tagsList.appendChild(allTags);
    tags.forEach(tag => {
      const item = document.createElement('li');
      item.innerHTML = `<button data-tag="${escapeHtml(tag.name)}" class="${currentTag === tag.name ? 'active-filter' : ''}">${escapeHtml(tag.name)}</button>`;
      tagsList.appendChild(item);
    });
    tagsList.querySelectorAll('button').forEach(button => button.addEventListener('click', () => {
      currentTag = button.dataset.tag || null;
      renderFilters();
      loadAssets(0);
    }));
  }

  function renderUploadTaxonomy(){
    const selectedCategory = categorySelect.value;
    categorySelect.innerHTML = '<option value="">Bez kategorii</option>';
    categories.forEach(category => {
      const option = document.createElement('option');
      option.value = category.id;
      option.textContent = category.name;
      categorySelect.appendChild(option);
    });
    categorySelect.value = selectedCategory;

    const checkedIds = new Set(Array.from(uploadTagsList.querySelectorAll('input:checked')).map(input => input.value));
    uploadTagsList.innerHTML = '';
    tags.forEach(tag => {
      const label = document.createElement('label');
      label.className = 'checkbox-item';
      label.innerHTML = `<input type="checkbox" name="tagIds" value="${tag.id}" ${checkedIds.has(String(tag.id)) ? 'checked' : ''} /> <span>${escapeHtml(tag.name)}</span>`;
      uploadTagsList.appendChild(label);
    });
  }

  function renderAssets(){
    assetsEl.innerHTML = '';
    countEl.textContent = total;
    for(const asset of assets){
      const node = template.content.cloneNode(true);
      const img = node.querySelector('.asset-thumb');
      const title = node.querySelector('.asset-title');
      const meta = node.querySelector('.asset-meta');
      const viewBtn = node.querySelector('.viewBtn');
      const dlBtn = node.querySelector('.downloadBtn');

      img.src = isImage(asset) ? previewUrl(asset) : placeholderUrl(asset);
      img.onerror = () => {
        img.onerror = null;
        img.src = placeholderUrl(asset);
      };
      img.alt = asset.title || 'miniatura';
      title.textContent = asset.title || asset.originalFilename || 'Bez tytulu';
      meta.textContent = assetMeta(asset);

      viewBtn.addEventListener('click', () => openModal(asset));
      dlBtn.addEventListener('click', () => downloadAsset(asset));
      assetsEl.appendChild(node);
    }
  }

  function renderPagination(){
    paginationEl.innerHTML = '';
    const pages = Math.max(1, Math.ceil(total / size));
    for(let i = 0; i < pages; i++){
      const btn = document.createElement('button');
      btn.textContent = (i + 1).toString();
      if(i === page) btn.disabled = true;
      btn.addEventListener('click', () => loadAssets(i));
      paginationEl.appendChild(btn);
    }
  }

  function openModal(asset){
    modal.classList.remove('hidden');
    const url = previewUrl(asset);
    let preview = `<a class="preview-link" href="${url}" target="_blank" rel="noopener">Otworz podglad</a>`;
    if(isImage(asset)){
      preview = `<img class="asset-preview" src="${url}" alt="${escapeHtml(asset.title || asset.originalFilename)}" onerror="this.replaceWith(document.createTextNode('Podglad pliku jest niedostepny'))" />`;
    }else if(asset.mimeType && asset.mimeType.startsWith('video/')){
      preview = `<video class="asset-preview" src="${url}" controls></video>`;
    }else if(asset.mimeType === 'application/pdf'){
      preview = `<iframe class="asset-preview-frame" src="${url}" title="${escapeHtml(asset.title || asset.originalFilename)}"></iframe>`;
    }
    modalBody.innerHTML = `<h2>${escapeHtml(asset.title || asset.originalFilename || 'Asset')}</h2>${preview}<dl class="asset-details">${assetDetails(asset)}</dl>`;
  }

  function closeModalFn(){
    modal.classList.add('hidden');
    modalBody.innerHTML = '';
  }

  async function downloadAsset(asset){
    try{
      const res = await fetch(`/api/assets/${asset.id}/download`);
      if(!res.ok) throw new Error('download failed');
      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = asset.originalFilename || asset.title || 'download';
      document.body.appendChild(link);
      link.click();
      link.remove();
      URL.revokeObjectURL(url);
    }catch(e){
      alert('Nie udalo sie pobrac pliku');
    }
  }

  async function createCategory(name){
    const res = await fetch('/api/categories', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({name})
    });
    if(!res.ok) throw new Error('category create failed');
    return res.json();
  }

  async function createTag(name){
    const res = await fetch('/api/tags', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({name})
    });
    if(!res.ok) throw new Error('tag create failed');
    return res.json();
  }

  function isImage(asset){
    return asset.mimeType && asset.mimeType.startsWith('image/');
  }

  function previewUrl(asset){
    const version = encodeURIComponent(asset.updatedAt || asset.versionNumber || asset.fileSizeBytes || Date.now());
    return `/api/assets/${asset.id}/preview?v=${version}`;
  }

  function placeholderUrl(asset){
    const label = encodeURIComponent((asset.type || asset.mimeType || 'FILE').toString().slice(0, 12));
    return `https://placehold.co/320x200/e5e7eb/374151?text=${label}`;
  }

  function assetMeta(asset){
    const parts = [asset.mimeType, formatBytes(asset.fileSizeBytes), categoryNameById(asset.categoryId)];
    if(asset.tagNames && asset.tagNames.length) parts.push(asset.tagNames.join(', '));
    return parts.filter(Boolean).join(' | ');
  }

  function assetDetails(asset){
    const rows = [
      ['Plik', asset.originalFilename || '-'],
      ['Typ', asset.mimeType || '-'],
      ['Rozmiar', formatBytes(asset.fileSizeBytes) || '-'],
      ['Kategoria', categoryNameById(asset.categoryId) || 'Brak'],
      ['Tagi', asset.tagNames && asset.tagNames.length ? asset.tagNames.join(', ') : 'Brak']
    ];
    return rows.map(([label, value]) => `<dt>${escapeHtml(label)}</dt><dd>${escapeHtml(value)}</dd>`).join('');
  }

  function categoryNameById(id){
    const category = categories.find(item => String(item.id) === String(id));
    return category ? category.name : '';
  }

  function formatBytes(bytes){
    if(bytes === null || bytes === undefined || bytes === '') return '';
    const value = Number(bytes);
    if(Number.isNaN(value)) return String(bytes);
    if(value < 1024) return `${value} B`;
    if(value < 1024 * 1024) return `${(value / 1024).toFixed(1)} KB`;
    return `${(value / 1024 / 1024).toFixed(1)} MB`;
  }

  function escapeHtml(value){
    return String(value ?? '').replace(/[&<>"']/g, char => ({
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#39;'
    })[char]);
  }

  searchInput.addEventListener('input', () => loadAssets(0));
  refreshBtn.addEventListener('click', () => loadAssets(page));
  gridViewBtn.addEventListener('click', () => {
    assetsEl.className = 'assets grid';
    gridViewBtn.classList.add('active');
    listViewBtn.classList.remove('active');
  });
  listViewBtn.addEventListener('click', () => {
    assetsEl.className = 'assets list';
    listViewBtn.classList.add('active');
    gridViewBtn.classList.remove('active');
  });
  closeModal.addEventListener('click', closeModalFn);
  modal.addEventListener('click', event => {
    if(event.target === modal) closeModalFn();
  });

  categoryForm.addEventListener('submit', async event => {
    event.preventDefault();
    const name = newCategoryName.value.trim();
    if(!name) return;
    try{
      await createCategory(name);
      newCategoryName.value = '';
      await loadFilters();
    }catch(err){
      console.error('Category create error', err);
      alert('Nie udalo sie dodac kategorii');
    }
  });

  tagForm.addEventListener('submit', async event => {
    event.preventDefault();
    const name = newTagName.value.trim();
    if(!name) return;
    try{
      await createTag(name);
      newTagName.value = '';
      await loadFilters();
    }catch(err){
      console.error('Tag create error', err);
      alert('Nie udalo sie dodac tagu');
    }
  });

  uploadForm.addEventListener('submit', async event => {
    event.preventDefault();
    const file = document.getElementById('fileInput').files[0];
    if(!file){
      alert('Wybierz plik');
      return;
    }
    const title = document.getElementById('titleInput').value || file.name;
    const ownerId = document.getElementById('ownerInput').value || '1';
    const fd = new FormData();
    fd.append('file', file);
    fd.append('title', title);
    fd.append('ownerId', ownerId);
    if(categorySelect.value) fd.append('categoryId', categorySelect.value);
    uploadTagsList.querySelectorAll('input:checked').forEach(input => fd.append('tagIds', input.value));

    try{
      const res = await fetch('/api/assets/upload', { method: 'POST', body: fd });
      if(!res.ok) throw new Error('upload failed');
      uploadForm.reset();
      await loadFilters();
      await loadAssets(0);
      alert('Plik przeslany');
    }catch(err){
      console.error('Upload error', err);
      alert('Upload nie powiodl sie');
    }
  });

  async function init(){
    await loadFilters();
    await loadAssets(0);
  }

  init();
})();
