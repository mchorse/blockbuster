# Blockbuster

Blockbuster is a minecraft mod (which is built on top of Forge and uses portions of 
code from [Mocap mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000)) which lets you create minecraft 
machinimas in single player.

Blockbuster supports only Minecraft 1.9 for now.

## Install

Install Minecraft Forge first, then go to 
[releases](https://github.com/mchorse/blockbuster/releases) and download the 
latest version jar file. Put it in minecraft's `mods` folder, launch the game, 
and done. 

After that, Blockbuster mod should be installed and will appear in Minecraft's 
mods menu.

## Features

This mod provides following features:

* Player recording (using `record` command or Actors), following actions are recorded by mod:
  * Basic walking, jumping, looking, sprinting, and sneaking
  * Interacting with blocks
  * Placing blocks
  * Holding items in both arms
  * Equipping armor 
* Director block – organizes actors and cameras, playbacks the actors and allows player to jump between cameras
  * Attach actors and cameras
  * Playbacks actors and hides cameras during playback
  * Let you jump between cameras using `[` and `]` keys 
* Actors – recorded by players and playbacked by director block or commands
  * Actor's skin can be changed using Skin Manager item on actor
  * Actors can be attached to director block
  * Actors can be playbacked by director block or by `play` command
* Cameras – can be mounted and you can control it in the mid-air
  * Jump between cameras with `[` and `]` keys (you can only jump between cameras if, the camera you're riding is attached to director block)
  * Configure speed, acceleration rate, max acceleration and direction of movement with Camera Configuration item

## Tutorial

See the demo video, tutorial video and/or play adventure map with cinematics (made available by this mod). 