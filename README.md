# SweetDrops

挖掘方块自定义掉落物插件

## 简介

设定在哪些世界挖掘哪种方块，会随机掉落某些物品，甚至可以限制使用的工具类型、附魔种类，要求权限等等。

还可以设定时运附魔是否会对掉落数量产生影响，等等。

你可以取消或不取消方块原本的掉落物，然后让方块掉落你指定的原版物品、MythicMobs 物品，甚至是在配置中详细定义的预制体。

## 命令

根命令为 `/sweetdrops`，别名 `/sdrops`, `/sd`。  
`<>`包裹的是必选参数，`[]`包裹的是可选参数，  
所有命令均只能由管理员或控制台执行

| 命令                           | 描述              |
|------------------------------|-----------------|
| `/sd debug`                  | 开启或关闭调试模式       |
| `/sd prefeb <预制体> [数量] [玩家]` | 给予自己或某人指定数量的预制体 |
| `/sd reload`                 | 重载配置文件          |
