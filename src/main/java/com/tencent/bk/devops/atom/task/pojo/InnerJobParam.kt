package com.tencent.bk.devops.atom.task.pojo

import com.tencent.bk.devops.atom.pojo.AtomBaseParam
import lombok.Data
import lombok.EqualsAndHashCode

@Data
@EqualsAndHashCode(callSuper = true)
class InnerJobParam : AtomBaseParam() {
    val bizId: Long = -1L
    val planId: Long = -1L
    val timeout: Long = 600L
    val globalVarStr: String = ""
}
