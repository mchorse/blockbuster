![Blockbuster](./logo.png) 

# Blockbuster

Blockbuster (**pun intended**) is a Minecraft mod which lets you create simple 
Minecraft machinimas in single player (without having to recruit and organize a 
crowd of actors and cameras) and simple cinematics in adventure maps.

Blockbuster mod is built on top of Forge 12.17.0.1976 for Minecraft 1.9.4, and 
the recording code is based on the code from the 
[Mocap mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000) 
(the author of the mod gave me [permission](http://i.imgur.com/lc1lJB5.png) to use his code). 

Original [minecraft forum thread](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2700216-blockbuster-create-simple-machinimas-and-adventure) 
and [planet minecraft post](http://www.planetminecraft.com/mod/blockbuster-machinima-mod/). See also [McME](https://github.com/mchorse/mcme), the JSON custom model editor for Blockbuster mod.

## Install

Install [Minecraft Forge](http://files.minecraftforge.net/), then go to 
[releases](https://github.com/mchorse/blockbuster/releases) and download the 
latest stable version (not a pre-release) of jar file. Put it in minecraft's `mods` folder, and launch the game. 

After that, Blockbuster mod should be installed and will appear in Minecraft's 
mods menu. If Blockbuster didn't appear in the mods menu, then something went 
wrong.

## Videos

### Tutorial video

Tutorial videos for 1.2.1. This playlist shows how to use the mod features. Every update comes with a change log video which show out new features.

<a href="https://youtu.be/mDCYX1oRKYk?list=PL6UPd2Tj65nHvEH-_F_brz6LQDdlsCIXJ">
    <img src="https://img.youtube.com/vi/mDCYX1oRKYk/0.jpg">
</a>

### Machinima Examples

This playlist consists out of videos that I've created during Blockbuster mod testings. Those videos are my lab experiments. Don't judge the quality of these machinimas yet, I'm still learning.

<a href="https://www.youtube.com/watch?v=Q-IdY4VsMFk&index=1&list=PL6UPd2Tj65nFdhjzY-z6yCJuPaEanB2BF">
    <img src="https://img.youtube.com/vi/Q-IdY4VsMFk/0.jpg">
</a>

## Features

### Player's recording

* All stuff in Mocap, but more
* Text formatting in chat using the `[` character instead of `§`
* Interacting with blocks (opening doors, toggling levers, pushing buttons, etc.)
* Breaking blocks
* Mounting entities (tested with AnimalBikes, works good enough, but keep 
  animals in fences)
* Flying using elytra

### Director block

* Manages actors and replays
* Can playback actors and replays using playback button or `/director` command
* Provides GUI for managing the cast
* Camera profile can be attached to the playback button while attaching it to director block

### Actors

* Can playback player's actions
* Customizable actor models and skins
* When tied to director block and player starts recording this actor, player 
  will be able to react to previously recorded actors and morphed into actor's model
  and skin
* When recording, HUD overlay would be displayed with caption to which file it 
  records player's actions

### Cameras

* Flexible and complex customizations of cameras
* Camera profiles – saveable and loadable list of camera fixtures
* Camera fixtures – constructing blocks of camera profiles, they define how the camera 
  moves. Following fixtures are provided by the mod:
    * Idle fixture – static looking at camera
    * Path fixture – smooth path which camera follows
    * Look fixture – look fixture which keeps focus on a given entity
    * Follow fixture – GoPro fixture
    * Circular fixture – fixture that rotates around the given point with 
      specified offset and distance
* Keyboard bindings that allows *almost* full control of camera profile

### Commands

* Action command (`/action`) – allows players to record their 
  actions to a file and playback recorded actions
* Director command (`/director`) – allows players to trigger or stop 
  playback in director block specified at XYZ position
* Camera command (`/camera`) – allows players to customize camera profiles
* Morph command (`/morph`) – allows players to morph into custom models
* Export model command (`/export-model`) – allows players to export in-game models (basic limb and poses generation)

## Role Plays

Technically, you can use this mod also for role playing. In 1.3 and up, Blockbuster has a feature called player morphing, which allows players to morph into different "creatures." 

So in theory, you could use this mod for role playing a chicken, or cat, or other creater other than human (model).

## License and Manual

Manual is located in repository's [wiki](https://github.com/mchorse/blockbuster/wiki). See file [LICENSE.md](./LICENSE.md) for more information about the license.
