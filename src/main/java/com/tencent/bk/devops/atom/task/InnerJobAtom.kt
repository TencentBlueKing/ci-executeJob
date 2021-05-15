package com.tencent.bk.devops.atom.task

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.bk.devops.atom.AtomContext
import com.tencent.bk.devops.atom.spi.AtomService
import com.tencent.bk.devops.atom.spi.TaskAtom
import com.tencent.bk.devops.atom.task.pojo.EsbGlobalVarV3DTO
import com.tencent.bk.devops.atom.task.pojo.ExecuteJobPlanReq
import com.tencent.bk.devops.atom.task.pojo.InnerJobParam
import com.tencent.bk.devops.atom.task.utils.JobUtils
import com.tencent.bk.devops.atom.task.utils.Keys
import com.tencent.bk.devops.atom.utils.json.JsonUtil
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory

@AtomService(paramClass = InnerJobParam::class)
class InnerJobAtom : TaskAtom<InnerJobParam> {

    private var jobHost: String = ""
    private var iamHost: String = ""
    private var esbHost: String = ""
    private var paasHost: String = ""
    private var appId: String = ""
    private var appSecret: String = ""
    private var timeout: Long? = null

    override fun execute(atomContext: AtomContext<InnerJobParam>) {
        val param = atomContext.param
        timeout = param.timeout
        logger.info("param:${JsonUtil.toJson(param)}")
        checkParam(param)
        exceute(param)
        logger.info("atom run success")
    }

    companion object {
        val logger = LoggerFactory.getLogger(InnerJobAtom::class.java)
    }

    fun exceute(param: InnerJobParam) {
        logger.info("Begin to execute Job-Plan")
        val jobHost = getConfigValue(Keys.JOB_HOST, param)
        val iamHost = getConfigValue(Keys.IAM_HOST, param)
        val esbHost = getConfigValue(Keys.ESB_HOST, param)
        val paasHost = getConfigValue(Keys.PAAS_HOST, param)
        val appId = getConfigValue(Keys.BK_APP_ID, param)
        val appSecret = getConfigValue(Keys.BK_APP_SECRET, param)
        if (!checkVariable(jobHost, iamHost, esbHost, paasHost, appId, appSecret)) {
            throw RuntimeException("Please contact admin to add private configurations for this plugin")
        }
        this.jobHost = jobHost!!
        this.iamHost = iamHost!!
        this.esbHost = esbHost!!
        this.paasHost = paasHost!!
        this.appId = appId!!
        this.appSecret = appSecret!!

        executeJobPlan(param)
    }

    private fun getGlobalVarDocLink(): String {
        return "<a href='$paasHost/esb/api_docs/system/JOBV3/execute_job_plan/'>API Doc of execute_job_plan</a>"
    }

    private fun executeJobPlan(
        param: InnerJobParam
    ) {
        val bizId = param.bizId
        val buildId = param.pipelineBuildId
        val taskId = param.pipelineTaskId
        val planId = param.planId
        var operator = param.pipelineStartUserName

        val lastModifyUser = param.pipelineUpdateUserName
        val globalVarStr = param.globalVarStr
        var globalVarList: List<EsbGlobalVarV3DTO>? = null
        val wrongGlobalVarMsg = "Format of globalVar is incorrect, please refer to the global_var_list param of " + getGlobalVarDocLink() + " to fill, example:[{\"name\":\"var1\",\"value\":\"var1_value\"},{\"name\":\"var2\",\"value\":\"var1_value\"},{\"id\":\"1\",\"server\":{ \"ip_list\": [{\"bk_cloud_id\": 0,\"ip\": \"10.0.0.1\"}]}}]"
        if (!StringUtils.isBlank(globalVarStr)) {
            try {
                globalVarList = JsonUtil.fromJson(
                    globalVarStr.trim(),
                    object : TypeReference<List<EsbGlobalVarV3DTO>>() {}
                )
            } catch (e: Exception) {
                throw RuntimeException(wrongGlobalVarMsg)
            }
        }
        if (globalVarList == null) {
            throw RuntimeException(wrongGlobalVarMsg)
        }

        if (null != lastModifyUser && operator != lastModifyUser) {
            // 以流水线的最后一次修改人身份执行
            logger.info("operator:$operator, lastModifyUser:$lastModifyUser")
            operator = lastModifyUser
        }
        logger.info("The link of Job-Plan:<a target='_blank' href='$jobHost/api_plan/$planId'>Go to Job to see plan detail</a>, please confirm that the last modifier of this pipeline [$operator] can execute the plan correctly in $jobHost")
        logger.warn("If permission is required, you can go to <a target='_blank' href='$iamHost/perm-apply'>BK IAM Center</a> to apply the permission of businessId [$bizId] and hosts related, and you can contact business operator or admin to approve applications for you")
        val executeJobPlanReq = ExecuteJobPlanReq(
            appCode = this.appId,
            appSecret = this.appSecret,
            username = operator,
            bizId = bizId,
            planId = planId,
            globalVars = globalVarList
        )

        val taskInstanceId = JobUtils.executeJobPlan(executeJobPlanReq, this.esbHost)
        showJobDetailLink(taskInstanceId)
        val startTime = System.currentTimeMillis()

        checkStatus(
            bizId = bizId,
            startTime = startTime,
            taskId = taskId,
            taskInstanceId = taskInstanceId,
            operator = operator,
            buildId = buildId,
            esbHost = esbHost
        )

        showJobDetailLink(taskInstanceId)
    }

