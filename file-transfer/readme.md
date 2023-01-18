

拖拽发送文件

参数优先级
命令行参数
配置文件参数
默认配置参数



# 问题

## 日志无法写入文件

```text
18:12:21,379 |-INFO in ch.qos.logback.core.rolling.RollingFileAppender[FILE_APPENDER] - File property is set to [null]
18:12:21,380 |-WARN in ch.qos.logback.core.rolling.RollingFileAppender[FILE_APPENDER] - Encoder has not been set. Cannot invoke its init method.
18:12:21,380 |-ERROR in ch.qos.logback.core.rolling.RollingFileAppender[FILE_APPENDER] - No encoder set for the appender named "FILE_APPENDER".
```
