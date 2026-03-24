package com.shanzhu.beadhouse.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanzhu.beadhouse.common.constant.CheckEnum;
import com.shanzhu.beadhouse.common.constant.ExceptionEnum;
import com.shanzhu.beadhouse.common.constant.YesNoEnum;
import com.shanzhu.beadhouse.common.util.AssertUtil;
import com.shanzhu.beadhouse.common.util.DateUtilWen;
import com.shanzhu.beadhouse.common.util.PageUtil;
import com.shanzhu.beadhouse.entity.base.DropDown;
import com.shanzhu.beadhouse.entity.base.PageResult;
import com.shanzhu.beadhouse.entity.base.Result;
import com.shanzhu.beadhouse.dao.mapper.ActiveMapper;
import com.shanzhu.beadhouse.dao.mapper.ActiveParticipantMapper;
import com.shanzhu.beadhouse.dao.mapper.ElderLabelMapper;
import com.shanzhu.beadhouse.entity.po.Active;
import com.shanzhu.beadhouse.entity.po.ActiveParticipant;
import com.shanzhu.beadhouse.entity.po.ActiveType;
import com.shanzhu.beadhouse.entity.po.Elder;
import com.shanzhu.beadhouse.entity.po.ElderLabel;
import com.shanzhu.beadhouse.entity.po.Label;
import com.shanzhu.beadhouse.entity.po.LabelType;
import com.shanzhu.beadhouse.entity.query.OperateActiveQuery;
import com.shanzhu.beadhouse.entity.query.PageActiveByKeyQuery;
import com.shanzhu.beadhouse.entity.query.PageSearchElderByKeyQuery;
import com.shanzhu.beadhouse.entity.vo.GetActiveByIdVo;
import com.shanzhu.beadhouse.entity.vo.PageActiveByKeyVo;
import com.shanzhu.beadhouse.entity.vo.RecommendActiveVo;
import com.shanzhu.beadhouse.service.ActiveService;
import com.shanzhu.beadhouse.service.common.ActiveFunc;
import com.shanzhu.beadhouse.service.common.ActiveParticipantFunc;
import com.shanzhu.beadhouse.service.common.ActiveTypeFunc;
import com.shanzhu.beadhouse.service.common.CommonFunc;
import com.shanzhu.beadhouse.service.common.ElderFunc;
import com.shanzhu.beadhouse.service.common.LabelFunc;
import com.shanzhu.beadhouse.service.common.LabelTypeFunc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActiveServiceImpl implements ActiveService {
    @Resource
    private CommonFunc commonFunc;
    @Resource
    private ActiveTypeFunc activeTypeFunc;
    @Resource
    private ActiveMapper activeMapper;
    @Resource
    private ActiveParticipantMapper activeParticipantMapper;
    @Resource
    private ActiveParticipantFunc activeParticipantFunc;
    @Resource
    private ActiveFunc activeFunc;
    @Resource
    private PageUtil pageUtil;
    @Resource
    private ElderFunc elderFunc;
    @Resource
    private ElderLabelMapper elderLabelMapper;
    @Resource
    private LabelFunc labelFunc;
    @Resource
    private LabelTypeFunc labelTypeFunc;

    @Override
    public Result getActiveType() {
        return Result.success(BeanUtil.copyToList(activeTypeFunc.listNotDelActiveType(null), DropDown.class));
    }

    @Override
    public Result pageActiveByKey(PageActiveByKeyQuery query) {
        // 获取开始/结束时间
        Date startTime = DateUtilWen.getDayStartTime(DateUtilWen.dateStrToDate(query.getStartTime()));
        Date endTime = DateUtilWen.getDayEndTime(DateUtilWen.dateStrToDate(query.getEndTime()));
        // 根据关键词查询活动
        List<PageActiveByKeyVo> pageActiveByKeyVoList = activeMapper.listActiveByKey(query, startTime, endTime);
        // 封装返回数据
        PageResult<PageActiveByKeyVo> pageResult = pageUtil.packPageResultData(pageActiveByKeyVoList, query.getPageNum(), query.getPageSize());
        return Result.success(pageResult);
    }

    @Override
    public Result pageSearchElderByKey(PageSearchElderByKeyQuery query) {
        List<String> checkFlagList = new ArrayList<>(Arrays.asList(CheckEnum.CONSULT.getStatus(), CheckEnum.INTENTION.getStatus(), CheckEnum.RESERVE.getStatus(), CheckEnum.ENTER.getStatus(), CheckEnum.EXIT_AUDIT.getStatus(), CheckEnum.EXIT.getStatus()));
        // 根据姓名和联系电话获取咨询中、意向跟进、预定、入住、退住审核、已退住老人列表
        return commonFunc.pageSearchElderByKeyResult(query, checkFlagList);
    }

    @Override
    @Transactional
    public Result addActive(OperateActiveQuery query) {
        // 验证活动名称是否相同
        AssertUtil.isNull(activeFunc.getActiveByName(query.getName()), ExceptionEnum.ACTIVE_REPEAT);
        // 初始化活动
        query.setId(null);
        Active active = BeanUtil.toBean(query, Active.class);
        active.setDelFlag(YesNoEnum.NO.getCode());
        // 新增
        activeMapper.insert(active);
        // 批量插入活动参与者
        activeParticipantFunc.saveBatchActiveParticipant(query.getElderIdList(), active.getId(), false);
        return Result.success();
    }

    @Override
    public Result getActiveById(Long activeId) {
        // 根据编号获取活动
        Active active = activeMapper.selectById(activeId);
        // 判断是否为空
        AssertUtil.notNull(active, ExceptionEnum.DATA_NOT_EXIST);
        // 转换实体
        GetActiveByIdVo getActiveByIdVo = BeanUtil.toBean(active, GetActiveByIdVo.class);
        // 根据活动编号获取活动参与老人列表并设值
        getActiveByIdVo.setParticipateElderVoList(activeParticipantMapper.listParticipateElder(activeId));
        return Result.success(getActiveByIdVo);
    }

    @Override
    @Transactional
    public Result editActive(OperateActiveQuery query) {
        // 验证活动名称是否相同
        Active activeByName = activeFunc.getActiveByName(query.getName());
        boolean checkName = activeByName != null && !Objects.equals(activeByName.getId(), query.getId());
        AssertUtil.notTrue(checkName, ExceptionEnum.ACTIVE_REPEAT);
        // 封装修改
        Active active = BeanUtil.toBean(query, Active.class);
        // 修改
        activeMapper.updateById(active);
        // 批量插入活动参与者
        activeParticipantFunc.saveBatchActiveParticipant(query.getElderIdList(), active.getId(), true);
        return Result.success();
    }

    @Override
    public Result deleteActive(Long activeId) {
        // 封装修改
        Active active = new Active();
        active.setId(activeId);
        active.setDelFlag(YesNoEnum.YES.getCode());
        // 修改
        activeMapper.updateById(active);
        return Result.success();
    }

    @Override
    public Result recommendActiveByElder(Long elderId, Integer topN) {
        // 根据编号获取老人信息
        Elder elder = elderFunc.getById(elderId);
        // 判断是否为空
        AssertUtil.notNull(elder, ExceptionEnum.DATA_NOT_EXIST);
        // 默认推荐条数
        int limit = (topN == null || topN <= 0) ? 5 : Math.min(topN, 20);
        // 查询今天及之后的活动
        Date now = new Date();
        List<Active> activeList = activeMapper.selectList(new LambdaQueryWrapper<Active>()
                .eq(Active::getDelFlag, YesNoEnum.NO.getCode())
                .isNotNull(Active::getActiveDate)
                .ge(Active::getActiveDate, DateUtilWen.getDayStartTime(now))
                .orderByAsc(Active::getActiveDate)
                .orderByDesc(Active::getCreateTime));
        // 若没有活动，直接返回空列表
        if (CollectionUtils.isEmpty(activeList)) {
            return Result.success(Collections.emptyList());
        }

        // 获取老人历史参与记录
        Set<Long> historyActiveIdSet = activeParticipantFunc.list(new LambdaQueryWrapper<ActiveParticipant>()
                        .eq(ActiveParticipant::getElderId, elderId))
                .stream()
                .map(ActiveParticipant::getActiveId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        // 过滤已经参加过的活动
        List<Active> candidateActiveList = activeList.stream()
                .filter(active -> !historyActiveIdSet.contains(active.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(candidateActiveList)) {
            return Result.success(Collections.emptyList());
        }

        // 历史偏好统计（按活动类型）
        Map<Long, Long> historyTypeCountMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(historyActiveIdSet)) {
            List<Active> historyActiveList = activeMapper.selectBatchIds(historyActiveIdSet);
            for (Active historyActive : historyActiveList) {
                if (historyActive == null || historyActive.getTypeId() == null) {
                    continue;
                }
                historyTypeCountMap.put(historyActive.getTypeId(), historyTypeCountMap.getOrDefault(historyActive.getTypeId(), 0L) + 1);
            }
        }
        long historyTotal = historyTypeCountMap.values().stream().mapToLong(Long::longValue).sum();

        // 活动分类映射
        Map<Long, String> activeTypeMap = activeTypeFunc.listNotDelActiveType(null).stream()
                .collect(Collectors.toMap(ActiveType::getId, ActiveType::getName, (source, target) -> source));

        // 获取老人标签，并筛选兴趣标签
        Set<String> allLabelSet = new LinkedHashSet<>();
        Set<String> interestLabelSet = new LinkedHashSet<>();
        loadElderLabelSet(elderId, allLabelSet, interestLabelSet);
        Set<String> preferLabelSet = CollectionUtils.isEmpty(interestLabelSet) ? allLabelSet : interestLabelSet;

        // 获取候选活动的参与者，计算“社交协同”
        Map<Long, Set<Long>> activeParticipantMap = buildActiveParticipantMap(candidateActiveList);
        Map<Long, Elder> participantElderMap = buildParticipantElderMap(activeParticipantMap);

        // 评分并封装推荐结果
        List<RecommendActiveVo> recommendActiveVoList = new ArrayList<>();
        for (Active active : candidateActiveList) {
            List<String> reasonList = new ArrayList<>();
            String activeText = buildActiveText(active);
            int score = 20;

            // 1) 兴趣标签匹配
            List<String> matchLabelList = matchLabelList(preferLabelSet, activeText);
            if (!CollectionUtils.isEmpty(matchLabelList)) {
                int interestScore = Math.min(45, matchLabelList.size() * 18);
                score += interestScore;
                reasonList.add("兴趣匹配：" + String.join("、", matchLabelList));
            } else if (!CollectionUtils.isEmpty(preferLabelSet)) {
                reasonList.add("兴趣标签未直接命中，优先采用历史行为和时间窗口推荐");
            } else {
                reasonList.add("暂无兴趣标签，采用行为偏好和时间窗口推荐");
            }

            // 2) 历史行为偏好（活动类型）
            if (active.getTypeId() != null && historyTotal > 0) {
                long typeHistoryCount = historyTypeCountMap.getOrDefault(active.getTypeId(), 0L);
                if (typeHistoryCount > 0) {
                    int typeScore = Math.min(25, Math.max(6, (int) Math.round((double) typeHistoryCount * 25 / historyTotal)));
                    score += typeScore;
                    reasonList.add("历史偏好：同类活动参与 " + typeHistoryCount + " 次");
                } else {
                    score += 4;
                    reasonList.add("多样性补充：推荐未参与过的活动类型");
                }
            }

            // 3) 时间窗口（越近越优先）
            long dayDiff = Math.max(0L, (active.getActiveDate().getTime() - now.getTime()) / (24 * 60 * 60 * 1000));
            if (dayDiff <= 7) {
                score += 15;
                reasonList.add("时间窗口：活动在 7 天内，参与转化更高");
            } else if (dayDiff <= 30) {
                score += 8;
            } else {
                score += 4;
            }

            // 4) 年龄适配
            score += calcAgeSuitabilityScore(elder, activeText, reasonList);

            // 5) 社交协同（同护理等级已报名人数）
            score += calcSocialScore(elder, activeParticipantMap.get(active.getId()), participantElderMap, reasonList);

            // 封装结果
            RecommendActiveVo recommendActiveVo = new RecommendActiveVo();
            recommendActiveVo.setId(active.getId());
            recommendActiveVo.setTypeId(active.getTypeId());
            recommendActiveVo.setTypeName(activeTypeMap.get(active.getTypeId()));
            recommendActiveVo.setTheme(active.getTheme());
            recommendActiveVo.setName(active.getName());
            recommendActiveVo.setContent(active.getContent());
            recommendActiveVo.setAddress(active.getAddress());
            recommendActiveVo.setOrganizer(active.getOrganizer());
            recommendActiveVo.setPhone(active.getPhone());
            recommendActiveVo.setActiveDate(DateUtilWen.dateToDateStr(active.getActiveDate(), "yyyy-MM-dd"));
            recommendActiveVo.setScore(Math.min(score, 100));
            recommendActiveVo.setRecommendLevel(getRecommendLevel(recommendActiveVo.getScore()));
            recommendActiveVo.setReasonList(reasonList);
            recommendActiveVo.setMatchLabelList(matchLabelList);
            recommendActiveVoList.add(recommendActiveVo);
        }

        // 根据分值降序、活动时间升序排序
        recommendActiveVoList.sort(
                Comparator.comparing(RecommendActiveVo::getScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(RecommendActiveVo::getActiveDate, Comparator.nullsLast(String::compareTo))
        );
        // 截取推荐数量
        if (recommendActiveVoList.size() > limit) {
            recommendActiveVoList = new ArrayList<>(recommendActiveVoList.subList(0, limit));
        }
        return Result.success(recommendActiveVoList);
    }

    /**
     * 获取老人所有标签和兴趣标签
     */
    private void loadElderLabelSet(Long elderId, Set<String> allLabelSet, Set<String> interestLabelSet) {
        List<ElderLabel> elderLabelList = elderLabelMapper.selectList(new LambdaQueryWrapper<ElderLabel>()
                .eq(ElderLabel::getElderId, elderId));
        if (CollectionUtils.isEmpty(elderLabelList)) {
            return;
        }
        Map<Long, Label> labelMap = labelFunc.listNotDelLabel().stream()
                .collect(Collectors.toMap(Label::getId, label -> label, (source, target) -> source));
        Map<Long, String> labelTypeMap = labelTypeFunc.listNotDelLabelType().stream()
                .collect(Collectors.toMap(LabelType::getId, LabelType::getName, (source, target) -> source));
        for (ElderLabel elderLabel : elderLabelList) {
            Label label = labelMap.get(elderLabel.getLabelId());
            if (label == null || label.getName() == null || "".equals(label.getName().trim())) {
                continue;
            }
            allLabelSet.add(label.getName());
            String typeName = labelTypeMap.get(label.getTypeId());
            if (isInterestType(typeName)) {
                interestLabelSet.add(label.getName());
            }
        }
    }

    /**
     * 判断是否是兴趣标签分类
     */
    private boolean isInterestType(String typeName) {
        if (typeName == null || "".equals(typeName.trim())) {
            return false;
        }
        return typeName.contains("兴趣") || typeName.contains("爱好");
    }

    /**
     * 活动标签命中匹配
     */
    private List<String> matchLabelList(Set<String> labelSet, String activeText) {
        if (CollectionUtils.isEmpty(labelSet)) {
            return Collections.emptyList();
        }
        String text = activeText == null ? "" : activeText.toLowerCase();
        List<String> matchLabelList = new ArrayList<>();
        for (String labelName : labelSet) {
            if (labelName == null || "".equals(labelName.trim())) {
                continue;
            }
            if (text.contains(labelName.toLowerCase())) {
                matchLabelList.add(labelName);
            }
        }
        return matchLabelList;
    }

    /**
     * 构建活动与参与者映射
     */
    private Map<Long, Set<Long>> buildActiveParticipantMap(List<Active> candidateActiveList) {
        Map<Long, Set<Long>> activeParticipantMap = new HashMap<>();
        List<Long> activeIdList = candidateActiveList.stream()
                .map(Active::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(activeIdList)) {
            return activeParticipantMap;
        }
        List<ActiveParticipant> participantList = activeParticipantFunc.list(new LambdaQueryWrapper<ActiveParticipant>()
                .in(ActiveParticipant::getActiveId, activeIdList));
        for (ActiveParticipant participant : participantList) {
            activeParticipantMap.computeIfAbsent(participant.getActiveId(), key -> new HashSet<>()).add(participant.getElderId());
        }
        return activeParticipantMap;
    }

    /**
     * 构建活动参与者详情映射
     */
    private Map<Long, Elder> buildParticipantElderMap(Map<Long, Set<Long>> activeParticipantMap) {
        Set<Long> participantElderIdSet = activeParticipantMap.values().stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(participantElderIdSet)) {
            return new HashMap<>();
        }
        List<Elder> participantElderList = elderFunc.listByIds(participantElderIdSet);
        return participantElderList.stream()
                .collect(Collectors.toMap(Elder::getId, elder -> elder, (source, target) -> source));
    }

    /**
     * 计算年龄适配得分
     */
    private int calcAgeSuitabilityScore(Elder elder, String activeText, List<String> reasonList) {
        if (elder.getAge() == null || activeText == null) {
            return 0;
        }
        int age = elder.getAge();
        boolean gentleKeyword = containsKeyword(activeText, Arrays.asList("养生", "康复", "手工", "书法", "棋牌", "阅读", "音乐", "太极"));
        boolean sportKeyword = containsKeyword(activeText, Arrays.asList("运动", "健身", "舞蹈", "户外", "球类", "体操"));
        if (age >= 80 && gentleKeyword) {
            reasonList.add("年龄适配：高龄长者更匹配舒缓型活动");
            return 8;
        }
        if (age < 70 && sportKeyword) {
            reasonList.add("年龄适配：活力型长者更匹配运动类活动");
            return 8;
        }
        if (gentleKeyword || sportKeyword) {
            return 4;
        }
        return 0;
    }

    /**
     * 计算社交协同得分
     */
    private int calcSocialScore(Elder elder, Set<Long> participantIdSet, Map<Long, Elder> participantElderMap, List<String> reasonList) {
        if (elder.getNursingGradeId() == null || CollectionUtils.isEmpty(participantIdSet)) {
            return 0;
        }
        int sameGradeCount = 0;
        for (Long participantId : participantIdSet) {
            Elder participantElder = participantElderMap.get(participantId);
            if (participantElder == null) {
                continue;
            }
            if (Objects.equals(participantElder.getNursingGradeId(), elder.getNursingGradeId())) {
                sameGradeCount++;
            }
        }
        if (sameGradeCount <= 0) {
            return 0;
        }
        reasonList.add("社交协同：同护理等级已报名 " + sameGradeCount + " 人");
        return Math.min(10, sameGradeCount * 2);
    }

    /**
     * 文本是否包含关键字
     */
    private boolean containsKeyword(String text, List<String> keywordList) {
        if (text == null || CollectionUtils.isEmpty(keywordList)) {
            return false;
        }
        for (String keyword : keywordList) {
            if (keyword != null && text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 组装活动匹配文本
     */
    private String buildActiveText(Active active) {
        StringBuilder builder = new StringBuilder();
        if (active.getTheme() != null) {
            builder.append(active.getTheme()).append(" ");
        }
        if (active.getName() != null) {
            builder.append(active.getName()).append(" ");
        }
        if (active.getContent() != null) {
            builder.append(active.getContent()).append(" ");
        }
        if (active.getAddress() != null) {
            builder.append(active.getAddress());
        }
        return builder.toString().toLowerCase();
    }

    /**
     * 推荐等级
     */
    private String getRecommendLevel(Integer score) {
        if (score == null) {
            return "可尝试";
        }
        if (score >= 80) {
            return "高匹配";
        }
        if (score >= 60) {
            return "中匹配";
        }
        return "可尝试";
    }
}
