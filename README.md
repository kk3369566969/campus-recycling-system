# 低碳校园资源循环流转系统（首版）

这是按《2026软件构造分组实验v3》搭建的 Java 17 + Swing + SQLite Maven 桌面应用首版。

## 首版能力

- 用户注册、登录、SHA-256 密码存储、普通用户 / 管理员角色
- 闲置物品发布、编辑、下架、分类/关键字/价格筛选、收藏
- 发起流转意向、完成流转、历史记录持久化
- 流转完成后的评价与信用分
- 类别流转统计、低碳减排估算、CSV 导出
- 管理员查看用户、下架违规物品、查看全平台统计
- SQLite 初始化脚本、关键操作日志、JUnit 核心业务测试

## 运行

```bash
mvn clean verify
mvn package
java -jar target/campus-cycle-0.1.0.jar
```

首次启动会在当前目录创建 `data/campus-cycle.db`。演示账号：`admin / admin123`，普通用户可自行注册。

## 设计模式

- `DatabaseManager`：单例模式，统一管理 SQLite 连接。
- `DaoFactory`：工厂模式，统一创建数据访问对象。
- `ItemFilter`：策略模式，组合关键字、分类和价格过滤器。
- `EventPublisher` + `AuditLogListener`：观察者模式，记录关键业务操作。

## 目录

`domain` 领域对象；`repository` JDBC 数据访问层；`service` 业务服务层；`ui` Swing 界面；`filter` 检索策略；`events` 领域事件；`db/schema.sql` 数据库脚本。

完整课程设计报告见 `docs/EXPERIMENT_REPORT.md`，重构与质量记录见 `docs/REFACTORING_LOG.md`。`mvn verify` 会执行 JUnit、SQLite 集成测试和 PMD 静态分析，分析报告写入 `target/pmd.xml`。
