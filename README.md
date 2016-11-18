![Blockbuster](./logo.png) 

[Downloads](https://github.com/mchorse/blockbuster/releases) – [PMC post](http://www.planetminecraft.com/mod/blockbuster-machinima-mod/) – [Minecraft Forum post](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2700216-blockbuster-create-simple-machinimas-and-adventure) – [McME](https://github.com/mchorse/mcme) (model editor)  
[EchebKeso](https://twitter.com/EchebKeso) – [Mocap mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000) – [Mocap's source code](https://github.com/EchebKeso/Mocap)

Blockbuster (**pun intended**) is a Minecraft mod which lets you create simple 
Minecraft machinimas in single player, without having to recruit and organize a 
crowd of body actors and cameras.

Blockbuster mod is built on top of Forge 12.18.1.2073 for Minecraft 1.10.2. Blockbuster mod is also provides build for 1.9.4, if it's possible, without changing the code.

Recording code is based and refined on recording code from Mocap mod by [EchebKeso](https://twitter.com/EchebKeso).

## Features

Blockbuster mod provides you with lots of features to make cool machinimas:

* **Actors and player recording** – the most important feature, as you won't be able to create a machinimas without it. With Blockbuster mod you can record yourself doing things and then playback it using an actor. *Recording code has some limitations on which actions it can record*. 
* **Director blocks** – one actor is good, a crowd of them is better. Blockbuster mod provides you with a mechanism called *director block* which manages playback of registered actors. It has also outlets for redstone contraptions, making it easy attaching some custom commands or redstone logic on start or the end of the scene playback.
* **Camera support** – camera is how you present your machinima. Blockbuster mod has a support for different camera features starting from simple idle camera to complex camera moves like paths (with FOV animation), following and looking at the actor.
* **Custom models** – recording only player-like entities isn't fun. Blockbuster mod has support for custom models created in McME model editor (link above). Unleash your creativity with custom actor models!
* **[Minema](https://github.com/daipenger/minema) compatability** – thanks to frame-based player recording and cameras, you can convert your in-game machinimas to high-quality video output.

## Install

Install [Minecraft Forge](http://files.minecraftforge.net/), then go to 
[releases](https://github.com/mchorse/blockbuster/releases) and download the 
latest stable version (not a pre-release) of jar file. Put it in minecraft's `mods` folder, and launch the game. 

After that, Blockbuster mod should be installed and will appear in Minecraft's 
mods menu. If Blockbuster didn't appear in the mods menu, then something went 
wrong.

## Videos

### Tutorial video

Tutorial videos for 1.3. This playlist shows how to use the mod features. Every update comes with a change log video which show out new features.

<a href="https://youtube.com/playlist?list=PL6UPd2Tj65nGxteZIdEE_fIga7_HoZJ9w">
    <img src="https://img.youtube.com/vi/WXrBEQZrQ7Q/0.jpg">
</a>

### Machinima Examples

This playlist consists out of videos that I've created during Blockbuster mod testings. Those videos are my lab experiments. Don't judge the quality of these machinimas yet, I'm still learning.

<a href="https://youtu.be/NMtTmRoLKJQ?list=PL6UPd2Tj65nFdhjzY-z6yCJuPaEanB2BF">
    <img src="https://img.youtube.com/vi/NMtTmRoLKJQ/0.jpg">
</a>

## License and Manual

Blockbuster mod's code is licensed under MIT, see file [LICENSE.md](./LICENSE.md) for more information about the license.

Manual is located in repository's [wiki](https://github.com/mchorse/blockbuster/wiki). 

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