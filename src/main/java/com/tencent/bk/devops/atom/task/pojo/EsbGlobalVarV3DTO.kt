package com.tencent.bk.devops.atom.task.pojo

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class EsbGlobalVarV3DTO @JsonCreator constructor(
    @JsonProperty("id")
    val id: Long?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("value")
    val value: String?,
    @JsonProperty("server")
    val server: EsbServerV3DTO?
)
