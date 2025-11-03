const deleteButton = document.querySelector("button.btn-delete");
deleteButton.addEventListener("click", () => {
    location.href = "/post/delete/" + postId;
});


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

const imageButtons = document.querySelectorAll("button.thumb");
imageButtons.forEach(button => {
    button.addEventListener("click", async (e) => {
        const response = await fetch(`/files/download/${e.target.closest("button").dataset.id}`);
        location.href = await response.text();
    })
})
