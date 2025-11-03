const postEmpty = document.getElementById("attachmentsEmpty");
postEmpty.style.display = postFiles.length === 0 ? "block" : "none";

const imageGallery = document.getElementById("imageGallery");
let text = ``;
postFiles.forEach(file => {
    text += `
        <li>
            <button type="button" data-id="${file.id}" class="thumb">
                <img src="${file.postFilePath}"/>
            </button>
        </li>
    `;
});
imageGallery.innerHTML = text;

const deleteFiles = [];
const imageButtons = document.querySelectorAll("button.thumb");
imageButtons.forEach(button => {
    button.addEventListener("click", (e) => {
        const attachmentTitle = document.getElementById("attachments-title");
        deleteFiles.push(button.dataset.id);
        e.target.closest("li").remove();
        attachmentTitle.style.display =
            document.getElementById("imageGallery").childElementCount === 0 ? "none" : "block";
    });
});

const submitButton = document.querySelector("button.btn-submit");
submitButton.addEventListener("click", (e) => {
    const deleteFilesWrap = document.getElementById("deleteFiles");
    let text = ``;
    deleteFiles.forEach(id => {
         text += `<input type="hidden" name="deleteFileIds" value="${id}">`;
    })
    deleteFilesWrap.innerHTML = text;
    document.updateForm.submit();
});

