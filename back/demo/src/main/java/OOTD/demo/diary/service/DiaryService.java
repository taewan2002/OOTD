package OOTD.demo.diary.service;

import OOTD.demo.auth.service.AuthService;
import OOTD.demo.diary.Diary;
import OOTD.demo.diary.dto.DiaryDto;
import OOTD.demo.diary.dto.PostDiaryReq;
import OOTD.demo.diary.dto.PostDiaryRes;
import OOTD.demo.diary.dto.UpdateDiaryReq;
import OOTD.demo.diary.repository.DiaryRepository;
import OOTD.demo.diary_image.DiaryImage;
import OOTD.demo.diary_image.repository.DiaryImageRepository;
import OOTD.demo.file.FileUploadUtil;
import OOTD.demo.file.dto.FileDto;
import OOTD.demo.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static OOTD.demo.diary_image.DiaryImage.createDiaryImage;

/**
 * Diary 엔티티 관련 서비스 클래스입니다.
 * @version 1.0.0
 * @author CHO Min Ho
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final FileUploadUtil fileUploadUtil;
    private final DiaryImageRepository diaryImageRepository;
    private final AuthService authService;

    /**
     * 게시글을 생성하는 메서드입니다.
     * TODO : 날씨 관련 로직 추가
     * @param dto 게시글 생성 관련 엔티티
     * @param files 게시글 이미지 리스트
     * @return 생성된 게시글 엔티티의 ID를 포함한 DTO
     */
    public PostDiaryRes createPost(PostDiaryReq dto, List<MultipartFile> files) {
        Diary diary = diaryRepository.save(Diary.createPost(dto.getTitle(), dto.getContent(), dto.getScope(),
                authService.getCurrentLoginUser()));

        uploadImages(diary, files);

        return new PostDiaryRes(diary.getId());
    }

    /**
     * 게시글 엔티티 수정 메서드입니다.
     * @param dto 수정할 게시글의 사항 관련 DTO
     * @return 수정된 게시글의 ID를 포함한 DTO
     */
    public PostDiaryRes updatePost(UpdateDiaryReq dto, List<MultipartFile> files) {
        Optional<Diary> diary = diaryRepository.findById(dto.getId());
        User currentUser = authService.getCurrentLoginUser();

        if (diary.isEmpty()) {
            // TODO : 예외 처리 시 반환할 공통 메서드 필요
        }

        if (currentUser.getId() != diary.get().getId()) {
            // TODO : 권한이 없는 사용자가 게시글 수정 시도시 예외 발생 로직 필요
        }

        deleteImages(diary.get());
        uploadImages(diary.get(), files);
        diary.get().updateDiary(dto.getTitle(), dto.getContent(), dto.getScope());

        return new PostDiaryRes(diary.get().getId());

    }

    /**
     * 게시글 삭제 메서드입니다.
     * @param id 삭제할 게시글 ID
     */
    public void deleteDiary(Long id) {
        Optional<Diary> diary = diaryRepository.findById(id);
        User currentUser = authService.getCurrentLoginUser();

        if (diary.isEmpty()) {
            // TODO : 예외 처리 시 반환할 공통 메서드 필요
        }

        if (diary.get().getId() != currentUser.getId()) {
            // TODO : 게시글을 삭제하려는 사용자가 해당 게시글의 작성자가 아닐 경우 퍼미션 오류 관련 처리 필요
        }

        deleteImages(diary.get());

        diaryRepository.delete(diary.get());
    }

    /**
     * 게시글 ID를 이용하여 특정 게시글을 조회하는 메서드입니다.
     * @param id 게시글 ID
     * @return 해당 게시글의 정보를 담고 있는 DTO
     */
    public DiaryDto findDiaryById(Long id) {
        Optional<Diary> diary = diaryRepository.findById(id);

        if (diary.isEmpty()) {
            // TODO : 예외 처리 시 반환할 공통 메서드 필요
        }

        return new DiaryDto(diary.get().getId(), diary.get().getTitle(), diary.get().getContent(),
                diary.get().getCreateDate(), diary.get().getUpdateDate(), diary.get().getUser().getId());
    }

    /**
     * 이미지 리스트를 S3에 업로드하는 메서드입니다.
     */
    private void uploadImages(Diary diary, List<MultipartFile> files) {
        for (int i = 1; i <= files.size(); i++) {
            FileDto fileDto = fileUploadUtil.uploadFile("diary", files.get(i - 1));
            diaryImageRepository.save(createDiaryImage(fileDto.getName(), i, diary, fileDto.getUrl()));
        }
    }

    /**
     * 해당 Diary 의 이미지를 S3로부터 삭제하는 메서드입니다.
     */
    private void deleteImages(Diary diary) {
        for (DiaryImage diaryImage : diaryImageRepository.findByDiary(diary)) {
            fileUploadUtil.deleteFileByName(diaryImage.getImageUrl());
        }
        diaryImageRepository.deleteAll(diaryImageRepository.findByDiary(diary));
    }

}
