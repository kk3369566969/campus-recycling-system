# 部署与运行说明

## 环境

- JDK 17 或更高版本
- Maven 3.9 或更高版本
- Windows、macOS、Linux 均可运行 Swing 界面

## 构建

在项目根目录执行：

```bash
mvn clean test
mvn package
```

`target/campus-cycle-0.1.0.jar` 是包含 SQLite JDBC 依赖的可执行 Jar。双击或执行 `java -jar target/campus-cycle-0.1.0.jar` 即可启动。

## 数据与日志

首次运行自动创建 `data/campus-cycle.db` 并执行 `database/schema.sql` 的等价初始化。关键操作日志写入 `data/app.log`，统计导出默认写入 `data/statistics.csv`。

## 演示账号

- 管理员：`admin / admin123`
- 普通用户：`demo / 123456`

生产环境使用前应删除演示数据库，重新注册真实账号，并按课程要求补充备份恢复、集成测试和发布签名。
