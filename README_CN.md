![Blockbuster](https://i.imgur.com/fkRVMIw.png)

[Planet Minecraft](http://www.planetminecraft.com/mod/blockbuster-machinima-mod/) – [CurseForge](https://www.curseforge.com/minecraft/mc-mods/blockbuster) – [GitHub](https://github.com/mchorse/blockbuster) – [百科](https://github.com/mchorse/blockbuster/wiki) – [中文百科](https://github.com/ycwei982/blockbuster/wiki) – 
[EchebKeso](https://twitter.com/EchebKeso) – [Mocap mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000) – [Mocap 源代码](https://github.com/EchebKeso/Mocap) – [中文用户交流群](https://jq.qq.com/?_wv=1027&k=584nNVF)

**Blockbuster** 是一个可以帮助您在 Minecraft 中使用 NPC 演员来制作短片/角色扮演的 mod。除了基础的录制和回放，它还提供了众多杂项功能，可以帮助您为您的视频增添许多原创性，进而超越传统演员表演的可能性（查看 **特性** 部分）。

本 mod 需要与 Forge **14.23.4.2638**（或更高版本）配合使用，适用于 Minecraft 1.12.2（旧版本适用于 1.10.2 和 1.11.2）。不支持网易我的世界中国版。

录制与动作回放功能基于 [EchebKeso](https://twitter.com/EchebKeso) 的 Mocap 进行重构。

![MachinimaHub](https://i.imgur.com/jrK0WA2.png)

如果您对 Minecraft 的短片、角色扮演或是动画（创作、观赏、配音等等），[欢迎加入](https://discord.gg/4YFUmJp) MachinimaHub。**‎‎MachinimaHub**‎ ‎是一个由社区运营的关于 Machinima（类似于其他形式的讲故事视频，如第一人称角色扮演和动画）的 Discord 服务器。‎

除此之外，MachinimaHub 中还有许多 Blockbuster 的用户，所以如果您需要帮助，欢迎来随时来服务器里提问！

## 安装

安装 [Minecraft Forge](http://files.minecraftforge.net/)，下载对应 Minecraft 版本的最新稳定版 jar 文件。同时，需要安装以下mod：[McLib](https://www.curseforge.com/minecraft/mc-mods/mchorses-mclib)，和 [Metamorph](https://www.curseforge.com/minecraft/mc-mods/metamorph)。将其放在 Minecraft 的`mods`文件夹中，并启动游戏。

启动后，Blockbuster mod 应该会安装成功，并会出现在 Minecraft 的 Mods 菜单中。如果Blockbuster 没有出现在 Mods 菜单中，那就说明出问题了。

## FAQ (疑问解答)

**Q：模型方块拿在手上的时候不可见。如何修复？(1.12.2)**  
A：看样子您在使用 OptiFine D1 或以下版本。请升级到 OptiFine D2 或更高版本。

## 特性

Blockbuster mod 提供许多很酷的功能让您创作 Minecraft 短片/角色扮演：

* **演员和玩家动作录制** – 最重要的功能，创作短片不可或缺的部分。使用 Blockbuster，您可以录制您自己的行动并通过演员回放。*录制部分的代码会对能被录制的动作产生一些限制*。前往 [Blockbuster 百科](https://github.com/mchorse/blockbuster/wiki/Home) 了解限制的范围。
* **导演方块** – 一个演员不是春，一群演员春满园。通过导演方块，您可以一次录制一个演员，同时对之前录制的演员做出反应，这样便可以独自创建复杂的场景。
* **场景** – 场景如同导演方块，但更强大。它们被保存在单独的文件夹中（在地图的 `blockbuster/scenes/` 文件夹），而非保存在世界中。使用场景，您可以创建模板，转移到另一个世界，最重要的是，您永远不会在世界中丢失它们。
* **模型方块** – 除了演员，Blockbuster 还加入了 *模型方块*，可以用于以静态方式放置自定义模型或生物，从而搭建出色和身临其境的场景。除此之外，您也可以将那些模型或者生物拿在手上（该功能仅在 1.12.2 版本可用）。
* **BB 枪** – 一个用于右键射出投掷物的特殊物品。该功能提供了各种各样的配置选项，不仅可以用来创建火拼场景，还可以用于雪崩，假物理，人群爆炸等场景！
* **[Aperture](https://www.curseforge.com/minecraft/mc-mods/aperture) 相机支持** – Blockbuster 内置了 Aperture 集成功能。当 Aperture 加载后，Blockbuster mod 会提供一些功能，例如：可以通过绑定播放按钮来预览相机配置文件，可以在编辑相机的同时预览导演方块的回放，还可以在相机编辑器中编辑玩家回放动作。
* **自定义模型** – 只录制那些普通的玩意儿真是太屈才了。Blockbuster mod 能让我们使用游戏内的模型编辑器来创建自定义模型。尽情释放您的脑洞吧！
* **OBJ 和 MagicaVoxel 模型** – 除了可以在游戏中的自定义模型编辑器中创建自定义模型外，Blockbuster 还支持比自定义 JSON 模型更强大的 OBJ 模型和 MagicaVoxel 模型！
* **伪装支持** – 感谢 [Metamorph](https://www.curseforge.com/minecraft/mc-mods/metamorph) 的伪装 API，Blockbuster 支持 Metamorph 所提供的任何伪装模型作为 Blockbuster 的演员，这也包括 Blockbuster 和 [Emoticons](https://www.curseforge.com/minecraft/mc-mods/emoticons) 所提供的模型。
* **高级伪装** – 除了在伪装系统中激活自定义模型，Blockbuster 也添加了了特殊的模型，用于高级短片创作：
    * **图像伪装** – 用于显示图像的伪装（在平面上）。
    * **序列伪装** – 用于在给定伪装之间自动切换的伪装，取决于多个伪装之间设置的延迟时间。
    * **录制伪装** – 用于在伪装中播放玩家回放（在模型方块和身体部分系统很实用）。
    * **结构伪装** – 用于伪装成结构方块保存的结构的伪装。
    * **粒子伪装** – 用于释放原版和伪装模型粒子的伪装。
    * **暴风雪伪装** (由 [Spark Squared](https://spark-squared.com/) 赞助) – 用于创建自定义粒子效果的伪装。
* **绿幕功能** – Blockbuster 加入了没有任何阴影的发光纯绿色方块，以及将天空完全变为绿色的选项，从而能够更地创建用于后期处理的绿幕。此外，Blockbuster 的键控选项还能够创建透明的截图和视频，参见[本教程](https://youtu.be/OY_USRJofT0)。
* **动态 GIF 支持** – 现在您可以在 Blockbuster 中加入 GIF 格式的梗和表情包了。一切要感谢 [MrCrayfish's furniture Mod](https://github.com/MrCrayfish/MrCrayfishFurnitureMod) 和 [DhyanB](https://github.com/DhyanB/Open-Imaging/blob/master/src/main/java/at/dhyan/open_imaging/GifDecoder.java)。
* **动画** — 通过动画姿势和伪装动画，您可以为您的演员和场景添加动画。
* **[Minema](https://github.com/daipenger/minema/releases) 兼容性** – 感谢基于帧的玩家回放和相机，您只需通过一个按钮，即可让游戏内的短片导出为视频文件。
* **多语言支持** – 除了英语，Blockbuster 也翻译成了俄语和中文，分别感谢 [Andruxioid](https://www.youtube.com/channel/UCnHOceBjwMyqCR5oYOoNqhQ)、[ycwei982](https://www.youtube.com/channel/UCfUDMSGlXUblXimkvNl_7Ww) 以及 [Chunk7](https://twitter.com/RuaC7w)。

## 视频

### 教程视频

这是更新日志的播放列表。几乎每次更新（除了纯 Bug 修复和很少功能增加）都会附有一个更新日志视频，用来展示新功能，并且对使用方法进行简要地介绍。

<a href="https://youtu.be/JghXifbHi-k?list=PL6UPd2Tj65nEwg2bfY-NduLihPy6fgnvK"><img src="https://img.youtube.com/vi/JghXifbHi-k/0.jpg"></a>

此外，还有一个直接或间接与 Blockbuster/Minecraft 短篇创作相关的教程的播放列表。一定要去看看，因为它包含了一些诸如 Blockbuster mod 的基础知识，如何导入 OBJ 模型等的教程。

<a href="https://youtu.be/vo8fquY-TUM?list=PLLnllO8nnzE-LIHZiaq0-ZAZiDO82K1I9"><img src="https://img.youtube.com/vi/vo8fquY-TUM/0.jpg"></a>

您也可前往 [McHorse's Mods 哔哩哔哩中文频道](https://space.bilibili.com/472615413) 观看。

### Blockbuster 短片

这个播放列表展示了我测试 Blockbuster 时制作的一系列我觉得还过意的去的短片。欢迎来收看我做的脑洞大开的视频。

<a href="https://youtu.be/eig13klr-kw?list=PL6UPd2Tj65nFdhjzY-z6yCJuPaEanB2BF"><img src="https://img.youtube.com/vi/eig13klr-kw/0.jpg"></a>

这里还有四百多个使用 Blockbuster mod 并由社区制作的视频：

<a href="https://youtu.be/9hCMFoNnfxw?list=PL6UPd2Tj65nEE8kLKBxYYZLAjruJkO0r_"><img src="https://img.youtube.com/vi/9hCMFoNnfxw/0.jpg"></a>

如果您对此项目感兴趣，您可以关注我的社交媒体账号：

[![YouTube](http://i.imgur.com/yA4qam9.png)](https://www.youtube.com/channel/UCWVDjAcecHHa8UrEWMRGI8w) [![Discord](http://i.imgur.com/gI6JEpJ.png)](https://discord.gg/qfxrqUF) [![Twitter](http://i.imgur.com/6b8vHcX.png)](https://twitter.com/McHorsy) [![GitHub](http://i.imgur.com/DmTn1f1.png)](https://github.com/mchorse)

另外，如果您在 Patreon 上支持我的话，我会很感激的！

[![成为赞助者](https://i.imgur.com/4pQZ2xW.png)](https://www.patreon.com/McHorse)

## Bug 反馈

如果您发现了一个 Bug，或因本 mod 导致游戏崩溃，我希望您可以将 Bug 或崩溃提交至 [issue tracker](https://github.com/mchorse/blockbuster/issues/)，或在 [Twitter](https://twitter.com/McHorsy) 私信。也请麻烦在 [pastebin](http://pastebin.com) 贴一份日志文件，并描述下 Bug 与崩溃的内容，及重现 Bug 或崩溃的方法。感谢！

如果您没有英文交流能力，您也可以选择在 [中文用户QQ群(328380393)](https://jq.qq.com/?_wv=1027&k=584nNVF) 反馈您的 Bug 或崩溃，Mod 的用户们将会互相协助。

## 许可证

Blockbuster mod 的代码根据 MIT 许可协议授权, 详情请参阅 [LICENSE.md](./LICENSE.md) 文件。

## 开发者请看

语言文件是由 YML（在 `help\` 文件夹）编译成 INI 格式的。

如果您想翻译本 Mod，您得先修改合适的 YML 文件。然后使用 `./gradlew buildLangFiles` 命令来将 YML 转换成 INI 的 `.lang` 文件，到 `src/main/resources/assets/blockbuster/lang` 文件夹。转换完语言文件后，别忘了刷新您的 IDE。

另外您也需要 [Aperture](https://github.com/mchorse/aperture)， [McLib](https://github.com/mchorse/mclib) 和 [Metamorph](https://github.com/mchorse/metamorph) 的开发版构建。构建它们，然后把 `-sources.jar` 和 `-dev.jar` 放在 `run/libs/` 文件夹，之后刷新 IDE。