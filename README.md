# Ruru Custom
最近更新时间:2026-05-24

原作者已经很久没更新了 后面新出的很多东西都没加上检测

稍微补了一下

并且为了达到(几乎)一劳永逸的效果 允许用户自定义包名检测

(这质量怀疑PR过去都不会理的 干脆另开了) 

### 更改
- 添加基于零宽字符的应用检测(绝大多数Root相关应用都没有在/Android/data目录下创建文件夹 所以不少应用都查不出来 唯一用途可能就是用来查自定义的应用了)
- 添加KitsuneMask KernelSU APatch管理器及其部分分支的检测
- 添加Shizuku检测(学的Hunter)
- 支持自定义要检测的应用 点击右上角按钮进行修改后重启生效
- 支持检测MoveCertificate模块(问就是我在用)
- 尝试支持预测性返回手势

### 发牢骚专区
检测应用列表真的真的很蠢 误判率高得吓人

但奈何还是有部分软件把你是否安装了某些"违规"应用当成检测的重要标准

但奈何这算是原理最简单的检测之一 所以咯

顺便的 就算真查出来有什么问题 也没必要过于紧张

绝大多数软件不会因为这个就给你号黑掉 最多拿去做用户画像罢了

<small>极少数连USB调试和连点器都查还直接封号的SB游戏除外</small>
<br/>

Kotlin是真的难 Jetpack也是一窍不通

代码改的稀巴烂 不喜勿喷

### 第三方开源引用
##### Apache License 2.0
[vvb2060/XposedDetector](https://github.com/vvb2060/XposedDetector)

[Dr-TSNG/ApplistDetector](https://github.com/Dr-TSNG/ApplistDetector)

[mahongyin/API-Security](https://github.com/mahongyin/API-Security)

[lamster2018/EasyProtector](https://github.com/lamster2018/EasyProtector)
##### GNU General Public License v3.0
[LSPosed/LSPatch](https://github.com/LSPosed/LSPatch)
##### MIT License
[AoEiuV020/IAmNotDisabled](https://github.com/AoEiuV020/IAmNotDisabled/blob/main/LICENSE)
