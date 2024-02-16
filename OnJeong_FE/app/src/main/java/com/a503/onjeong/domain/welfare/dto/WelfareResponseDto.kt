package com.a503.onjeong.domain.welfare.dto

import com.google.gson.annotations.SerializedName

data class WelfareResponseDto(
    @SerializedName("currentCount")
    val currentCount: Int,
    @SerializedName("data")
    val data: List<WelfareData>
)

data class WelfareData(
    @SerializedName("부서명")
    val departmentName: String,
    @SerializedName("사용자구분")
    val userType: String,
    @SerializedName("상세조회URL")
    val detailUrl: String,
    @SerializedName("서비스ID")
    val serviceId: String,
    @SerializedName("서비스명")
    val serviceName: String,
    @SerializedName("서비스목적요약")
    val serviceSummary: String,
    @SerializedName("서비스분야")
    val serviceArea: String,
    @SerializedName("선정기준")
    val selectionCriteria: String,
    @SerializedName("소관기관명")
    val organizationName: String,
    @SerializedName("소관기관유형")
    val organizationType: String,
    @SerializedName("소관기관코드")
    val organizationCode: String,
    @SerializedName("신청기한")
    val applicationDeadline: String,
    @SerializedName("신청방법")
    val applicationMethod: String,
    @SerializedName("전화문의")
    val phoneInquiry: String,
    @SerializedName("접수기관")
    val receptionAgency: String,
    @SerializedName("조회수")
    val viewCount: Int,
    @SerializedName("지원내용")
    val supportContent: String,
    @SerializedName("지원대상")
    val supportTarget: String,
    @SerializedName("지원유형")
    val supportType: String
)
