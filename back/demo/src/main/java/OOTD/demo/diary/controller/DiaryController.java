package OOTD.demo.diary.controller;

import OOTD.demo.diary.dto.PostDiaryReqDTO;
import OOTD.demo.diary.dto.PostDiaryResDTO;
import OOTD.demo.diary.dto.UpdateDiaryReqDTO;
import OOTD.demo.diary.service.DiaryService;
import OOTD.demo.common.HttpResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;


/**
 * Diary 엔티티 관련 컨트롤러입니다.
 * @version 1.0.1
 * @author CHO Min Ho
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;
    private final HttpResponseUtil httpResponseUtil;

    /**
     * 게시글 생성 컨트롤러 메서드입니다.
     * @param dto 게시글 생성 DTO
     * @param files 게시글 이미지
     * @return 생성된 게시글의 ID
     */
    @Operation(summary = "게시글 생성 API", description = "게시글 생성 API입니다. (TODO : 현재 User가 NULL로 들어갑니다.)",
            tags = { "Diary Controller" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = PostDiaryResDTO.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST - validation 오류 등",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR - 내부 서버 오류",
                    content = @Content)
    })
    @PostMapping("/api/diary/create")
    public ResponseEntity<?> createDiary(
            @Parameter(name = "dto", description = "게시글 생성 관련 DTO") @RequestPart @Valid PostDiaryReqDTO dto,
            @Parameter(name = "files", description = "게시글 사진들") @RequestPart List<MultipartFile> files) {

        // TODO : 현재 로그인된 사용자를 가져오는 로직 필요

        return httpResponseUtil.createOKHttpResponse(diaryService.createPost(dto, files, null),
                "게시글 생성에 성공했습니다.");
    }

    /**
     * 단일 게시글을 조회하는 컨트롤러 메서드입니다.
     * @param id 게시글 ID
     * @return 해당 게시글의 정보를 담고 있는 DTO
     */
    @Operation(summary = "게시글 조회 API", description = "게시글 조회 API입니다.",
            tags = { "Diary Controller" })
    @GetMapping("/api/diary/{id}")
    public ResponseEntity<?> findDiary(@PathVariable(name = "id") Long id) {

        // TODO : 공개 여부에 따라 퍼미션 거부 로직 필요

        return httpResponseUtil.createOKHttpResponse(diaryService.findDiaryById(id), "게시글 조회에 성공했습니다.");
    }

    /**
     * 게시글 업데이트 관련 컨트롤러 메서드입니다.
     * @param dto 업데이트할 게시글 정보를 담고 있는 DTO
     * @return 업데이트한 게시글 엔티티의 ID
     */
    @Operation(summary = "게시글 수정 API", description = "게시글 수정 API입니다. (TODO : 현재 User가 NULL로 들어갑니다.)",
            tags = { "Diary Controller" })
    @PostMapping("/api/diary/update")
    public ResponseEntity<?> updateDiary(@RequestBody UpdateDiaryReqDTO dto) {

        // TODO : 현재 로그인된 사용자를 가져오는 로직 필요

        return httpResponseUtil.createOKHttpResponse(diaryService.updatePost(dto, null), "게시글 수정에 성공했습니다.");
    }

    /**
     * 게시글 삭제 관련 컨트롤러 메서드입니다.
     * @param id 삭제할 게시글 id
     * @return 성공 여부
     */
    @Operation(summary = "게시글 삭제 API", description = "게시글 삭제 API입니다.",
            tags = { "Diary Controller" })
    @GetMapping("/api/diary/delete/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable(name = "id") Long id) {

        // TODO : 현재 로그인된 사용자를 가져오는 로직 필요

        diaryService.deleteDiary(id, null);

        // TODO : 반환할 값이 없는 경우 반환할 양식 규정 필요
        return httpResponseUtil.createOKHttpResponse(null, "게시글 삭제에 성공했습니다.");
    }


}
