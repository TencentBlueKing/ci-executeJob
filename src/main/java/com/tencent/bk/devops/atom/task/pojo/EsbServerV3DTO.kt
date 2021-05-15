package com.tencent.bk.devops.atom.task.pojo

import EsbCCTopoNodeDTO
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 服务器定义-ESB
 */
data class EsbServerV3DTO(
    @JsonProperty("ip_list")
    val ips: List<EsbIpDTO>?,
    @JsonProperty("dynamic_group_list")
    val dynamicGroups: List<EsbDynamicGroupDTO>?,
    @JsonProperty("topo_node_list")
    val topoNodes: List<EsbCCTopoNodeDTO>?
)
