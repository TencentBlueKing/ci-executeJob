package com.tencent.bk.devops.atom.task.pojo

import com.fasterxml.jackson.annotation.JsonProperty
import com.tencent.bk.devops.atom.utils.json.annotation.SkipLogField

/**
 * 执行作业请求
 */
data class ExecuteJobPlanReq(
    @SkipLogField
    @JsonProperty("bk_app_code")
    val appCode: String,

    @SkipLogField
    @JsonProperty("bk_app_secret")
    val appSecret: String,

    @JsonProperty("bk_username")
    val username: String,

    @JsonProperty("bk_biz_id")
    val bizId: Long,

    @JsonProperty("job_plan_id")
    val planId: Long,

    @JsonProperty("global_var_list")
    val globalVars: List<EsbGlobalVarV3DTO>? = null
)
