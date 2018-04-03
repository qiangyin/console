function ajaxDataController(requestUrl, params, gotoUrl, callback, async, method) {
    if (!method) {
        throw 'method 参数未设置.'
    }

    params = params || {};
    async = async || true;

    $.ajax({
        async: async,
        url: requestUrl,
        timeout: 5000,
        dataType: 'json',
        data: params,
        type: method,
        contentType: 'application/json;charset=utf-8',
        beforeSend: function () {
        },
        success: function (data) {
            if (gotoUrl) {
                window.location.href = gotoUrl;
            } else if (callback && isFunction(callback)) {
                callback(data);
            }
        },
        error: function (xhr) {
            try {
                console.log("httpStatus=" + xhr.status + ",descition=" + xhr.responseText);
                if (xhr.status == 'timeout') {
                    $(this).abort();
                }
            } catch (e) {
            }
        },
        complete: function (xhr) {
        }
    });
}

function isFunction(funName) {
    return Object.prototype.toString.call(funName) === '[object Function]';
}


function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return decodeURIComponent(r[2]);
    }
    return null;
}