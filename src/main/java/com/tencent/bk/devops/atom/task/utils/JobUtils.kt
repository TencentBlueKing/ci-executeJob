package com.tencent.bk.devops.atom.task.utils

import com.tencent.bk.devops.atom.task.pojo.ExecuteJobPlanReq
import com.tencent.bk.devops.atom.task.service.JobResourceV3Api

object JobUtils {

    private val jobResourceV3Api = JobResourceV3Api()

    fun executeJobPlan(executeJobPlanReq: ExecuteJobPlanReq, esbHost: String): Long {
        return jobResourceV3Api.executeJobPlan(executeJobPlanReq, esbHost)
    }

    fun getTaskResult(
        appId: String,
        appSecret: String,
        bizId: Long,
        taskInstanceId: Long,
        operator: String,
        esbHost: String
    ): JobResourceV3Api.TaskResult {
        return jobResourceV3Api.getTaskResult(appId, appSecret, bizId, taskInstanceId, operator, esbHost)
    }

    fun getDetailUrl(jobHost: String, taskInstanceId: Long): String {
        return "<a target='_blank' href='$jobHost/api_execute/$taskInstanceId'>到作业平台查看详情(Go to Job for Detail)</a>"
    }
}
