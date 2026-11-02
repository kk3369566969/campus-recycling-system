# 重构与质量记录

## 重构目标

让桌面 GUI 只负责事件处理和展示，业务规则集中在服务层，数据库访问集中在 Repository 层，并让新增筛选条件、日志监听器和后续统计模块时不需要修改已有核心流程。

## 已完成重构

| 问题 | 原始风险 | 重构结果 | 验证方式 |
| --- | --- | --- | --- |
| GUI 直接承载业务判断 | 事件方法过长，难以测试 | 抽取 `AuthService`、`MarketplaceService`、`TransactionService` | `CoreFlowIntegrationTest` |
| SQL 分散在界面逻辑 | 难以替换数据库，容易泄漏连接 | 抽取 `UserRepository`、`ItemRepository`、`TransactionRepository` | SQLite 集成测试 |
| 筛选条件 if/else 堆叠 | 增加条件会修改主流程 | 使用 `ItemFilter` 策略与 `CompositeFilter` | `FilterTest` |
| 日志和业务代码耦合 | 日志改动会影响核心流程 | 使用事件发布器和 `AuditLogListener` | 操作日志检查 |
| 实体状态可被任意 setter 修改 | 容易生成非法业务状态 | 使用 Java record 作为只读领域对象 | 编译和集成测试 |

## 质量门禁

- Java 17 编译无 `-Xlint:all` 警告。
- JUnit 单元测试和 SQLite 集成测试必须通过。
- `mvn verify` 执行 PMD 静态分析并生成报告。
- `mvn package` 生成带依赖的可执行 Jar。
- 数据访问必须使用参数化 SQL 和 try-with-resources。

## 后续重构候选

- 将 Repository 接口抽象出来，增加内存实现以提升纯单元测试速度。
- 将管理员权限校验下沉到服务层，避免只依赖 GUI 的隐藏标签页。
- 将 Swing 窗口拆分为更细的 View 和 Controller，降低 `MainFrame` 的方法数量。
- 增加数据库事务边界，使完成流转、更新物品状态和信用分更新具备原子性。