    private fun showJobDetailLink(taskInstanceId: Long) {
        logger.info(JobUtils.getDetailUrl(jobHost, taskInstanceId))
    }

    private fun checkStatus(
        bizId: Long,
        startTime: Long,
        taskInstanceId: Long,
        operator: String,
        buildId: String,
        taskId: String,
        esbHost: String
    ) {

        var jobSuccess = true
        while (jobSuccess) {
            if (System.currentTimeMillis() - startTime > timeout!! * 1000) {
                throw RuntimeException("This job plugin executing timeout [${timeout}s], while the job is still running in Job and you can click the link printed to go to Job Platform to stop it manually if you need to")
            }
            Thread.sleep(2000)
            val taskResult = JobUtils.getTaskResult(appId, appSecret, bizId, taskInstanceId, operator, esbHost)
            if (taskResult.isFinish) {
                if (taskResult.success) {
                    logger.info("[$buildId]|SUCCEED|taskInstanceId=$taskId|${taskResult.msg}")
                    jobSuccess = false
                } else {
                    logger.info("[$buildId]|FAIL|taskInstanceId=$taskId|${taskResult.msg}")
                    throw RuntimeException("job execute fail, mssage:${taskResult.msg}")
                }
            } else {
                logger.info("Waiting for job:$taskInstanceId", taskId)
                showJobDetailLink(taskInstanceId)
            }
        }
        logger.info("Job time consuming: ${System.currentTimeMillis() - startTime}ms")
    }

    private fun getConfigValue(key: String, param: InnerJobParam): String? {
        val configMap = param.bkSensitiveConfInfo
        if (configMap == null) {
            logger.warn("Private configuration is missing, please contact plugin publisher to add")
        }
        if (configMap.containsKey(key)) {
            return configMap[key]
        }
        return null
    }

    private fun checkVariable(jobHost: String?, iamHost: String?, esbHost: String?, paasHost: String?, appId: String?, appSecret: String?): Boolean {
        if (jobHost.isNullOrBlank()) {
            logger.error("Private configuration of key JOB_HOST is missing, please contact plugin publisher to add")
            return false
        }
        if (iamHost.isNullOrBlank()) {
            logger.error("Private configuration of key IAM_HOST is missing, please contact plugin publisher to add")
            return false
        }
        if (esbHost.isNullOrBlank()) {
            logger.error("Private configuration of key ESB_HOST is missing, please contact plugin publisher to add")
            return false
        }
        if (paasHost.isNullOrBlank()) {
            logger.error("Private configuration of key PAAS_HOST is missing, please contact plugin publisher to add")
            return false
        }
        if (appId.isNullOrBlank()) {
            logger.error("Private configuration of key BK_APP_ID is missing, please contact plugin publisher to add")
            return false
        }
        if (appSecret.isNullOrBlank()) {
            logger.error("Private configuration of key BK_APP_SECRET is missing, please contact plugin publisher to add")
            return false
        }
        return true
    }

    private fun checkParam(param: InnerJobParam) {
        if (param.bizId <= 0) {
            throw RuntimeException("bizId must be an positive integer")
        }
        if (param.planId <= 0) {
            throw RuntimeException("planId must be an positive integer")
        }
    }
}
