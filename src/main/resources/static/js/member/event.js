const button = document.querySelector('.signup-button');
button.addEventListener('click', async () => {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const remember = document.querySelector("input[name='remember']");
    const result = await memberService.login({memberEmail: email, memberPassword: password, remember: remember.checked});
    if(result.accessToken){
        location.href = '/post/list/1';
    }
});

// 카카오 로그인
const kakaoLoginButton = document.getElementById("kakao-login");
kakaoLoginButton.addEventListener("click", (e) => {
    window.location.href = "/oauth2/authorization/kakao";
});

// 네이버 로그인
const naverLoginButton = document.getElementById("naver-login");
naverLoginButton.addEventListener("click", (e) => {
    window.location.href = "/oauth2/authorization/naver";
});

// 구글 로그인
const googleLoginButton = document.getElementById("google-login");
googleLoginButton.addEventListener("click", (e) => {
    e.preventDefault();
    window.location.href = "/oauth2/authorization/google";
});
