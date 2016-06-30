![Blockbuster logo](./logo.png)

# Blockbuster

Blockbuster (**pun intended**) is a Minecraft mod which lets you create simple Minecraft machinimas in 
single player (without having to recruit/organize a crowd of actors and cameras) 
and simple cinematics in adventure maps.

Blockbuster mod is built on top of Forge 12.6.1.1907 for Minecraft 1.9, and recording 
code is based on the code borrowed from [Mocap mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000) (author of the mod gave me permission 
to use his code). 

[Original minecraft forum thread](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2700216-blockbuster-create-simple-machinimas-and-adventure). 
Tested on Mac OS X 10.10 only, but in theory suppose to work on any OS.

## Install

Install Minecraft Forge, then go to 
[releases](https://github.com/mchorse/blockbuster/releases) and download the 
latest version jar file. Put it in minecraft's `mods` folder, launch the game, 
and done. 

After that, Blockbuster mod should be installed and will appear in Minecraft's 
mods menu. If Blockbuster didn't appeared in mods menu, then something went 
wrong.

## Videos

Blockbuster 1.0 – Tutorial by me

<a href="https://www.youtube.com/watch?v=LPJb49VUUqk">
    <img height="420" src="https://img.youtube.com/vi/LPJb49VUUqk/0.jpg">
</a>

## License

Mmm, I want something like MIT, but with restriciton on reposting on some 
websites like 9minecraft or something, and forbidding using in mod packs 
without the my permission. Yeah. 

## Features

This mod provides following features:

#### Player's recording

* All stuff that Mocap, but more
* Interacting with blocks (opening doors, toggling levers, pushing buttons, etc.)
* Breaking blocks
* Mounting entities like pigs (tested with AnimalBikes, works well, but keep animals in fences)
* Flying elytra
* Text formatting in chat using '[' character instead of '§'

#### Director blocks

* Has two variations: for machinimas and for adventure maps
* Ties actors and cameras into an organizable scene (with lots of benefits)
* Can be playbacked by playback button or /play-director command
* Both of the block have their own GUIs for managing the cast (view, add, edit, remove, reset)

#### Actors

* Can playback player's actions
* Customizable skins (simply drop 64x32 skins into minecraft/config/blockbsuter/skins folder)
* Mostly look like players
* When tied to director block and player starts recording this actor, player will be able to react to previously recorded actors

#### Cameras

* Configurable camera's properties such as: speed, acceleration rate, maximum acceleration and flying direction
* Jump between cameras when tied to director block

#### Commands

* Record command (/record) – allows players to record their actions to a filename for a later playback
* Play command (/play) – allows playback of earlier recorded file
* Play director command (/play-director) – allows player to trigger playback in director block specified at XYZ position

## Manual

Manual is located in the root of this repository in file named [MANUAL.md](./MANUAL.md).