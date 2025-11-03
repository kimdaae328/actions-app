const logoutLink = document.querySelector("a.nav-link");

logoutLink.addEventListener("click", async (e) => {
    e.preventDefault();
    await memberService.logout()
    location.href = "/member/login";
});