# 校园失物招领系统

## 项目简介

这是一个面向校园场景的失物招领系统，包含 **Spring Boot 后端** 和 **微信小程序前端** 两部分。系统用于帮助学生或校园用户发布失物招领信息、查找遗失物品、提交认领申请，并通过评论与收藏等功能提升信息流转效率。

项目采用前后端分离思路实现：

- 后端负责用户认证、物品管理、认领流程、评论、收藏、文件上传等核心业务
- 小程序负责信息展示、交互操作和用户登录流程
- 认证方式基于 **微信登录 + JWT**
- 数据存储基于 **MySQL**
- 数据访问层使用 **MyBatis**

本项目适合作为：

- 校园失物招领课程设计
- Spring Boot + MyBatis 综合实训项目
- 微信小程序与 Java 后端联调示例
- 基于 JWT 的轻量级认证项目参考

## 功能简介

### 用户功能

- 微信小程序登录
- 获取当前用户信息
- JWT 登录态保存与请求鉴权

### 物品管理

- 发布失物招领 / 寻物启事
- 查看物品详情
- 条件筛选与分页查询
- 编辑自己发布的物品
- 更新物品状态
- 撤销发布
- 删除物品
- 浏览量统计

### 认领流程

- 提交认领申请
- 查看我的认领记录
- 查看收到的认领申请
- 发布者确认认领
- 发布者拒绝认领
- 确认后自动将物品标记为已认领
- 自动拒绝同一物品的其他待处理申请

### 评论与收藏

- 查看评论列表
- 发表评论
- 删除自己的评论
- 收藏 / 取消收藏
- 查看我的收藏
- 评论数、收藏数自动维护

### 文件上传

- 支持图片上传
- 用于物品发布时上传物品照片

## 技术栈

### 后端

- Java 21
- Spring Boot 3.5.13
- MyBatis Spring Boot Starter 3.0.5
- MySQL
- Lombok
- JJWT 0.12.6
- Maven

### 前端

- 微信小程序原生开发

## 项目结构

```text
lost-found-backend/
├─ src/main/java/com/example/lost_found_backend/
│  ├─ common/           # 通用返回、异常处理、JWT 工具与拦截器
│  ├─ config/           # WebMvc 配置
│  ├─ controller/       # 控制器层
│  ├─ entity/           # 实体类
│  ├─ mapper/           # Mapper 接口
│  ├─ service/          # 服务接口
│  └─ service/impl/     # 服务实现
├─ src/main/resources/
│  ├─ mapper/           # MyBatis XML
│  ├─ db/schema.sql     # 数据库建表脚本
│  └─ application.yml   # 后端配置文件
├─ miniprogram/         # 微信小程序代码
├─ pom.xml              # Maven 配置
└─ README.md
```

## 运行环境

请确保本地具备以下环境：

- JDK 21
- Maven 3.9+
- MySQL 8.x
- 微信开发者工具

## 后端启动说明

### 1. 创建数据库

先在 MySQL 中创建数据库：

```sql
CREATE DATABASE lost_and_found DEFAULT CHARACTER SET utf8mb4;
```

然后执行项目中的建表脚本：

```text
src/main/resources/db/schema.sql
```

### 2. 修改后端配置

编辑 `src/main/resources/application.yml`：

- 配置 MySQL 连接地址、用户名、密码
- 配置微信小程序 `appid` 与 `secret`
- 如需部署到服务器，可修改端口、JWT 密钥等配置

当前默认配置包括：

- 数据库地址：`jdbc:mysql://localhost:3306/lost_and_found`
- 服务端口：`8080`
- JWT 有效期：`7 天`

### 3. 启动后端

在项目根目录执行：

```bash
./mvnw spring-boot:run
```

Windows 也可以使用：

```bash
mvnw.cmd spring-boot:run
```

### 4. 编译检查

```bash
./mvnw -DskipTests compile
```

## 小程序启动说明

### 1. 打开微信开发者工具

导入项目根目录下的小程序配置，前端代码位于：

```text
miniprogram/
```

### 2. 修改后端地址

编辑文件：

```text
miniprogram/app.js
```

默认后端地址为：

```js
baseUrl: 'http://localhost:8080'
```

如果后端部署到了其他服务器，请改成对应域名或 IP。

### 3. 配置合法域名

如果在真机或正式环境运行，需要在微信公众平台中配置服务器域名。

## 主要接口模块

### 用户接口

- `POST /api/user/login`
- `GET /api/user/info`

### 物品接口

- `POST /api/item/publish`
- `GET /api/item/{id}`
- `GET /api/item/list`
- `GET /api/item/my`
- `PUT /api/item/{id}`
- `PUT /api/item/{id}/status`
- `PUT /api/item/{id}/revoke`
- `DELETE /api/item/{id}`

### 认领接口

- `POST /api/claim/apply`
- `PUT /api/claim/{id}/confirm`
- `PUT /api/claim/{id}/reject`
- `GET /api/claim/my`
- `GET /api/claim/received`

### 评论接口

- `POST /api/comment/add`
- `GET /api/comment/list`
- `DELETE /api/comment/{id}`

### 收藏接口

- `POST /api/collection/toggle`
- `GET /api/collection/my`
- `GET /api/collection/check`

### 上传接口

- `POST /api/upload/image`

## 业务说明

### 物品状态约定

- `0`：待认领
- `1`：已认领
- `2`：已失效

### 物品类型约定

- `1`：失物招领
- `2`：寻物启事

### 认领状态约定

- `0`：待处理
- `1`：已确认
- `2`：已拒绝

## 项目亮点

- 基于微信登录与 JWT 的轻量认证方案
- 采用分层架构，结构清晰，便于维护
- 评论、收藏、认领等模块业务完整
- 认领确认后自动处理其他待审核申请
- 接口返回结构统一，便于前端调用
- 适合课程设计、毕业设计或教学演示

## 注意事项

- `application.yml` 中的数据库密码、JWT 密钥、微信 `appid/secret` 建议根据实际环境修改
- 当前小程序默认请求本地后端 `http://localhost:8080`
- 若要上线，需补充正式域名、HTTPS、对象存储、日志与安全配置

## 后续可扩展方向

- 增加管理员后台
- 增加消息通知功能
- 增加审核机制
- 增加分类管理与标签系统
- 增加更完整的测试用例
- 增加 Docker 部署支持

## 许可说明

本项目当前未单独声明开源许可证，如需公开发布，建议补充 License 文件。
