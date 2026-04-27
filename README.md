# 地铁失物招领系统

一款专业的地铁失物招领系统，采用 Angular + Java (Spring Boot) + MySQL 技术栈开发，支持乘客发布失物招领信息、在线认领，管理员审核管理和统计分析。

## 系统架构

- **前端**: Angular 15 + Angular Material
- **后端**: Spring Boot 2.7 + Spring Security + JWT + JPA
- **数据库**: MySQL 8.0

## 功能特性

### 乘客功能
- 用户注册和登录
- 浏览已审核通过的失物招领信息
- 发布失物启事或招领启事
- 在线提交认领申请
- 查看我的发布和我的认领
- 列表分页展示

### 管理员功能
- 审核失物招领信息（通过/拒绝）
- 标记已认领/已归还
- 删除虚假信息
- 管理认领申请
- 统计分析（失物类型、线路分布、归还率）
- 数据导出（Excel格式）
- 列表分页展示

## 环境要求

- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+
- Angular CLI 15+

## 快速启动

### 1. 数据库初始化

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 执行初始化脚本
source /path/to/project/database/init.sql
```

或者在 MySQL 客户端中手动执行 `database/init.sql` 文件内容。

**注意**: 数据库连接配置在 `backend/src/main/resources/application.properties` 中，根据实际情况修改用户名和密码：
```properties
spring.datasource.username=root
spring.datasource.password=root
```

### 2. 启动后端

```bash
cd backend

# 使用 Maven 启动
mvn spring-boot:run

# 或者先打包再运行
mvn clean package
java -jar target/lost-found-1.0.0.jar
```

后端服务将在 `http://localhost:8080` 启动。

### 3. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
ng serve
```

前端服务将在 `http://localhost:4200` 启动。

## 测试账号

系统初始化时已创建以下测试账号：

### 管理员账号
- 用户名: `admin`
- 密码: `admin123`

### 乘客测试账号
- 用户名: `user1` / `user2`
- 密码: `123456`

## 功能验证指南

### 乘客功能验证

1. **用户注册**
   - 访问 `http://localhost:4200/register`
   - 填写注册信息（用户名、密码、手机号、邮箱等）
   - 点击注册，系统自动登录并跳转到首页

2. **用户登录**
   - 访问 `http://localhost:4200/login`
   - 输入用户名和密码（如 user1 / 123456）
   - 点击登录，成功后跳转到首页

3. **浏览失物招领**
   - 访问 `http://localhost:4200/lost` 查看全部信息
   - 可筛选查看寻物启事或招领启事
   - 点击列表项可查看详情

4. **发布信息**
   - 登录后点击导航栏"发布信息"
   - 选择信息类型（寻物/招领）
   - 填写标题、详细描述、物品类型、线路、联系方式等
   - 点击发布，信息将进入待审核状态

5. **查看我的发布**
   - 登录后点击"我的发布"
   - 可查看所有发布的信息及其状态
   - 待审核或已拒绝的信息可删除

6. **在线认领**
   - 在失物招领列表中找到属于您的物品
   - 点击进入详情页
   - 在右侧填写认领理由（需详细描述物品特征）
   - 点击提交认领申请

7. **查看我的认领**
   - 登录后点击"我的认领"
   - 可查看认领申请的审核状态

### 管理员功能验证

1. **管理员登录**
   - 使用 admin / admin123 登录
   - 登录后导航栏会显示管理后台入口

2. **信息审核**
   - 点击"信息管理"进入审核页面
   - 默认显示待审核的信息
   - 可按状态、类型、关键词筛选
   - 点击"通过"或"拒绝"进行审核
   - 拒绝时需填写拒绝原因

3. **标记已归还**
   - 对于已认领的物品，管理员可标记为"已归还"
   - 点击"标记已归还"按钮

4. **删除信息**
   - 对于虚假或违规信息，管理员可直接删除
   - 点击删除按钮，确认后删除

5. **认领管理**
   - 点击"认领管理"查看所有认领申请
   - 对待审核的申请进行通过或拒绝操作
   - 通过后对应的物品状态将更新为"已认领"

6. **统计分析**
   - 点击"统计分析"查看数据统计
   - 概览：总记录数、待审核、已通过、已归还、归还率等
   - 线路分布：各线路的失物招领数量占比
   - 物品类型分布：各类物品的数量占比
   - 月度趋势：近6个月的发布和归还数据

7. **数据导出**
   - 在统计分析页面
   - 点击"导出记录"导出所有失物招领记录
   - 点击"导出统计"导出统计数据
   - 文件将以 Excel 格式下载

## 项目结构

