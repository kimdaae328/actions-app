// javascript
(() => {
    const input = document.getElementById('attachments');
    const addBtn = document.getElementById('attachAddBtn');
    const listEl = document.getElementById('attachFileList');
    const uploader = document.getElementById('attachUploader');

    const MAX_FILES = 10;
    const MAX_SIZE = 20 * 1024 * 1024; // 20MB
    const ALLOW_ALL = true; // 필요 시 타입 제한 로직 추가 가능

    // 선택된 파일을 유지/재구성하기 위한 버퍼
    let fileBuffer = [];

    const toKey = (f) => `${f.name}|${f.size}|${f.lastModified}`;

    const syncInput = () => {
        const dt = new DataTransfer();
        fileBuffer.forEach(f => dt.items.add(f));
        input.files = dt.files;
    };

    const extOf = (name) => {
        const idx = name.lastIndexOf('.');
        return idx > -1 ? name.slice(idx + 1).toUpperCase() : 'FILE';
    };

    const render = () => {
        listEl.innerHTML = '';
        fileBuffer.forEach((file, idx) => {
            const item = document.createElement('div');
            item.className = 'attach-item';

            if (file.type && file.type.startsWith('image/')) {
                const img = document.createElement('img');
                img.className = 'attach-thumb';
                const reader = new FileReader();
                reader.onload = (e) => { img.src = e.target.result; };
                reader.readAsDataURL(file);
                item.appendChild(img);
            } else {
                const box = document.createElement('div');
                box.className = 'attach-generic';
                box.textContent = extOf(file.name);
                item.appendChild(box);
            }

            const rm = document.createElement('button');
            rm.type = 'button';
            rm.className = 'attach-remove';
            rm.innerHTML = `
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                              <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                            </svg>
                          `;
            rm.addEventListener('click', () => {
                fileBuffer.splice(idx, 1);
                syncInput();
                render();
            });

            item.appendChild(rm);
            listEl.appendChild(item);
        });
    };

    const addFiles = (files) => {
        // map(toKey): 기존 파일객체를 "a.jpg|1000|1690000000000" 문자열 형태로 변경
        const existingKeys = new Set(fileBuffer.map(toKey));
        const arFile = Array.from(files);

        for (const f of arFile) {
            if (fileBuffer.length >= MAX_FILES) {
                alert(`최대 ${MAX_FILES}개까지 업로드할 수 있습니다.`);
                break;
            }
            if (f.size > MAX_SIZE) {
                alert(`"${f.name}" 파일이 용량 제한(20MB)을 초과했습니다.`);
                continue;
            }
            if (!ALLOW_ALL) {
                // 타입 제한이 필요하면 여기서 f.type 검사
                console.log(f.type);
            }
            if (existingKeys.has(toKey(f))) {
                // 중복 방지
                continue;
            }
            fileBuffer.push(f);
            existingKeys.add(toKey(f));
        }
        syncInput();
        render();
    };

    // 이벤트 바인딩
    addBtn.addEventListener('click', () => input.click());
    input.addEventListener('change', () => addFiles(input.files));
})();