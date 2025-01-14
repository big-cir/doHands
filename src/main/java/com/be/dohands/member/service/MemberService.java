package com.be.dohands.member.service;

import static com.be.dohands.member.dto.QuestResult.processQuestType;

import com.be.dohands.article.repository.ArticleRepository;
import com.be.dohands.article.repository.MemberArticleRepository;
import com.be.dohands.common.security.CustomUserDetails;
import com.be.dohands.evaluation.repository.EvaluationExpQueryRepository;
import com.be.dohands.evaluation.service.EvaluationExpService;
import com.be.dohands.level.LevelExp;
import com.be.dohands.level.service.LevelExpService;
import com.be.dohands.member.dto.MemberInfoResponseDTO;
import com.be.dohands.member.dto.MemberResponse;
import com.be.dohands.member.dto.QuestsInProgressRequestDTO;
import com.be.dohands.member.dto.QuestsInProgressResponseDTO;
import com.be.dohands.member.dto.QuestsInProgressResponseDTO.QuestInProgress;
import com.be.dohands.member.dto.UpdateCharacterDto;
import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.dto.QuestRecentDto;
import com.be.dohands.quest.entity.JobQuestExpEntity;
import com.be.dohands.quest.entity.QuestScheduleEntity;
import com.be.dohands.quest.entity.UserQuestEntity;
import com.be.dohands.quest.repository.LeaderQuestRepository;
import com.be.dohands.quest.repository.QuestScheduleRepository;
import com.be.dohands.quest.repository.UserQuestRepository;
import com.be.dohands.quest.service.JobQuestService;
import com.be.dohands.quest.repository.LeaderQuestExpQueryRepository;
import com.be.dohands.member.Member;
import com.be.dohands.member.MemberExp;
import com.be.dohands.member.dto.UpdatePasswordDto;
import com.be.dohands.member.dto.CursorResult;
import com.be.dohands.member.dto.MemberExpStatusDto;
import com.be.dohands.member.dto.MultiCursor;
import com.be.dohands.member.dto.MultiCursorResult;
import com.be.dohands.member.dto.QuestExpConditionDto;
import com.be.dohands.member.dto.QuestExpDto;
import com.be.dohands.member.dto.QuestResult;
import com.be.dohands.member.repository.MemberExpRepository;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.quest.service.LeaderQuestExpService;
import com.be.dohands.tf.repository.TfExpQueryRepository;
import com.be.dohands.tf.service.TfExpService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberExpRepository memberExpRepository;
    private final EvaluationExpQueryRepository evaluationExpQueryRepository;
    private final LeaderQuestExpQueryRepository leaderQuestExpQueryRepository;
    private final TfExpQueryRepository tfExpQueryRepository;
    private final UserQuestRepository userQuestRepository;
    private final QuestScheduleRepository questScheduleRepository;
    private final LeaderQuestRepository leaderQuestRepository;
    private final MemberArticleRepository memberArticleRepository;
    private final ArticleRepository articleRepository;

    private final JobQuestService jobQuestService;
    private final LevelExpService levelExpService;
    private final EvaluationExpService evaluationExpService;
    private final TfExpService tfExpService;
    private final LeaderQuestExpService leaderQuestExpService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String[] questType = {"evaluation", "leader", "tf", "job"};

    @Transactional(readOnly = true)
    public MultiCursorResult<QuestExpDto> findQuestExpsById(String loginId, QuestExpConditionDto questExpConditionDto) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        MultiCursor multiCursor = MultiCursor.deserialize(questExpConditionDto.encodeCursor());

        String employeeNumber = member.getEmployeeNumber();
        String department = member.getDepartment();

        return findQuestExpWithoutJobQuestByEmployeeNumber(employeeNumber, department, multiCursor,
                questExpConditionDto.size());
    }

    @Transactional(readOnly = true)
    public MemberExpStatusDto findMemberExpById(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        MemberExp memberExp = memberExpRepository.findByUserId(member.getUserId()).orElseThrow();
        LevelExp level = levelExpService.findNextExpByCategory(member.getJobCategory(),
                memberExp.getCurrentExp());
        return new MemberExpStatusDto(memberExp.getCurrentExp(), memberExp.getCumulativeExp(),
                level == null ? 0 : level.getExp());
    }

    @Transactional
    public MemberResponse modifyPassword(UpdatePasswordDto updatePasswordDto, String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        member.updatePassword(updatePasswordDto.changePassword());
        memberRepository.save(member);
        LevelExp level = findLevelExpById(member.getLevelId());
        return new MemberResponse(member, getLevelName(level));
    }

    @Transactional
    public MemberResponse modifyCharacter(UpdateCharacterDto updateCharacterDto, String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        member.updateCharacter(updateCharacterDto.characterType(), updateCharacterDto.skinId());
        memberRepository.save(member);
        LevelExp level = findLevelExpById(member.getLevelId());
        return new MemberResponse(member, getLevelName(level));
    }

    @Transactional(readOnly = true)
    public MemberInfoResponseDTO findMember(String loginId) {

        Member user = memberRepository.findByLoginId(loginId).get();
        Integer unreadArticles = articleRepository.findAll().size() - memberArticleRepository.countUnreadArticles(user.getUserId());

        return MemberInfoResponseDTO.builder()
            .employeeNumber(user.getEmployeeNumber())
            .name(user.getName())
            .department(user.getDepartment())
            .characterType(user.getCharacterType())
            .levelName(user.getName())
            .hireDate(user.getHireDate())
            .jobCategory(user.getJobCategory())
            .skinId(user.getSkinId())
            .unreadArticles(unreadArticles)
            .build();
    }

    @Transactional(readOnly = true)
    public QuestRecentDto findRecentQuest(String loginId) {
        String userId = String.valueOf(memberRepository.findByLoginId(loginId).get().getUserId());
        boolean keyExists = redisTemplate.hasKey(String.valueOf(userId));
        if (keyExists) {
            String[] parts = redisTemplate.opsForValue().get(userId).split("\\|");
            return new QuestRecentDto(parts[0],
                    LocalDateTime.parse(parts[1]),
                    Integer.parseInt(parts[2]));
        }
        return new QuestRecentDto("nothing", null, 0);
    }

    public void findRecentCompleteQuestWithOutJob(String type, String employeeNumber) {
        // questType = {"evaluation", "leader", "tf", "job"};
        QuestRecentDto recent = null;
        Long userId = null;
        if (type.equals(questType[0])) {
            recent = evaluationExpService.findAllMostRecent(employeeNumber);
            userId = memberRepository.findByEmployeeNumber(employeeNumber).get().getUserId();
        } else if (type.equals(questType[1])) {
            recent = leaderQuestExpService.findAllMostRecent(employeeNumber);
            userId = memberRepository.findByEmployeeNumber(employeeNumber).get().getUserId();
        } else if (type.equals(questType[2])) {
            recent = tfExpService.findAllMostRecent(employeeNumber);
            userId = memberRepository.findByEmployeeNumber(employeeNumber).get().getUserId();
        }

        String key = String.valueOf(userId);
        redisTemplate.opsForValue().set(key, getValue(recent));
    }

    public void findRecentCompleteJobQuest(String department) {
        QuestRecentDto recent = jobQuestService.findAllMostRecent(department);
        if (recent == null) return;

        memberRepository.findMembersByDepartment(department)
                        .forEach(m -> {
                            redisTemplate.opsForValue().set(String.valueOf(m.getUserId()), getValue(recent));
                        });
    }

    @Transactional(readOnly = true)
    public QuestsInProgressResponseDTO getQuestsInProgress(CustomUserDetails user, QuestsInProgressRequestDTO request) {
        List<QuestInProgress> result = new ArrayList<>();

        Long userId = memberRepository.findByLoginId(user.getUsername()).get().getUserId();

        List<QuestScheduleEntity> questMonthSchedules = questScheduleRepository.findByDepartmentAndMonth(request.getDepartment(),
            request.getYear(), request.getMonth());

        findQuestsInProgress(result, questMonthSchedules, userId);

        List<QuestScheduleEntity> questWeekSchedules = questScheduleRepository.findByDepartmentAndWeek(request.getDepartment(),
            request.getYear(), request.getWeek());

        findQuestsInProgress(result, questWeekSchedules, userId);

        return QuestsInProgressResponseDTO.builder()
            .questsInProgressList(result)
            .build();
    }

    private String getValue(QuestRecentDto recent) {
        return recent.questType() + "|" + recent.createdAt() + "|" + recent.exp();
    }

    private String getLevelName(LevelExp level) {
        return level != null ? level.getName() : null;
    }

    private LevelExp findLevelExpById(Long levelId) {
        return levelExpService.findLevelExp(levelId);
    }

    private void findQuestsInProgress(List<QuestInProgress> questsInProgressList,
        List<QuestScheduleEntity> questWeekSchedules, Long userId) {
        for (QuestScheduleEntity q : questWeekSchedules) {
            UserQuestEntity userQuest = userQuestRepository.findByQuestScheduleIdAndUserId(q.getQuestScheduleId(), userId);
            String questName = null;
            if (userQuest.getQuestType() == QuestType.LEADER) questName = leaderQuestRepository.findByLeaderQuestId(userQuest.getQuestId()).get().getQuestName();
            QuestInProgress questInProgress = new QuestInProgress(questName, userQuest.getQuestType(), userQuest.getStatusType());
            questsInProgressList.add(questInProgress);
        }
    }

    private MultiCursorResult<QuestExpDto> findQuestExpWithoutJobQuestByEmployeeNumber(String employeeNumber, String department, MultiCursor multiCursor, int size) {
        List<QuestExpDto> questExpDtos = new ArrayList<>();
        Map<String, String> typeCount = new HashMap<>();

        QuestResult evaluationResult = processQuestType(
                () -> evaluationExpQueryRepository.findEvaluationExpsByEmployeeNumber(employeeNumber, multiCursor.getEvaluationExpCursor(), size),
                quest -> new QuestExpDto(quest.getEvaluationExpId(), quest.getGrade(), quest.getExp(), questType[0], quest.getCreatedAt()));
        questExpDtos.addAll(evaluationResult.items());

        QuestResult leaderQuestResult = processQuestType(
                () -> leaderQuestExpQueryRepository.findLeaderQuestExpsByEmployeeNumber(employeeNumber, multiCursor.getLeaderQuestExpCursor(), size),
                quest -> new QuestExpDto(quest.getLeaderQuestExpId(), quest.getQuestName(), quest.getExp(), questType[1], quest.getCreatedAt()));
        questExpDtos.addAll(leaderQuestResult.items());

        QuestResult tfResult = processQuestType(
                () -> tfExpQueryRepository.findTfExpsByEmployeeNumber(employeeNumber, multiCursor.getTfExpCursor(), size),
                quest -> new QuestExpDto(quest.getTfExpId(), quest.getProjectName(), quest.getExp(), questType[2], quest.getCreatedAt()));
        questExpDtos.addAll(tfResult.items());

        QuestResult jobQuestResult = processQuestType(
                () -> findJobQuestExpByDepartment(department, multiCursor.getJobQuestExpCursor(), size),
                quest -> new QuestExpDto(quest.getJobQuestExpId(), quest.getProductivity().toString(), quest.getExp(), questType[3], quest.getCreatedAt()));
        questExpDtos.addAll(jobQuestResult.items());

        questExpDtos.sort((a, b) -> {
            int timeComparison = b.createdAt().compareTo(a.createdAt());
            return timeComparison != 0 ? timeComparison : b.itemId().compareTo(a.itemId());
        });

        List<QuestExpDto> resultItems = questExpDtos.subList(0, Math.min(questExpDtos.size(), size));

        for (QuestExpDto item : resultItems) {
            typeCount.put(item.itemType(), item.createdAt().toString());
        }

        updateCursor(questType[0], multiCursor, evaluationResult.cursorResult(), typeCount);
        updateCursor(questType[1], multiCursor, leaderQuestResult.cursorResult(), typeCount);
        updateCursor(questType[2], multiCursor, tfResult.cursorResult(), typeCount);
        updateCursor(questType[3], multiCursor, jobQuestResult.cursorResult(), typeCount);

        boolean hasNext = questExpDtos.size() > size;

        return new MultiCursorResult<>(resultItems, multiCursor.serialize(), hasNext);
    }

    private void updateCursor(String questType, MultiCursor multiCursor, CursorResult<?> cursorResult, Map<String, String> typeCount) {
        String cursor = cursorResult.getCursor();
        if (cursor != null) {
            String newCursor = cursorResult.isLast() ? "-1"
                    : typeCount.getOrDefault(questType, cursor);
            multiCursor.updateCursor(questType, newCursor);
        }
    }

    private CursorResult<JobQuestExpEntity> findJobQuestExpByDepartment(String department, String cursor, int size) {
        return jobQuestService.findJobQuestExpByDepartment(department, cursor, size);
    }
}
