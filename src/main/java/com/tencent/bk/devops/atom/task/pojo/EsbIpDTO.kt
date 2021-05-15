package com.tencent.bk.devops.atom.task.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class EsbIpDTO(
    @JsonProperty("bk_cloud_id")
    val cloudAreaId: Long,
    @JsonProperty("ip")
    val ip: String
)
