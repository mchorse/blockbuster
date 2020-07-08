![Blockbuster](https://i.imgur.com/fkRVMIw.png)

[Planet Minecraft page](http://www.planetminecraft.com/mod/blockbuster-machinima-mod/) – [CurseForge page](https://www.curseforge.com/minecraft/mc-mods/blockbuster) – [GitHub](https://github.com/mchorse/blockbuster) – [Wiki](https://github.com/mchorse/blockbuster/wiki)  
[EchebKeso](https://twitter.com/EchebKeso) – [Mocap mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000) – [Mocap's source code](https://github.com/EchebKeso/Mocap)

**Blockbuster** is a Minecraft mod which helps you create Minecraft machinimas/roleplays series using NPC body actors. Besides providing basic recording and playback, it also features a ton of miscellaneous features which can help you to add some originality to your video, extending your possibilities beyond plain body acting (see **Features** section). 

This mod works with Forge **14.23.4.2638** (or above) for Minecraft 1.12.2 (past versions available for 1.10.2 and 1.11.2).

Recording and playback is based on, and rewritten from scratch, Mocap mod by [EchebKeso](https://twitter.com/EchebKeso).

![MachinimaHub](https://i.imgur.com/jrK0WA2.png)

If you're interested in any aspect of Minecraft machinimas, roleplays or animations (creation, watching, voice acting and etc.), [feel free to join](https://discord.gg/4YFUmJp) the MachinimaHub. **MachinimaHub** is a community run Discord server about machinimas (and other forms of story-telling videos such as first-person roleplays and animations). 

Beside that, MachinimaHub has lots of Blockbuster users, so if you need any help feel free to hop on the server!

## Install

Install [Minecraft Forge](http://files.minecraftforge.net/), download the latest stable version of jar file for available Minecraft version. Also install following mods: [McLib](https://www.curseforge.com/minecraft/mc-mods/mchorses-mclib), and [Metamorph](https://www.curseforge.com/minecraft/mc-mods/metamorph). Put it in minecraft's `mods` folder, and launch the game.

After that, Blockbuster mod should be installed and will appear in Minecraft's mods menu. If Blockbuster didn't appear in the mods menu, then something went wrong. 

## FAQ

**Q: Model block is invisible when I hold it in the hand. How to fix it? (1.12.2)**  
A: Looks like you're using Optifine D1 or below. Update Optifine to D2 or higher.

## Features

Blockbuster mod provides you with lots of features to create outstanding machinimas/roleplay series:

* **Actors and player recording** – the most important feature, as you won't be able to create a machinimas without it. With Blockbuster mod you can record yourself doing things and then playback it using an actor. *Recording code has some limitations on which actions it can record*. See [Blockbuster Wiki](https://github.com/mchorse/blockbuster/wiki) for more information about limitations.
* **Director blocks** – special blocks which manage playback of multiple actors. With a director block, you can record an actor at a time, meanwhile reacting to previously recorded actors, making it possible to create complex scenes solo.
* **Scenes** – scenes are just like director blocks, but cooler. They are stored in separate files (in world's `blockbuster/scenes/` folder), as opposed in the world. With scenes, you can create templates, transfer to another worlds, and most importantly you will never lose them in the world.
* **Model block** – besides actors, Blockbuster also adds a *model block*, allowing you placing down custom models or mobs as static props, which allows creating magnificient and immersive scenes. Besides placing them down, you can also hold those models or mobs in your hand.
* **BB gun item** – a special item which shoots projectiles on right click. This feature provides many different configuration options which can be used to create not only firearm combat scenes, but also special effects like avalanches, fake physics, crowd explosion and much more!
* **[Aperture](https://www.curseforge.com/minecraft/mc-mods/aperture) support** – Blockbuster mod has built-in Aperture integration. When Aperture is loaded, Blockbuster mod provides you with some features such as: ability to attach camera profiles to playback button, previewing director block playback while editing cameras and editing the player recording actions within the camera editor.
* **Custom models** – recording only player-like entities limits the originality of your machinimas. Blockbuster mod let's you create custom models using in-game model editor. Unleash your creativity with custom models!
* **OBJ and MagicaVoxel models** – beside custom models which can be created with in-game custom model editor, Blockbuster also supports OBJ models and MagicaVoxel models on top of custom JSON models!
* **Morphs support** – thanks to [Metamorph](https://www.curseforge.com/minecraft/mc-mods/metamorph)'s morph API, Blockbuster is capable of using any morphs which Metamorph provides to be used by Blockbuster actors. Including Blockbuster's provided morphs and [Emoticons](https://www.curseforge.com/minecraft/mc-mods/emoticons)' morphs.
* **Advanced morphs** – beside registering custom models into morph system, Blockbuster also provides special morphs for advanced machinima creation:
	* **Image morph** – a morph which allows to display an image (on a plane).
    * **Sequencer morph** – a morph that switches between given morphs automatically, depending on delays set between multiple morphs.
    * **Record morph** – a morph that allows to playback a player recording within the morph itself (useful with model block or body part system).
    * **Structure morph** – a morph that allows to morph into a structure saved with structure block.
    * **Particle morph** – a morph which allows to emit vanilla and morph particles.
    * **Snowstorm morph** (sponsored by [Spark Squared](https://spark-squared.com/)) — a morph which allows to create custom particle effects.
* **Green screen features** – Blockbuster adds glowing green blocks without any shading and an option to turn the sky fully green which makes it easier creating green screens for post processing of your machinimas. Beside that, Blockbuster has keying options which allow to create screenshots and videos with transparency, see [this tutorial](https://youtu.be/OY_USRJofT0).
* **Animated GIF support** – now you can put reaction and meme GIFs onto Blockbuster models and just as images. Credit goes to [MrCrayfish's furniture mod](https://github.com/MrCrayfish/MrCrayfishFurnitureMod) and [DhyanB](https://github.com/DhyanB/Open-Imaging/blob/master/src/main/java/at/dhyan/open_imaging/GifDecoder.java).
* **Animations** — with animated poses and image morph animations, you can add animation to your actors and sets.
* **[Minema](https://github.com/daipenger/minema/releases) compatability** – thanks to frame-based player recording and cameras, you can convert your in-game machinimas to videos with a press of a button.
* **Speaks multiple languages** – besides English, Blockbuster is also translated into Russian and Chinese thanks to [Andruxioid](https://www.youtube.com/channel/UCnHOceBjwMyqCR5oYOoNqhQ), [ycwei982](https://www.youtube.com/channel/UCfUDMSGlXUblXimkvNl_7Ww) and [Chunk7](https://twitter.com/RuaC7w) respectively.

## Videos

### Tutorial videos

This is change logs playlist. Almost every update (for exception of patches with bug fixes and minor features) comes with a change log video which showcases new features, and briefly shows how to use them.

<a href="https://youtu.be/JghXifbHi-k?list=PL6UPd2Tj65nEwg2bfY-NduLihPy6fgnvK"><img src="https://img.youtube.com/vi/JghXifbHi-k/0.jpg"></a> 

Here is also a playlist of tutorials directly or indirectly related to Blockbuster/Minecraft machinima creation. Make sure to check it out, as it consists out of tutorials such as basics of Blockbuster mod, how to import OBJ models, etc.

<a href="https://youtu.be/vo8fquY-TUM?list=PLLnllO8nnzE-LIHZiaq0-ZAZiDO82K1I9"><img src="https://img.youtube.com/vi/vo8fquY-TUM/0.jpg"></a> 

### Blockbuster machinimas

This playlist consists out of videos that I've created a bunch of machinimas during Blockbuster mod testings and just when I felt like. Feel free to check out my crazy videos.

<a href="https://youtu.be/eig13klr-kw?list=PL6UPd2Tj65nFdhjzY-z6yCJuPaEanB2BF"><img src="https://img.youtube.com/vi/eig13klr-kw/0.jpg"></a> 

Here is also a playlist of 400+ community made videos with Blockbuster mod:

<a href="https://youtu.be/9hCMFoNnfxw?list=PL6UPd2Tj65nEE8kLKBxYYZLAjruJkO0r_"><img src="https://img.youtube.com/vi/9hCMFoNnfxw/0.jpg"></a> 

If you're interested in this project, you might as well follow me on any of social media accounts listed below:

[![YouTube](http://i.imgur.com/yA4qam9.png)](https://www.youtube.com/channel/UCSLuDXxxql4EVK_Ktd6PNbw) [![Discord](http://i.imgur.com/gI6JEpJ.png)](https://discord.gg/qfxrqUF) [![Twitter](http://i.imgur.com/6b8vHcX.png)](https://twitter.com/McHorsy) [![GitHub](http://i.imgur.com/DmTn1f1.png)](https://github.com/mchorse) 

Also, I would really appreciate if you will support me on Patreon!

[![Become my Patron](https://i.imgur.com/4pQZ2xW.png)](https://www.patreon.com/McHorse) 

## Bug reports

If you found a bug, or this mod crashed your game. I'll appreciate if you could report a bug or a crash to me either on [issue tracker](https://github.com/mchorse/blockbuster/issues/), on PM or on [Twitter](https://twitter.com/McHorsy). Please, make sure to attach a crash log ([pastebin](http://pastebin.com) please) and description of a bug or crash and the way to reproduce it. Thanks! 

## License

Blockbuster mod's code is licensed under MIT license. See [LICENSE.md](./LICENSE.md) file for more information about the license.

## For Devs

Language files are compiled from YML (which are located in `help/` folder) to INI format. 

If you're going to work with localization strings, modify the appropriate YML file, first. Then use the `./gradlew buildLangFiles` command to convert YML to INI format `.lang` files directly to `src/main/resources/assets/blockbuster/lang` folder. Don't forget to refresh your IDE after building the langauge files.

Also, you need [Aperture](https://github.com/mchorse/aperture)'s, [McLib](https://github.com/mchorse/mclib) and [Metamorph](https://github.com/mchorse/metamorph)'s dev builds. Build them and then grab `-sources.jar` and `-dev.jar` to `run/libs/` folder, and refresh the IDE.