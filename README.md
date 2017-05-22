![Blockbuster](http://i.imgur.com/nqDKg1R.png)

[Planet Minecraft page](http://www.planetminecraft.com/mod/blockbuster-machinima-mod/) – [Minecraft Forum thread](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2700216-blockbuster-machinima-studio-mod) – [CurseForge page](https://minecraft.curseforge.com/projects/blockbuster) – [Source code](https://github.com/mchorse/blockbuster) – [AdFly support link](http://adf.ly/15268913/blockbuster-curseforge) – [Wiki](https://github.com/mchorse/blockbuster/wiki)  
[EchebKeso](https://twitter.com/EchebKeso) – [Mocap mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000) – [Mocap's source code](https://github.com/EchebKeso/Mocap)

Blockbuster is a Minecraft mod which lets you create Minecraft machinimas in singleplayer using NPC body actors and cameras. This mod works on Forge for Minecraft 1.9.4, 1.10.2 and 1.11.2.

Blockbuster mod depends upon [Metamorph](https://minecraft.curseforge.com/projects/metamorph). Every new change log will specify the version of Metamorph supported. 

Recording is based and rewritten from scratch on recording code from Mocap mod by [EchebKeso](https://twitter.com/EchebKeso).

## Features

Blockbuster mod provides you with lots of features to make cool machinimas:

* **Actors and player recording** – the most important feature, as you won't be able to create a machinimas without it. With Blockbuster mod you can record yourself doing things and then playback it using an actor. *Recording code has some limitations on which actions it can record*. See [Blockbuster Wiki](https://github.com/mchorse/blockbuster/wiki/Home) for more information about limitations.
* **Director blocks** – one actor is good, a crowd of them is even better. Blockbuster mod provides you with a mechanism called *director block* which manages playback of registered actors. It has also outlets for redstone contraptions, making it easy attaching some custom commands or redstone logic on start or the end of the scene playback.
* **Camera support** – camera is how you present your machinima. Blockbuster mod has a support for different camera features starting from simple idle camera to complex camera paths (with FOV animation), following and looking at the actor.
* **Custom models** – recording only player-like entities isn't very fun. Blockbuster mod has support for custom models created in [McME](https://mchorse.github.io/mcme/) model editor or in-game with integrated model editor. Unleash your creativity with custom models!
* **Mobs support** – thanks to [Metamorph](https://minecraft.curseforge.com/projects/metamorph)'s public API, Blockbuster is capable of using any morphs which Metamorph provides to be used with Blockbuster actors.
* **[Minema](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2790594-minema-unofficial-the-smooth-movie-recorder) compatability** – thanks to frame-based player recording and cameras, you can convert your in-game machinimas to videos with a press of a button.

## Install

Install [Minecraft Forge](http://files.minecraftforge.net/), download the latest stable version of jar file for available minecraft version. Also install following mods: [Metamorph](https://minecraft.curseforge.com/projects/metamorph). Put it in minecraft's `mods` folder, and launch the game.

After that, Blockbuster mod should be installed and will appear in Minecraft's mods menu. If Blockbuster didn't appear in the mods menu, then something went wrong. 

## Videos

### Tutorial videos

Feature videos for 1.4. This playlist shows how to use the mod features. Every update comes with a change log video which show out new features.

<a href="https://youtu.be/CoJ_6Byh6LA?list=PL6UPd2Tj65nEwg2bfY-NduLihPy6fgnvK"><img src="https://img.youtube.com/vi/CoJ_6Byh6LA/0.jpg"></a> 

There's also a video that shows how to create a simple machinima with Blockbuster mod. This video shows basics of director block, how to attach actors to director block, and how to setup cameras:

<a href="https://youtu.be/cVTIzKzWtqg?list=PL6UPd2Tj65nE0Pmf6GD2Fk3aRGWTGKlZk"><img src="https://img.youtube.com/vi/cVTIzKzWtqg/0.jpg"></a> 

### Machinima Examples

This playlist consists out of videos that I've created during Blockbuster mod testings. Those videos are my lab experiments. Don't judge the quality of these machinimas yet, I'm still learning.

<a href="https://youtu.be/FjED5qT80eM?list=PL6UPd2Tj65nFdhjzY-z6yCJuPaEanB2BF"><img src="https://img.youtube.com/vi/FjED5qT80eM/0.jpg"></a> 

## For mod reviewers and reposters

When reposting my mod on your own website or reviewing it, please consider following (if you want to support me and my mod):

* Don't distort the mod name. It's the *Blockbuster* mod.
* Make sure that information and description of my mod is legit. Misleading information, like Minecraft version support or non-existent features, is your responsibility.
* By uploading a custom build of this mod, the build becomes your responsibility.
* Provide the source link, please. [CurseForge](https://minecraft.curseforge.com/projects/blockbuster) page is preferable.
* Provide a link to my [YouTube channel](https://www.youtube.com/channel/UCWVDjAcecHHa8UrEWMRGI8w), please. This will be really appreciated! 
* You can use Blockbuster [banner](http://i.imgur.com/nqDKg1R.png) or [cover](http://i.imgur.com/XgU8Tvx.png) for your repost page. Don't apply the watermark, though, that's just rude.

If you're interested in this project, you might as well follow me on any of social media accounts listed below:

[![YouTube](http://i.imgur.com/yA4qam9.png)](https://www.youtube.com/channel/UCWVDjAcecHHa8UrEWMRGI8w) [![Discord](http://i.imgur.com/gI6JEpJ.png)](https://discord.gg/qfxrqUF) [![Twitter](http://i.imgur.com/6b8vHcX.png)](https://twitter.com/McHorsy) [![GitHub](http://i.imgur.com/DmTn1f1.png)](https://github.com/mchorse)  

## Bug reports

If you found a bug, or this mod crashed your game. I'll appreciate if you could report a bug or a crash to me either on [issue tracker](https://github.com/mchorse/blockbuster/issues/), on PM or on [Twitter](https://twitter.com/McHorsy). Please, make sure to attach a crash log ([pastebin](http://pastebin.com) please) and description of a bug or crash and the way to reproduce it. Thanks! 

## License

Blockbuster mod's code is licensed under MIT, see file [LICENSE.md](./LICENSE.md) for more information about the license.

## For Devs

Language files have to be compiled from YML to INI format using PHP. You need to have PHP 5 and [composer installed](https://getcomposer.org/download/). Once when you have PHP and composer, run:

```sh
# Go to "php" folder
cd php

# Install PHP dependencies (I assumed you installed composer in the root of repository)
../composer.phar install

# Go back
cd ..

# Or "make check"
make build_lang
```

This should compile YML files into language files. Also, you'll have to refresh Eclipse or your IDE to get the changed file get into the app. Simply open `en_US.lang` in IDE and build the project again.

Also download one of the [Metamorph](https://minecraft.curseforge.com/projects/metamorph)'s `-dev` jars (and optionally `-source`, for documentation) published in releases. Put the `-dev` mod into `run/mods` (for more information check `depenencies` block in `build.gradle`).