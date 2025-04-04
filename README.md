# Hizone
## 需求文档
1. 注册/登录
2. 用户信息编辑
3. 关注/取消关注/删除粉丝
4. 发布/修改/删除内容
5. 内容点赞/评论/评论点赞/删除评论/回复评论/内容收藏
6. 全文搜索
## 业务文档
### 注册/登录业务
用户提交邮箱，后端给邮箱发送验证码，用户提交验证码，后端验证验证码，验证成功后，
若邮箱未注册，则创建新用户，最后返回token

### 用户信息编辑业务
用户提交修改后的信息，后端验证后更新并返回
### 帖子收藏业务
用户提交收藏帖子信息，交互服务更新数据，并调用用户服务更新用户收藏数
## 接口文档
1. 登录注册服务
   1. 注册
      - 参数：用户名、密码、手机号、邮箱
      - 返回：uid、token
   2. 账号密码登录
      - 参数：用户名、密码
      - 返回：uid、token
   3. 找回密码
      - 参数：手机号、邮箱
      - 返回：token
   4. 第三方登录
      - 参数：第三方token
      - 返回：uid、token
2. 用户管理服务
   1. 获取用户信息
      - 参数：uid
      - 返回：用户基本信息
   2. 获取用户自身信息
      - 参数：uid、token
      - 返回：用户自身信息
   3. 修改用户信息
      - 参数：uid、token、修改后的用户信息
      - 返回：提示消息
   4. 修改头像
      - 参数：uid、token、头像文件
      - 返回：提示消息
3. 用户关系服务
   1. 关注用户
      - 参数：uid、token、被关注的用户uid
      - 返回：提示消息
   2. 取消关注用户
      - 参数：uid、token、被取消关注的用户uid
      - 返回：提示消息
   3. 删除粉丝
      - 参数：uid、token、被删除的粉丝uid
      - 返回：提示消息
   4. 获取关注列表
      - 参数：uid、（token）
      - 返回：关注列表、（自己的关注列表）
   5. 获取粉丝列表
      - 参数：uid、（token）
      - 返回：粉丝列表、（自己的粉丝列表）
   6. 私信用户
      - 参数：uid、token、被私信的用户uid、私信内容
      - 返回：提示消息
4. 内容管理服务
   1. 发布内容
      - 参数：uid、token、内容
      - 返回：内容id
   2. 修改内容
      - 参数：uid、token、内容id、修改后的内容
      - 返回：提示消息
   3. 删除内容
      - 参数：uid、token、内容id
      - 返回：提示消息
   4. 获取内容
      - 参数：内容id
      - 返回：内容详情
   5. 获取自己发布的内容列表
      - 参数：uid、token
      - 返回：内容列表
   6. 获取推送内容
      - 参数：uid、token
      - 返回：内容列表
5. 内容交互服务
   1. 内容点赞
      - 参数：uid、token、内容id
      - 返回：提示消息
   2. 内容评论
      - 参数：uid、token、内容id、评论内容
      - 返回：评论id
   3. 评论点赞
      - 参数：uid、token、评论id
      - 返回：提示消息
   4. 删除评论
      - 参数：uid、token、评论id
      - 返回：提示消息
   5. 回复评论
      - 参数：uid、token、内容id、评论id、回复内容
      - 返回：评论id
   6. 内容转发
      - 参数：uid、token、内容id
      - 返回：内容id
   7. 内容收藏
      - 参数：uid、token、内容id
      - 返回：收藏id
   8. 获取点赞数
      - 参数：内容id
      - 返回：点赞数
   9. 获取评论数
      - 参数：内容id
      - 返回：评论数
   10. 获取转发数
      - 参数：内容id
      - 返回：转发数
   11. 获取收藏数
      - 参数：内容id
      - 返回：收藏数
   12. 获取评论列表
      - 参数：内容id
      - 返回：评论列表