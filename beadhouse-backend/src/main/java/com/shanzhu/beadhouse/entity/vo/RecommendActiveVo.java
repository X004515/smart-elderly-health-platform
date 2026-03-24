package com.shanzhu.beadhouse.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "活动推荐响应实体")
public class RecommendActiveVo {
    @ApiModelProperty(value = "活动编号", example = "1")
    private Long id;
    @ApiModelProperty(value = "活动分类编号", example = "1")
    private Long typeId;
    @ApiModelProperty(value = "活动分类名称", example = "文艺活动")
    private String typeName;
    @ApiModelProperty(value = "活动主题", example = "春季联欢")
    private String theme;
    @ApiModelProperty(value = "活动名称", example = "银龄音乐会")
    private String name;
    @ApiModelProperty(value = "活动内容", example = "音乐表演与互动")
    private String content;
    @ApiModelProperty(value = "活动地点", example = "多功能厅")
    private String address;
    @ApiModelProperty(value = "组织者", example = "张老师")
    private String organizer;
    @ApiModelProperty(value = "联系电话", example = "13800000000")
    private String phone;
    @ApiModelProperty(value = "活动日期", example = "2026-03-31")
    private String activeDate;
    @ApiModelProperty(value = "推荐分", example = "86")
    private Integer score;
    @ApiModelProperty(value = "推荐等级", example = "高匹配")
    private String recommendLevel;
    @ApiModelProperty(value = "命中的兴趣标签", example = "[\"音乐\", \"阅读\"]")
    private List<String> matchLabelList;
    @ApiModelProperty(value = "推荐原因", example = "[\"兴趣匹配：音乐\", \"时间窗口：活动在 7 天内\"]")
    private List<String> reasonList;
}
