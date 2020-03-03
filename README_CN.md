![Blockbuster](http://i.imgur.com/nqDKg1R.png)

[Planet Minecraft 页面](http://www.planetminecraft.com/mod/blockbuster-machinima-mod/) – [CurseForge 页面](https://minecraft.curseforge.com/projects/blockbuster) – [源代码](https://github.com/mchorse/blockbuster) – [AdFly 支持链接](http://adf.ly/15268913/blockbuster-curseforge) – [百科](https://github.com/mchorse/blockbuster/wiki) – [中文百科](https://github.com/ycwei982/blockbuster/wiki) – 
[EchebKeso](https://twitter.com/EchebKeso) – [Mocap 动作捕捉 mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000) – [Mocap 的源代码](https://github.com/EchebKeso/Mocap) – [中文用户QQ群](https://jq.qq.com/?_wv=1027&k=584nNVF)

Blockbuster 是一个可以帮助你在 Minecraft 中使用 NPC 演员和摄像机来制作短片/角色扮演的 mod。除了基本的录制和回放，它还提供很多各式的功能可以为你的视频添加一些原创性，超越纯演员演戏的可能性（查看 **特性** 部分）。

本 mod 支持 Forge 版本的 Minecraft 1.10.2，1.11.2 和 1.12.2（要求 Forge 版本 **14.23.4.2638** 或更高），不支持网易我的世界中国版。

Blockbuster mod 依赖于 [Metamorph](https://minecraft.curseforge.com/projects/metamorph) 和 [McLib](https://minecraft.curseforge.com/projects/mchorses-mclib)。每次更新日志会写上支持的 Metamorph 版本。Blockbuster 也可以 **自愿地** 选择与 [Aperture](https://minecraft.curseforge.com/projects/aperture) mod 搭配使用。

录制动作基于 [EchebKeso](https://twitter.com/EchebKeso) 的 Mocap 录制代码，并且重构了代码。

![MachinimaHub](https://i.imgur.com/jrK0WA2.png)

如果你对 Minecraft 的短片、角色扮演或是动画（创作、观赏、配音等等），[欢迎加入](https://discord.gg/4YFUmJp) MachinimaHub. **MachinimaHub** 是一个在 Discord 服务器上关于短片的社区（以及其它类型的故事讲述视频，例如第一人称角色扮演和动画）。

除了这些，MachinimaHub 内还有很多 Blockbuster 的用户，所以如果你需要帮助，请尽管在里面提出来！

## 疑问解答

**问：模型方块拿在手上的时候不可见。怎么修复呢？（1.12.2）**  
答：看样子你在使用 OptiFine D1 或以下版本。请升级到 OptiFine D2 或更高。

## 特性

Blockbuster mod 提供许多很酷的功能让你创作 Minecraft 短片/角色扮演：

* **演员和玩家动作录制** – 最重要的部分，创作短片的根本。使用 Blockbuster 你可以录制你自己的行动并以演员回放。*录制部分的代码有些限制能录制的动作*。前往 [Blockbuster 百科](https://github.com/mchorse/blockbuster/wiki/Home) 了解限制的范围。
* **导演方块** – 一个演员不是春，大量演员春满园。Blockbuster mod 提供一个装置叫做 *导演方块* 来控制已激活的演员。它也能被红石控制，使得链接到一些自定义的命令，红石电路的开始结束，或者是场景的结束更为方便。
* **模型方块** – 除了演员，Blockbuster 也加了 *模型方块*，可以用于以静态方式放置自定义模型，创建出色的沉浸式场景。不仅如此，你也可以把那些模型或者生物拿在手上（这个功能仅在 1.12.2 版本可用）。
* **场景** – 场景如同导演方块，但更酷。它们保存在分离的文件中（在地图的 `blockbuster/scenes/` 文件夹），而不在世界中保存。拥有了场景，你可以创建模板、转移到另一个世界、最重要的是，你永远不会在世界中丢失它们。
* **BB 枪** – 一个用于右键射出投掷物的特殊物品。这个功能提供了各种各样的配置选项，不仅可以用来创建射击场景，还可以用于雪崩，假的物理，人群爆炸等等。
* **[Aperture](https://minecraft.curseforge.com/projects/aperture) 摄像机支持** – 摄像机是你表达短片的方式。Blockbuster 兼容了Aperture mod。当你加载了 Aperture，Blockbuster mod 能提供给你一些功能，比如：将摄像机配置文件连接到播放按钮，在摄像机 GUI 编辑器里边编辑导演方块中的玩家录制动作边预览。
* **自定义模型** – 仅录制一些普通的玩意不怎么有趣。Blockbuster mod 支持在游戏内编辑模型。用自定义的模型释放出你异想天开的能力吧！
* **OBJ 模型** – 除了可以在游戏内模型编辑器创建的自定义模型，现在 Blockbuster 还支持比 JSON 模型更高一等的 OBJ 模型！
* **动物支持** – 感谢 [Metamorph](https://minecraft.curseforge.com/projects/metamorph) 的公开 API，Blockbuster 支持 Metamorph 所提供的任何活体模型作为 Blockbuster 的演员，也包括 Blockbuster 和 [Emoticons](https://minecraft.curseforge.com/projects/emoticons) 所提供的模型。
* **高级伪装** – 除了在伪装系统中激活自定义模型，Blockbuster 也添加了了特殊的模型，用于高级短片创作：
    * **图像伪装** – 用于显示图像的伪装（在平面上）。
    * **序列伪装** – 用于在给予的一堆伪装中自动轮换，依赖于在伪装之间设定的延时。
    * **录制伪装** – 用于在伪装中播放玩家录制（在模型方块和身体部分系统很实用）。
    * **结构伪装** – 用于伪装成使用结构方块后的建筑。
    * **粒子伪装** – 用于释放原版和伪装模型粒子的伪装。
* **绿幕功能** – Blockbuster 添加了发光的绿色方块，没有任何阴影；还有绿色的天空，使得搭建绿幕拍摄场景和后期处理你的短片更容易。
* **动态 GIF 支持** – 现在你可以在 Blockbuster 中加入 GIF 格式的梗和表情包了。一切要感谢 [MrCrayfish 的家具 Mod](https://github.com/MrCrayfish/MrCrayfishFurnitureMod) 和 [DhyanB](https://github.com/DhyanB/Open-Imaging/blob/master/src/main/java/at/dhyan/open_imaging/GifDecoder.java)。
* **[Minema](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2790594-minema-unofficial-the-smooth-movie-recorder) 兼容性** – 感谢基于帧和录制的 mod，你可以通过一个按钮，让游戏内短片导出成视频文件。
* **多语言支持** – 除了英语，Blockbuster 也翻译成了俄语和中文，分别感谢 [Andruxioid](https://www.youtube.com/channel/UCnHOceBjwMyqCR5oYOoNqhQ) 和 [ycwei982](https://www.youtube.com/channel/UCfUDMSGlXUblXimkvNl_7Ww)。

## 安装

安装 [Minecraft Forge](http://files.minecraftforge.net/)，下载本 mod 支持的 Minecraft 版本的最新稳定版。并安装以下 mod：[McLib](https://minecraft.curseforge.com/projects/mchorses-mclib) 和 [Metamorph](https://minecraft.curseforge.com/projects/metamorph)。拷贝到 Minecraft 的 `mods` 文件夹，启动游戏。

之后，Blockbuster mod 应该安装了，显示在 Minecraft 的 mods 菜单。如果没有显示在菜单里，那么你可能错误地进行操作了。

## 视频

### 教程视频

这个播放列表是更新日志。几乎每次更新（除了纯 bug 修复和很少功能增加）都会附有一个更新视频，用来展示新功能，并且稍微展示如何使用。

<a href="https://youtu.be/4n5p83KAG4k?list=PL6UPd2Tj65nEwg2bfY-NduLihPy6fgnvK"><img src="https://img.youtube.com/vi/4n5p83KAG4k/0.jpg"></a>

还有个教程是直接关联到 Blockbuster/Minecraft 的短片制作的。确保看看它，因为它包含着基本的 Blockbuster mod 教程，如何导入 OBJ 模型，等等。

<a href="https://youtu.be/vo8fquY-TUM?list=PLLnllO8nnzE-LIHZiaq0-ZAZiDO82K1I9"><img src="https://img.youtube.com/vi/vo8fquY-TUM/0.jpg"></a>

也可前往 [McHorse's Mods 哔哩哔哩中文频道](https://space.bilibili.com/472615413) 观看。

### Blockbuster 短片

这个播放列表展示了我测试 Blockbuster 时制作的一系列自我感觉良好的短片。欢迎来收看我做的脑洞大开的视频。

<a href="https://youtu.be/0h0KeuHaXM4?list=PL6UPd2Tj65nFdhjzY-z6yCJuPaEanB2BF"><img src="https://img.youtube.com/vi/0h0KeuHaXM4/0.jpg"></a>

这里还有四百多个在社区中，使用 Blockbuster mod 制作的视频：

<a href="https://youtu.be/mSvKmB25kPQ?list=PL6UPd2Tj65nEE8kLKBxYYZLAjruJkO0r_"><img src="https://img.youtube.com/vi/mSvKmB25kPQ/0.jpg"></a>

如果你对此项目感兴趣，你可以关注我的社交媒体账号：

[![YouTube](http://i.imgur.com/yA4qam9.png)](https://www.youtube.com/channel/UCWVDjAcecHHa8UrEWMRGI8w) [![Discord](http://i.imgur.com/gI6JEpJ.png)](https://discord.gg/qfxrqUF) [![Twitter](http://i.imgur.com/6b8vHcX.png)](https://twitter.com/McHorsy) [![GitHub](http://i.imgur.com/DmTn1f1.png)](https://github.com/mchorse)

另外，如果你在 Patreon 上支持我的话，我会很感激的！

[![成为赞助者](https://i.imgur.com/4pQZ2xW.png)](https://www.patreon.com/McHorse)

## Bug 反馈

当你发现了一个 Bug，或者 mod 导致游戏崩溃，我希望你可以报告 bug 或崩溃在 [issue tracker](https://github.com/mchorse/blockbuster/issues/)，或私信在 [Twitter](https://twitter.com/McHorsy)。也请麻烦在 [pastebin](http://pastebin.com) 贴一份日志文件，并描述下发生的事情，及重现 bug 或崩溃的方法。谢谢！

如果你没有英文交流能力，你也可以选择在 [中文用户QQ群(328380393)](https://jq.qq.com/?_wv=1027&k=584nNVF) 反馈你的 Bug 或崩溃，Mod 的翻译者将会协助你。

## 许可证

Blockbuster mod 的代码是 MIT 许可证, 查看 [LICENSE.md](./LICENSE.md) 文件了解许可证内容。

## 开发者请看

语言文件是由 YML（在 `help\` 文件夹）编译成 INI 格式的。

如果你想翻译本 Mod，你得先修改合适的 YML 文件。然后使用 `./gradlew buildLangFiles` 命令来将 YML 转换成 INI 的 `.lang` 文件，到 `src/main/resources/assets/blockbuster/lang` 文件夹。转换完语言文件后，别忘了刷新你的 IDE。

另外你也需要 [Aperture](https://github.com/mchorse/aperture)， [McLib](https://github.com/mchorse/mclib) 和 [Metamorph](https://github.com/mchorse/metamorph) 的开发版构建。构建它们，然后把 `-sources.jar` 和 `-dev.jar` 放在 `run/libs/` 文件夹，之后刷新 IDE。
