package com.efub_assignment.community.community.post.service;

import com.efub_assignment.community.community.member.domain.Member;
import com.efub_assignment.community.community.member.repository.MemberRepository;
import com.efub_assignment.community.community.post.domain.Post;
import com.efub_assignment.community.community.post.dto.request.PostCreateRequest;
import com.efub_assignment.community.community.post.dto.request.PostUpdateRequest;
import com.efub_assignment.community.community.post.dto.response.PostListResponse;
import com.efub_assignment.community.community.post.dto.response.PostResponse;
import com.efub_assignment.community.community.post.dto.summary.PostSummary;
import com.efub_assignment.community.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createPost(PostCreateRequest postCreateRequest){
        Long memberId =postCreateRequest.memberId();
        Member writer = findByMemberId(memberId);
        Post newPost = postCreateRequest.toEntity(writer);
        postRepository.save(newPost);
        return newPost.getId();
    }

    @Transactional
    public PostResponse getPost(Long postId){
        postRepository.increaseViewCount(postId);
        Post post = findByPostId(postId);
        return PostResponse.from(post);
    }

    @Transactional(readOnly = true)
    public PostListResponse getAllPosts(){
        List<PostSummary> postSummaries = postRepository.findByOrderByCreatedAtDesc().stream()
                .map(PostSummary::from).toList();
        return new PostListResponse(postSummaries, postRepository.count());
    }

    @Transactional
    public void updatePostContent(Long postId, PostUpdateRequest request, Long memberId, String password){
        Post post = findByPostId(postId);
        Member member = findByMemberId(memberId);
        authorizePostWriter(post, member, password);
        post.changeContent(request.content());
    }

    @Transactional
    public void deletePost(Long postId, Long memberId, String password){
        Post post = findByPostId(postId);
        Member member = findByMemberId(memberId);
        authorizePostWriter(post, member, password);
        postRepository.delete(post);
    }

    private Post findByPostId(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(()-> new NoSuchElementException("게시글을 찾을 수 없습니다."));
    }

    private Member findByMemberId(Long memberId){
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new NoSuchElementException("회원을 찾을 수 없습니다."));
    }

    private void authorizePostWriter(Post post, Member member, String password){
        if(!post.getWriter().equals(member) || !post.getWriter().getPassword().equals(password)){
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }
    }
}

