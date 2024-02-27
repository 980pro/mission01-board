package org.ohgiraffers.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ohgiraffers.board.domain.dto.*;
import org.ohgiraffers.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/** 통합테스트 & 단위테스트
 *  통합테스트 : 모듈을 통합하는 과정에서 모듈 간의 호환성을 확인하기 위해 수행되는 테스트
 *  통합테스트는 API의 모든 과정이 올바르게 동작하는지 확인하는 것
 *
 *  단위(유닛)테스트 :
 *  하나의 모듈을 기준으로 독립적으로 진행되는 가장 작은 단위의 테스트(작은 단위 = 함수, 메서드)
 *  단위테스트는 애플리케이션을 구성하는 하나의 기능이 올바르게 동작하는지 독립적으로 테스트하는 것
 */

/** @WebMvcTest
 * MVC를 위한 테스트, 컨트롤러가 예상대로 동작하는지 테스트하는데 사용된다..
 * 웹 애플리케이션을 애플리케이션 서버에 배포하지 않고 테스트용 MVC 환경을 만들어 요청 및 전송 응답 기능을 제공해준다.
 */
@WebMvcTest(PostController.class)
public class PostControllerTest {

    /** @AutoWired
     *  의존성 주입(DI)을 할 때, 사용하는 필요한 의존 객체 타입이 해당하는 빈을 찾아 주입한다.
     *  의존성 : 하나의 코드가 다른 코드에 의존하는 상태를 뜻한다.
     */
    @Autowired
    MockMvc mockMvc;

    /** @MockBean
     *  스프링의 의존성 주입 기능을 사용해 테스트 실행 시 실제 빈 대신에
     *  Mockito로 생성된 모의 객체를 스프링 애플리케이션 컨텍스트에 추가한다.
     *  (가짜 객체를 사용해서 주입을 한다라고 이해하면 될 듯하다.)
     */
    @MockBean
    PostService postService;

    /** ObjectMapper
     *  특정 객체를 json 형태로 바꾸기위해 사용한다.
     */
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("게시물 작성 기능 테스트")
    void create_post_test() throws Exception {
        //given
        CreatePostRequest request = new CreatePostRequest("테스트 제목", "테스트 내용");
        CreatePostResponse response = new CreatePostResponse(1L,"테스트 제목", "테스트 내용");

        given(postService.createPost(any())).willReturn(response);

        //when & then
        mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L))
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.content").value("테스트 내용"))
                .andDo(print());
        // 그냥 변수만들고 선언하고 함수 호출하면 controller들이 하는 기능들을 테스트 할 수 없다고 함!
    }

    @Test
    @DisplayName("게시물 단일 조회 기능 테스트")
    void read_post_test() throws Exception{
        //given
        Long postId = 1L;
        ReadPostResponse response = new ReadPostResponse(1L, "테스트 제목", "테스트 내용");

        given(postService.readPostById(any())).willReturn(response);

        //when & then
        mockMvc.perform(get("/api/v1/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L))
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.content").value("테스트 내용"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 수정 기능 테스트")
    void update_post_test() throws Exception {
        // given
        Long postId = 1L;
        UpdatePostRequest request = new UpdatePostRequest("수정 제목", "수정 내용");
        UpdatePostResponse response = new UpdatePostResponse(1L, "수정 제목", "수정 내용");

        given(postService.updatePost(any(), any())).willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        // contentType을 json 형태로 바꿔주고,
                        // 요청받은 request를 objectMapper.writeValueAsBytes를 통해 형태를 매핑하여 컨텐츠로 전달한다
                        // content는 요청받은 "수정 제목", "수정 내용"을 의미
                        // 이해가 잘 안된다면 POSTMAN을 확인해보면 된다고  POSTMAN의 BODY부분이 request와 같다.
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L))
                .andExpect(jsonPath("$.title").value("수정 제목"))
                .andExpect(jsonPath("$.content").value("수정 내용"))
                .andDo(print());

    }

    @Test
    @DisplayName("게시물 삭제 기능")
    void delete_post_test() throws Exception {
        //given
        Long postId = 1L;
        DeletePostResponse response = new DeletePostResponse(1L);

        given(postService.deletePost( any(Long.class))).willReturn(response);

        //when & then
        mockMvc.perform(delete("/api/v1/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("모든 게시물을 페이지 단위로 조회하는 기능")
    void read_all_post_test() throws Exception{
        //given
        int page = 0;
        int size = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        ReadPostResponse readPostResponse = new ReadPostResponse(1L, "테스트 제목", "테스트 내용");
        List<ReadPostResponse> responses = new ArrayList<>();
        responses.add(readPostResponse);

        Page<ReadPostResponse> pageResponses = new PageImpl<>(responses, pageRequest, responses.size());

        given(postService.readAllPost(any())).willReturn(pageResponses);

        // when & then
        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].postId").value(readPostResponse.getPostId()))
                .andExpect(jsonPath("$.content[0].title").value(readPostResponse.getTitle()))
                .andExpect(jsonPath("$.content[0].content").value(readPostResponse.getContent()))
                .andDo(print());
    }


}
