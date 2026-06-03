
### 测试步骤
1. 启动 Spring Boot 应用，确保过滤器生效。
2. 访问 `http://localhost:8080/xss-demo`
3. 在文本框中输入恶意载荷，例如：
   ```
   <script>alert('XSS Attack');</script>
   ```
   或
   ```
   <img src=x onerror=alert(1)>
   ```
4. 分别点击 **“表单参数提交”** 和 **“JSON请求体提交”** 按钮。
5. 观察右侧 “后端清洗结果” 面板：恶意标签被移除或转义，输出安全的纯文本。
6. 左侧面板展示你实际输入的原始字符串，两者对比清晰说明 XSS 清洗生效。