```
LostInSubway/
├── database/                 # 数据库脚本
│   └── init.sql             # 数据库初始化脚本
├── backend/                 # 后端 Spring Boot 项目
│   ├── pom.xml              # Maven 配置
│   └── src/main/
│       ├── java/com/subway/lostfound/
│       │   ├── LostFoundApplication.java        # 启动类
│       │   ├── config/                          # 配置类
│       │   │   ├── WebSecurityConfig.java      # 安全配置
│       │   │   └── CorsConfig.java             # 跨域配置
│       │   ├── controller/                      # 控制器
│       │   │   ├── AuthController.java         # 认证控制器
│       │   │   ├── PublicController.java       # 公开接口
│       │   │   ├── LostFoundItemController.java # 失物招领控制器
│       │   │   ├── ClaimController.java        # 认领控制器
│       │   │   ├── AdminController.java        # 管理员控制器
│       │   │   └── StatisticsController.java   # 统计控制器
│       │   ├── dto/                             # 数据传输对象
│       │   ├── entity/                          # 实体类
│       │   │   └── enums/                      # 枚举类型
│       │   ├── repository/                      # 数据访问层
│       │   ├── security/                        # 安全相关
│       │   │   ├── JwtTokenUtil.java           # JWT 工具类
│       │   │   ├── JwtRequestFilter.java       # JWT 过滤器
│       │   │   └── JwtUserDetailsService.java  # 用户详情服务
│       │   └── service/                         # 业务逻辑层
│       └── resources/
│           └── application.properties           # 应用配置
└── frontend/                # 前端 Angular 项目
    ├── package.json
    ├── angular.json
    └── src/
        ├── app/
        │   ├── app.module.ts
        │   ├── app-routing.module.ts
        │   ├── components/                  # 组件
        │   │   ├── home/                    # 首页
        │   │   ├── login/                   # 登录页
        │   │   ├── register/                # 注册页
        │   │   ├── item-list/               # 失物招领列表
        │   │   ├── item-detail/             # 物品详情
        │   │   ├── item-create/             # 发布信息
        │   │   ├── my-items/                # 我的发布
        │   │   ├── my-claims/               # 我的认领
        │   │   ├── admin-items/             # 管理员信息管理
        │   │   ├── admin-claims/            # 管理员认领管理
        │   │   └── statistics/              # 统计分析
        │   ├── models/                       # 数据模型
        │   └── services/                     # 服务
        │       ├── auth.service.ts           # 认证服务
        │       ├── auth.guard.ts             # 路由守卫
        │       ├── auth.interceptor.ts       # 认证拦截器
        │       ├── item.service.ts           # 失物招领服务
        │       ├── claim.service.ts          # 认领服务
        │       └── statistics.service.ts     # 统计服务
        └── styles.css                        # 全局样式
```

## API 接口说明

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 公开接口
- `GET /api/public/items` - 获取已审核通过的失物招领列表
- `GET /api/public/items/{id}` - 获取失物招领详情
- `GET /api/public/subway-lines` - 获取地铁线路列表
- `GET /api/public/item-types` - 获取物品类型列表

### 受保护接口（需登录）
- `POST /api/items` - 发布失物招领信息
- `GET /api/items/my` - 获取我的发布列表
- `DELETE /api/items/{id}` - 删除我的发布
- `POST /api/claims` - 提交认领申请
- `GET /api/claims/my` - 获取我的认领列表

### 管理员接口（需管理员权限）
- `GET /api/admin/items` - 获取所有失物招领列表（含筛选）
- `PUT /api/admin/items/{id}/approve` - 审核通过
- `PUT /api/admin/items/{id}/reject` - 审核拒绝
- `PUT /api/admin/items/{id}/returned` - 标记已归还
- `DELETE /api/admin/items/{id}` - 删除信息
- `GET /api/admin/claims` - 获取所有认领列表
- `PUT /api/admin/claims/{id}/approve` - 通过认领申请
- `PUT /api/admin/claims/{id}/reject` - 拒绝认领申请
- `GET /api/admin/statistics` - 获取统计数据
- `GET /api/admin/statistics/export/items` - 导出记录
- `GET /api/admin/statistics/export/statistics` - 导出统计

## 安全说明

1. **密码加密**: 使用 BCrypt 算法加密存储用户密码
2. **JWT 认证**: 所有受保护接口需携带有效的 JWT Token
3. **角色权限**: 基于角色的访问控制（ROLE_PASSENGER, ROLE_ADMIN）
4. **跨域配置**: 前端默认允许 `http://localhost:4200` 访问，可在 `application.properties` 中修改

## 常见问题

1. **数据库连接失败**
   - 检查 MySQL 服务是否启动
   - 确认 `application.properties` 中的用户名和密码正确
   - 确认已执行 `init.sql` 初始化数据库

2. **前端无法访问后端接口**
   - 确认后端服务已启动（端口 8080）
   - 检查浏览器控制台是否有跨域错误
   - 确认 `cors.allowed-origins` 配置正确

3. **JWT Token 过期**
   - Token 默认有效期为 24 小时
   - 过期后需重新登录获取新 Token

4. **管理员权限不足**
   - 确认使用的是 admin 账号登录
   - 检查用户角色是否为 ADMIN

## 技术支持

如有问题，请检查：
1. 环境版本是否符合要求
2. 配置文件是否正确
3. 数据库是否已初始化
4. 依赖是否已正确安装

## 许可证

本项目仅供学习和参考使用。
