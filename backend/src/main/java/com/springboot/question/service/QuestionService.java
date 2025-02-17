package com.springboot.question.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import com.springboot.utils.CheckValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final StorageService storageService;
    private final MemberService memberService;
    private final CheckValidator checkValidator;

    public QuestionService(QuestionRepository questionRepository,
                           StorageService storageService,
                           MemberService memberService,
                           CheckValidator checkValidator) {
        this.questionRepository = questionRepository;
        this.storageService = storageService;
        this.memberService = memberService;
        this.checkValidator = checkValidator;
    }

    // 질문 생성 서비스 로직 구현
    public Question createQuestion(Question question, MultipartFile questionImage) {
        // 파일을 가져왔을때 그 파일이 null이거나 빈 파일 일때 검증해야함
        if (questionImage != null && !questionImage.isEmpty()) {
            question.setQuestionImage(questionImage.getOriginalFilename());
            String fileName = question.getMember().getMemberId() + "_" + System.currentTimeMillis();
            storageService.store(questionImage, fileName);
            question.setQuestionImage(fileName);
        } else {
            question.setQuestionImage("C:\\solo_project\\backend\\src\\main\\resources\\questionImage\\noImage.png");
        }
        memberService.findVerifiedMember(question.getMember().getMemberId());
        return questionRepository.save(question);
    }

    // 질문 수정 서비스 로직 구현
    public Question updateQuestion(Question question, long principalId) {
        Question findquestion = findVerifiedQuestion(question.getQuestionId());

        // 일단 질문을 수정하려면 내가 쓴 글만 수정이되어야함 즉 작성자가 맞는지를 따져야 하는거 아닌가?
        checkValidator.checkOwner(findquestion.getMember().getMemberId(), principalId);

        // 답변이 이미 달려서 질문 답변 완료 상태인데 이걸 수정할 수는 없음
        if(findquestion.getQuestionStatus() == Question.QuestionStatus.QUESTION_ANSWERED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_ALREADY_ANSWERED);
        }

        Optional.ofNullable(question.getTitle())
                .ifPresent(title -> findquestion.setTitle(title));
        Optional.ofNullable(question.getContent())
                .ifPresent(content -> findquestion.setContent(content));
        Optional.ofNullable(question.getVisibility())
                .ifPresent(visibility -> findquestion.setVisibility(visibility));

        return questionRepository.save(findquestion);
    }

    // 특정 질문 조회 서비스 로직 구현
    public Question findQuestion(long questionId, long principalId) {
        Question question = findVerifiedQuestion(questionId);

        // 질문의 작성자 ID가 뭔지 알기 위해 가져옴
        long ownerId = question.getMember().getMemberId();

        // 비밀글인 경우에는 작성자와 관리자만 접근 가능하도록 검증
        if (question.getVisibility() == Question.Visibility.QUESTION_SECRET) {
            checkValidator.checkAdminOrOwner(ownerId, principalId);
        }

        // 이미 삭제된 질문은 조회할 수 없음
        verifyQuestionDeleteStatus(question);

        // 조회수 증가
        question.setViewCount(question.getViewCount() + 1);

        return questionRepository.save(question);
    }

    // 전체 질문 조회 서비스 로직 구현
    public Page<Question> findQuestions(int page, int size) {
        if (page < 1) {
            throw new IllegalArgumentException("페이지 번호 1이상이여야 하는데용");
        }

        return questionRepository.findByQuestionStatusNotIn(Arrays.asList(
                Question.QuestionStatus.QUESTION_DELETED,
                Question.QuestionStatus.QUESTION_DEACTIVED
        ), PageRequest.of(page,size, Sort.by("questionId").descending()));
        // 비밀글인 상태 SECRET 이여도 가져오긴해야됨 보이긴해야지, 비밀글입니다 로 보여야지
    }

    // 질문 삭제 서비스 로직 구현
    public void deleteQuestion(long questionId, long principalId) {
        Question question = findVerifiedQuestion(questionId);

        // 질문을 한 사람 즉, 작성자 ID 가져옴
        long ownerId = question.getMember().getMemberId();

        // 작성자 본인만 삭제할 수 있음
        if (ownerId != principalId) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_OWNER);
        }

        // 질문이 삭제상태인지 검증해야함
        verifyQuestionDeleteStatus(question);

        // 삭제했으면 상태 변경
        question.setQuestionStatus(Question.QuestionStatus.QUESTION_DELETED);

        questionRepository.save(question);
    }

    // 질문이 DB에 존재하는지 검증 후 가져온 질문을 반환하는 메서드
    public Question findVerifiedQuestion(long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        return question.orElseThrow(() -> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
    }

    // 질문이 삭제 상태인지를 검증하는 메서드 필요
    public void verifyQuestionDeleteStatus(Question question) {
        if(question.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND);
        }
    }

    // 답변 삭제 되면 질문의 답변 null로 돌리기
    public void setAnswerOfQuestion(long questionId) {
        Question question = findVerifiedQuestion(questionId);
        question.setAnswer(null);
    }
}
