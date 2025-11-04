package com.efub.post;

import com.efub.domain.Post; //blog-core에 있던 Post가 등장
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public String create(@RequestBody Post post){
        Post requestPost = postService.create(post);
        return "id : "+ requestPost.getId() + "name:"+requestPost.getTitle()+
                "content:" + requestPost.getContent();
    }

    @GetMapping("/{id}")
    public Post find(@PathVariable Long id ){
        return postService.find(id);
    }
}
