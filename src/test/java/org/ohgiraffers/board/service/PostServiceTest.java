package org.ohgiraffers.board.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ohgiraffers.board.domain.dto.*;
import org.ohgiraffers.board.domain.entity.Post;
import org.ohgiraffers.board.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    /** @Mock
     *  가짜 객체, 테스트 실행 시 실제가 아닌 Mock 객체를 반환한다.
     */
    @Mock
    private PostRepository postRepository;

    /** @InjectMocks
     *  Mock 객체가 주입될 클래스를 지정한다.
     */
    @InjectMocks
    private PostService postService;

    private Post savedPost;

    private Post post;

    private CreatePostRequest createPostRequest;

    private UpdatePostRequest updatePostRequest;

    @BeforeEach
    void setup() {
        // 초기화
        post = new Post(1L, "테스트 제목", "테스트 내용");
        savedPost = new Post(2L,"저장되어 있던 테스트 제목","저장되어 있던 테스트 내용");
        createPostRequest = new CreatePostRequest("테스트 제목", "테스트 내용");
        updatePostRequest = new UpdatePostRequest("변경된 테스트 제목", "변경된 테스트 내용");
    }

    @Test
    @DisplayName("게시글 작성 기능 테스트")
    void create_post_test() {
        // given
        // mockito 기본 형태
        // when(postRepository.save(any())).thenReturn(post);
        //BddMockito 형태로


        given(postRepository.save(any())).willReturn(post);
        // 가상의 형태로 만들었기 때문에 post형식으로 우린 받을 것이다?
        // 이 코드는 postRepository.save(any()) 메서드가 호출될 때 post 객체를 반환하도록 설정하는 것

        // when
        CreatePostResponse createPostResponse = postService.createPost(createPostRequest);
        // 가상 PostRepositoy 인터페이스의 의존성을 주입하여 postService 클래스를 사용할건데 그 안에 있는 createPost메서드를 호출한다.
        // 매개변수는 createPostRequest로 미리 초기화해둔 값을 매개변수로 대입한다.
        // given에 있는 post 객체를 반환하도록 설정하였으므로, postService.createPost 내의 post 객체를 반환하도록 하는 것
        // 그 후 CreatePostResponse 형식의 createPostResponse를 선언

        //then
        assertThat(createPostResponse.getPostId()).isEqualTo(1L);
        assertThat(createPostResponse.getTitle()).isEqualTo("테스트 제목");
        assertThat(createPostResponse.getContent()).isEqualTo("테스트 내용");
        // 그 후 미리 지정해둔 createPostRequest 가져온 값과 입력값이 같은지 전부 확인하는 테스트
    }

    @Test
    @DisplayName("postId로 게시글을 조회하는 기능 테스트")
    void read_post_by_id_1() {
        //given
        when(postRepository.findById(any())).thenReturn(Optional.of((savedPost)));
        //Optional.of 라이브러리로 생성되어있다. 실제로 findById가 있는 값인지 없는 값인지 모르기때문에 Optional.of를 사용한다.

        //when
        ReadPostResponse readPostResponse = postService.readPostById(savedPost.getPostId());

        //then
        assertThat(readPostResponse.getPostId()).isEqualTo(savedPost.getPostId());
        assertThat(readPostResponse.getTitle()).isEqualTo(savedPost.getTitle());
        assertThat(readPostResponse.getContent()).isEqualTo(savedPost.getContent());

    }

    @Test
    @DisplayName("postId로 게시글을 찾지 못했을 때, 지정한 Exception을 발생시키는지 테스트")
    void read_post_by_id_2() {
        //given
        when(postRepository.findById(any())).thenThrow(new EntityNotFoundException("해당 post로 조회된 게시글이 없습니다."));

        //when & then
        assertThrows(EntityNotFoundException.class, ()->
                postService.readPostById(1L)
        );

    }

    @Test
    @DisplayName("전체 게시글 조회 기능 테스트")
    void read_all_post() {
        //given
        Pageable pageable = PageRequest.of(0, 5);
        List<Post> posts = Arrays.asList(post, savedPost);
        Page<Post> postPage = new PageImpl<>(posts,pageable, posts.size());

        given(postRepository.findAll(pageable)).willReturn(postPage);

        //when
        Page<ReadPostResponse> responses = postService.readAllPost(pageable);

        //then
        assertThat(responses.getContent()).hasSize(2);
        assertThat(responses.getContent().get(0).getTitle()).isEqualTo("테스트 제목");
        assertThat(responses.getContent().get(0).getContent()).isEqualTo("테스트 내용");
        assertThat(responses.getContent().get(1).getTitle()).isEqualTo("저장되어 있던 테스트 제목");
        assertThat(responses.getContent().get(1).getContent()).isEqualTo("저장되어 있던 테스트 내용");

    }

    @Test
    @DisplayName("게시글 삭제 기능 테스트")
    void delete_post() {
        //given
        when(postRepository.findById(any())).thenReturn(Optional.of(savedPost));

        //when
        DeletePostResponse deletePostResponse = postService.deletePost(savedPost.getPostId());

        //then
        assertThat(deletePostResponse.getPostId()).isEqualTo(savedPost.getPostId());

    }

    @Test
    @DisplayName("게시글 수정 기능 테스트")
    void update_post() {
        //given
        when(postRepository.findById(any())).thenReturn(Optional.of(savedPost));
        // UpdatePostRequest request = new UpdatePostRequest("변경된 제목", "변경된 내용");

        //when
        UpdatePostResponse updatePostResponse = postService.updatePost(savedPost.getPostId(), updatePostRequest);

        //then
        assertThat(updatePostResponse.getPostId()).isEqualTo(savedPost.getPostId());
        assertThat(updatePostResponse.getTitle()).isEqualTo("변경된 테스트 제목");
        assertThat(updatePostResponse.getContent()).isEqualTo("변경된 테스트 내용");
    }
}
