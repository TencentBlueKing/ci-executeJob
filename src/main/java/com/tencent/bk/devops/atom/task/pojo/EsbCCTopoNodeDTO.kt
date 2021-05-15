import com.fasterxml.jackson.annotation.JsonProperty

data class EsbCCTopoNodeDTO(
    /**
     * topo节点ID
     */
    @JsonProperty("id")
    val id: Long,
    /**
     * topo节点类型
     */
    @JsonProperty("node_type")
    val nodeType: String
)
