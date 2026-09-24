/* =============================================================================
 * 统一认证中心（microservice-oauth）登录页 / 授权确认页 共用脚本
 *
 * 提供：URL 参数读取、表单式 POST、统一错误消息提取、行内提示与按钮加载态。
 * 不依赖 jQuery / layer 等第三方库。
 * ========================================================================== */
(function (global) {
  "use strict";

  /** Sa-Token SaResult 的成功业务码 */
  var OK_CODE = 200;

  /** 服务端「系统异常」兜底标题，出现时改用 detail 展示 */
  var GENERIC_TITLE = "系统异常";

  /**
   * 读取当前地址栏的查询参数。
   *
   * @returns {URLSearchParams} 可读写的查询参数对象
   */
  function query() {
    return new URLSearchParams(global.location.search);
  }

  /**
   * 把服务端返回的错误体转换成一句可直接展示给用户的文案。
   *
   * <p>兼容 Spring ProblemDetail（title / detail）与 Sa-Token SaResult（msg）。
   *
   * @param {object|null} body 已解析的响应体
   * @param {number} status HTTP 状态码
   * @returns {string} 提示文案
   */
  function describe(body, status) {
    if (body && typeof body === "object") {
      var title = typeof body.title === "string" ? body.title : "";
      var detail = typeof body.detail === "string" ? body.detail : "";
      var msg = typeof body.msg === "string" ? body.msg : "";

      if (title && title !== GENERIC_TITLE) {
        return title;
      }
      if (detail) {
        return detail;
      }
      if (title) {
        return title;
      }
      if (msg) {
        return msg;
      }
    }
    return "请求失败（HTTP " + status + "）";
  }

  /**
   * 以 application/x-www-form-urlencoded 方式发起请求，并解析 JSON 响应。
   *
   * <p>非 2xx 响应，或 SaResult 的业务码不为 200 时，均以 Error 形式 reject，
   * message 为可直接展示的文案。
   *
   * @param {string} url 请求地址
   * @param {object} [options] 可选：method、body
   * @returns {Promise<object>} 响应体对象
   */
  function submit(url, options) {
    var opts = options || {};

    return global
      .fetch(url, {
        method: opts.method || "POST",
        headers: { Accept: "application/json" },
        body: opts.body || null,
        credentials: "same-origin",
      })
      .then(function (response) {
        return response.text().then(function (text) {
          var body = null;
          if (text) {
            try {
              body = JSON.parse(text);
            } catch (ignored) {
              body = null;
            }
          }

          if (!response.ok) {
            throw new Error(describe(body, response.status));
          }
          if (body && typeof body.code === "number" && body.code !== OK_CODE) {
            throw new Error(body.msg || describe(body, response.status));
          }
          return body || {};
        });
      });
  }

  /**
   * 展示行内提示。
   *
   * @param {HTMLElement} box 提示容器（.auth-alert）
   * @param {"error"|"success"|"warning"} type 提示类型
   * @param {string} message 提示文案
   */
  function alert(box, type, message) {
    if (!box) {
      return;
    }
    var text = box.querySelector(".auth-alert__text");
    if (text) {
      text.textContent = message;
    }
    box.setAttribute("data-type", type);
    box.setAttribute("data-visible", "true");
  }

  /**
   * 隐藏行内提示。
   *
   * @param {HTMLElement} box 提示容器
   */
  function clearAlert(box) {
    if (box) {
      box.removeAttribute("data-visible");
    }
  }

  /**
   * 切换按钮加载态（加载中禁用按钮、显示旋转指示器并播报给读屏软件）。
   *
   * @param {HTMLButtonElement} button 目标按钮
   * @param {boolean} loading 是否加载中
   * @param {string} [label] 加载中的无障碍文案
   */
  function loading(button, loading, label) {
    if (!button) {
      return;
    }
    if (loading) {
      if (!button.hasAttribute("data-idle-label")) {
        button.setAttribute("data-idle-label", button.textContent.trim());
      }
      if (label) {
        button.setAttribute("aria-label", label);
      }
    } else if (button.hasAttribute("data-idle-label")) {
      button.removeAttribute("aria-label");
    }
    button.classList.toggle("is-loading", loading);
    button.disabled = loading;
    button.setAttribute("aria-busy", loading ? "true" : "false");
  }

  /**
   * 在 URL 上追加查询参数，自动处理已有 ? / & 以及 #fragment。
   *
   * @param {string} url 目标地址
   * @param {object} params 参数键值对，值为 null / undefined / "" 的键会被忽略
   * @returns {string} 拼接后的地址
   */
  function appendParams(url, params) {
    var fragment = "";
    var hashIndex = url.indexOf("#");
    if (hashIndex !== -1) {
      fragment = url.slice(hashIndex);
      url = url.slice(0, hashIndex);
    }

    var parts = [];
    Object.keys(params || {}).forEach(function (key) {
      var value = params[key];
      if (value === null || value === undefined || value === "") {
        return;
      }
      parts.push(encodeURIComponent(key) + "=" + encodeURIComponent(value));
    });

    if (parts.length > 0) {
      var separator = "?";
      if (url.indexOf("?") !== -1) {
        separator = url.charAt(url.length - 1) === "?" || url.charAt(url.length - 1) === "&" ? "" : "&";
      }
      url += separator + parts.join("&");
    }

    return url + fragment;
  }

  global.Auth = {
    query: query,
    describe: describe,
    submit: submit,
    alert: alert,
    clearAlert: clearAlert,
    loading: loading,
    appendParams: appendParams,
  };
})(window);
