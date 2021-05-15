# Job-作业执行(GITHUB版)
# 版本说明
本插件仅适用于对接蓝鲸社区版6.x/企业版3.x中的新版作业平台。

# 配置
插件上架时，需要配置蓝鲸智云相关参数，路径：设置->私有配置
- BK_APP_ID:应用ID，别名bk_app_code，需要使用已添加至蓝鲸ESB接口调用白名单中的应用ID，建议使用蓝鲸开发者中心内的蓝盾调用ESB接口专用应用ID（默认为bk_ci）
- BK_APP_SECRET: 应用ID对应的安全密钥(应用TOKEN)
- ESB_HOST: 蓝鲸ESB网关地址, 例如： http://paas.service.consul:80
- JOB_HOST: 蓝鲸JOB独立地址，例如： http://job.bktencent.com:80
- PAAS_HOST: 蓝鲸PaaS平台独立地址，例如： http://paas.bktencent.com:80
- IAM_HOST: 蓝鲸权限中心独立地址，例如： http://paas.bktencent.com:80/o/bk_iam

蓝鲸社区版6.x/企业版3.x用户可在蓝鲸中控机执行如下代码获取配置信息.
```shell script
source ${CTRL_DIR:-/data/install}/load_env.sh

echo "BK_APP_ID      $BK_CI_APP_CODE"
echo "BK_APP_SECRET  $BK_CI_APP_TOKEN"
echo "ESB_HOST       $BK_PAAS_PRIVATE_URL"
echo "JOB_HOST       $BK_JOB_PUBLIC_URL"
echo "PAAS_HOST       $BK_PAAS_PUBLIC_URL"
echo "IAM_HOST       $BK_PAAS_PUBLIC_URL/o/bk_iam"

# 参考输出
BK_APP_ID      bk_ci
BK_APP_SECRET  略
ESB_HOST       http://paas.service.consul:80
JOB_HOST       http://job.bktencent.com:80
PAAS_HOST       http://paas.bktencent.com:80
IAM_HOST       http://paas.bktencent.com:80/o/bk_iam
```
# 常见问题&解决方案
#### 1.权限问题
插件使用**流水线最后保存人**的身份去调用作业平台接口。
需要确保流水线最后保存人这个账号拥有作业平台的【执行方案运行】权限，且执行方案运行权限关联的实例包含插件中指定的所有主机、节点与动态分组（申请权限时建议关联实例资源选择对应业务下的任意资源以拥有将来新增资源的权限），若无权限可到蓝鲸权限中心进行申请。
