package com.example.app.controller;

import com.example.app.auth.CustomUserDetails;
import com.example.app.common.search.Search;
import com.example.app.dto.MemberDTO;
import com.example.app.dto.PostDTO;
import com.example.app.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;

    @GetMapping("list/{page}")
    public String list(@PathVariable int page, Search search, Model model
                       /*@AuthenticationPrincipal CustomUserDetails customUserDetails*/) {
        model.addAttribute("postsCriteria", postService.list(page, search));
        model.addAttribute("search", search);
//        model.addAttribute("member", customUserDetails);
        return "post/list";
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable Long id, @RequestParam(defaultValue = "1") int page, Model model) {
        PostDTO post = postService.getPost(id);
        postService.setPreSignedUrl(post);
        model.addAttribute("post", post);
        model.addAttribute("page", page);
        return "post/detail";
    }

    @GetMapping("/write")
    public String goToWriteForm(PostDTO postDTO, @RequestParam(defaultValue = "1") int page, Model model) {
        model.addAttribute("post", postDTO);
        model.addAttribute("page", page);
        return "post/write";
    }

    @PostMapping("/write")
    public RedirectView write(PostDTO postDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam("files") List<MultipartFile> multipartFiles) {
        postDTO.setMemberId(customUserDetails.getId());
        postService.write(postDTO, multipartFiles);

        return new RedirectView("/post/detail/" + postDTO.getId());
    }

    @GetMapping("/delete/{id}")
    public RedirectView delete(@PathVariable Long id) {
        postService.delete(id);
        return new RedirectView("/post/list/1");
    }

    @GetMapping("/{id}")
    public String goToModifyForm(@PathVariable Long id, @RequestParam(defaultValue = "1") int page, Model model, HttpServletRequest request) {
        PostDTO post = postService.getPost(id);
        postService.setPreSignedUrl(post);
        model.addAttribute("post", post);
        model.addAttribute("page", page);
        model.addAttribute("member", (MemberDTO) request.getAttribute("member"));
        return "post/update";
    }

    @PostMapping("/{id}")
    public RedirectView modify(@PathVariable Long id,
                               @RequestParam(defaultValue = "1") int page,
                               PostDTO postDTO,
                               Long[] deleteFileIds,
                               @RequestParam("files") List<MultipartFile> multipartFiles) {
        postDTO.setId(id);
        postService.update(postDTO, deleteFileIds, multipartFiles);
        return new RedirectView("/post/detail/" + id + "?page=" + page);
    }
}
