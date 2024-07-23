# chainTogether

> 关卡可能按照戈德堡机的方式设置。

## 更新日志

### 7.16日

- 引入box2d
- 导入之前的地图
- 完成了碰撞箱生成器的方法

### 7.17日

- 完成Player类的构造器和渲染
- 完成新地图的地形
- !!未解决旋转贴图问题
- 完成开题报告

### 7.18日

- 一早起靠前辈指点的Sprite解决了旋转问题，方法如下

```java
// 这方法无敌了吧？发现了能不用也是神人了？你们有这样的方法吗
// 所以到底是怎么做到的？是因为缩放带来的误差吗？兄弟没道理的
// 它这个明显是在最开始就设定好中心，感觉像是给Texture坐标问题打的一个补丁？
sprite.setOriginBasedPosition(roleBody.getPosition().x,roleBody.getPosition().y);
```
- 整合了一下Player类和SmoothCamera类(extends OrthographicCamera)
- 向network进发！
- 随便写了个网络类，还没嵌入游戏中，需要调用和更新渲染

### 7.19日报

- 本日完成
  1. LoginScreen.java类，实现了输入账户密码后开始游戏；
  2. 连接了服务器数据库，实现通过MySQL存储用户名和密码，并进行用户名和密码的检验。
- 重点解决的问题/收获
  1. 弄清楚了屏幕转换的层级关系，利用setScreen(new LoginScreen(this)); super.render();等语句像父类和接口的抽象方法传参，而执行子类重写的方法和实现类实现的方法。
  2. 创建了表格布局和Textfiled、TextButton等字段，构建了登陆页面，理解了UI布局类和方法的使用。
  3. 通过监听器实现向数据库写入数据和验证返回值，明白了MySQL的基本使用原理。
- 明日计划
  - 进行联机测试，实现多人依次登录上线，并在等待房间（测试）中操纵角色移动，当三个人全部上线后，通过按键开始游戏。

### 7.20日报

connect sql 0.2