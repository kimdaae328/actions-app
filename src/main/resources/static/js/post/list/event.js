memberService.info((member) => {
    const span = document.querySelector("span.nav-user");
    span.innerText = `${member.memberName}님`;
}).catch(e => console.log(e));