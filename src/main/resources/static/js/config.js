
/***
 * 获取全局API接口
 * @param key 接口标识
 * @returns 具体某个API地址
 */
function globalApi(key) {
    var globalConfig = {
        "api.domain": "/api/v1",
        "api.rootPath": "/log",
        "pod.list": "/pod/list",
    };
    var api = globalConfig[key] || "/unknown/"
    return api + "?r=" + Math.random();
}