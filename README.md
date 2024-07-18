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
