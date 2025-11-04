package com.efub.post;

import com.efub.domain.Post;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final EntityManager em;

    @Transactional
    public Post create(Post post){
        em.persist(post);
        return post;
    }

    public Post find(Long id){
        return em.find(Post.class, id);
    }
}
