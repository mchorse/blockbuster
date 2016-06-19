![Blockbuster logo](./logo.png)

# Blockbuster

Blockbuster is a Minecraft mod which lets you create Minecraft machinimas in 
single player (without having to recruit/organize a crowd of actors and cameras) 
and simple cinematics in adventure maps.

Blockbuster mod is built on top of Forge 12.6.1.1907 for Minecraft 1.9, and recording 
code is based on the code borrowed from [Mocap mod](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000) 
(author of the mod gave me permission to use his code). 

Original minecraft forum thread.

Tested on Mac OS X 10.10 only, but in theory suppose to work on any OS.

**Pun intended**.

## Install

Install Minecraft Forge, then go to 
[releases](https://github.com/mchorse/blockbuster/releases) and download the 
latest version jar file. Put it in minecraft's `mods` folder, launch the game, 
and done. 

After that, Blockbuster mod should be installed and will appear in Minecraft's 
mods menu. If Blockbuster didn't appeared in mods menu, then something went 
wrong.

## Show me what you got

See the demo video, tutorial video and checkout 
[my adventure map with cinematics](https://github.com/mchorse/blockbuster/releases/tag/1.0-rc1) 
(made available by this mod).

P.S.: there will be links, soon.

## Features / Manual

This mod provides following features:

### Player recording

Player's actions can be recorded via `record` command or using actors.

Following actions are fully supported by this mod:

* Basic walking, jumping, looking, sprinting, swinging with left hand, and sneaking
* Interacting with blocks (opening doors, pushing buttons, toggling levers)
* Placing/breaking blocks
* Holding items in both arms
* Equipping armor
* Send messages in chat
* Mounting entities

Recorded actions are stored in the `blockbuster/records` folder in world's save 
folder.

Recorded player actions can be played either by `play` command or by actor entity. 

See Commands section for more reference about the commands.

### Director block

*Director block* is a special block, that comes with this mod, which is responsible 
managing scene (like real life director). Its purpose is to tie together 
actors and cameras. It has also special redstone hooks.

With the power of *director block* you can playback all registered actors together 
without having to setup big contraptions from redstone and command blocks.

To register actor or camera, you should use *register* item. Right click on actor or 
camera first, to take a sample of UUID onto *register* item, and then right 
click *register* item on *director block*. If you succesfully registered the entity 
(actor or camera), you'll get message in the chat that entity was succesfully 
registered.

When you'll register your actors and cameras, you'll be able to use full potential 
of this mod. Already recorded actors will be played when you'll start recording 
another actor, and cameras will have a really useful feature.

When the camera is registered to your *director block*, you can switch between 
cameras using "[" and "]" keys ("[" is for previous, and "]" is for next camera). 
The order in which you'll be switched around, is depends on the order in which 
you registered the cameras, you can name all your cameras from "Camera 1" to 
"Camera N" and see the order of your cameras by right clicking *director block* 
(*director block* will send you the message in the chat with the list of all cast 
members: actors and cameras, and their names).

To playback the scene, you need to attach a *playback button* to *director block*. 
Simply right click *director block* while holding *playback button*, and use the 
*playback button* (right click while holding *playback* item), to playback the scene.

By the way, all of these items are available in creative "Blockbuster" tab.

#### Director map block

*Director map block* is another variation of *director block* designed for 
adventure maps (cinematics FTW). It's just like *director block*, but functions 
more as `play` command.

With *director map block*, you register actors with the *name tag* item. 
Name of the name tag specifies the custon name tag for your actor and the file 
name from which he's being played. You may specify custom skin by appending 
colon (`:`) and the name of the skin. Hint: to give a custom name to a name tag, 
use anvil.

For example, if you want your actor to have file name of "JustDoIt", and actor's 
skin "Shia LaBeouf", you should name your tag "JustDoIt:Shia LaBeouf" and  
record a replay with this command:

    /record JustDoIt

When you'll register your actors, you can playback this block with playback 
button (just as with regular *director block*). Redstone hooks are also supported 
by *director map* block.

Note: *director map block* doesn't support cameras, yet. That would be cool, 
though. 

#### Redstone Hooks

When *director block* starts playing the scene, it emits redstone signal on the 
west side of the block (the side of the block where a play white triangle is drawn). 
When *director block* stops playing the scene, it emits redstone signal on the 
east side of the block (the side of the block where a stop white square is drawn).

This can be useful for reseting the scene. You might want to explode a TNT while 
recording your scene, so to avoid tedious rebuilding, you can use stop hook 
(when block stops playing) and attach redstone with command block that resets 
the scene (using /clone command).

Or maybe you want start playing a tune, or summon some zombies when the 
director starts playing, no worries, use the redstone play hook. 

### Actors

Actors are the entities that you use to record your actions. Actors, by default, 
look like crash test dummies, but you may change their skin, by using the 
*skin manager* item. First you'll have to put some skins into 
`minecraft/config/blockbuster/skins` folder, and then you can select a skin 
from this folder using GUI.

To record the actor, simply right click it, and start performing some sick 
movements. When you'll be done, you need to stop the recording either by 
right clicking actor again or entering `record` command with his name (actually, 
you can use any name for file, as long as you providing first argument).

For adventure maps makers: you can put skins into world's save folder 
`blockbuster/skins` to transfer the skins with the map.

Note: Blockbuster mod supports only 64x32 textured skins, yet.

### Cameras

Cameras are special rideable entities which is used as cameras. They're not 
acturally recording the scene, they're just giving you ability to traverse the 
space more freely.

Camera is a flying rideable entity with some configuration properties. With the 
help of *camera configuration* item, you can change camera's characteristic, such as: 
speed, maximum acceleration, acceleration rate, and direction of movement 
(any direction, or only horizontal).

If the camera is attached to the *director block*, then you can jump between 
cameras using "[" and "]" keys, you can rebind those keys in the Settings -> Controls
menu.

Also, if the camera is attached to the *director block*, during scene's playback, 
all attached cameras to current scene will be hidden, and will appear as soon 
as *director block* will stop playing the scene.

### Commands

This mod provides two commands:

1. `record` command which records player's actions to file name provided specified 
   in first argument, and stops the recording (run `record` again)
2. `play` command which playbacks player's actions from a file specified with 
   actor's custom tag name and skin
3. `play-director` command which triggers playback of director block located on 
   specified position 

Examples:

    # Record player's actions to file named "football"
    /record football 
    
    # To stop the recording
    /record footbal
    
    # Play football recording with actor's custom tag name "DavidBeckham" and skin "DavidBeckhamsSkin"
    /play football DavidBeckham DavidBeckhamsSkin
    
    # Play a director block that is located on X: 19, Y: 4, Z:-100
    /play-director 19 4 -100

Note: all arguments (file name, actor's custom tag name, and actor's skin) can't 
have spaces. That's due to the fact how minecraft's command handler parses arguments.
