# Manual

For version 1.1.

## Player recording

Player's actions can be recorded via `record` command or by interacting (right  
clicking the) with actors.

Following actions are fully supported by this mod:

* Basic walking, jumping, looking, sprinting, swinging with left hand, and sneaking
* Interacting with blocks (opening doors/gates, pushing buttons, toggling levers)
* Placing/breaking blocks
* Holding items in both arms
* Equipping armor
* Send formatted messages in chat (use `[` instead of `§` for formatting)
* Mounting entities
* Shoot arrow
* Flying elytra

Recorded actions are stored in the `blockbuster/records` folder in world's save 
folder.

Recorded player actions can be played either by `play` command or by actor entity. 

See Commands section for more reference about the commands.

## Director block

*Director block* is a special block, that comes with this mod, which is responsible 
managing scene (like real life director). Its purpose is to tie together 
actors and cameras. It has also special redstone hooks.

With the power of *director block* you can playback all registered actors together 
without having to setup big contraptions from redstone and command blocks.

To register an actor or a camera, you should use *register item*. Right click on 
actor or camera first, to take a sample of entity's UUID onto *register item*, 
and then right click *register item* on *director block*. If you have succesfully 
registered the entity (actor or camera), you'll get message in the chat that 
entity was succesfully registered.

When you'll register your actors and cameras, you'll be able to use full potential 
of this mod. Already recorded actors will be played when you'll start recording 
another actor, and cameras will disappear when *director block* starts playing 
and will appear again when *director block* will stop playing.

When the camera is registered to your *director block*, you can switch between 
cameras using "[" and "]" keys ("[" is for previous, and "]" is for next camera). 
The order in which you'll be switched around, is depends on the order in which 
you registered the cameras, you can name all your cameras from "Camera 1" to 
"Camera N" and see the order of your cameras by right clicking *director block* 
(*director block* will send you the message in the chat with the list of all cast 
members: actors and cameras, and their names).

To playback the scene, you need to attach a *playback button* to *director block*. 
Simply right click *director block* while holding *playback button* in your hand, 
and use the *playback button* (right click while holding *playback* item), to 
playback the scene.

You can manage director block's cast by right clicking the director block, the 
GUI will pop up. You'll see a scroll list with entries of registered entities, 
their name, icon (which signify either that's an actor or a camera) and two 
buttons for managing the entity (edit or remove). In previous version you would 
have to break the block to reset the scene.

By the way, all of these items are available in creative "Blockbuster" tab.

### Director map block

*Director map block* is another variation of *director block* designed for 
adventure maps (cinematics FTW). It's just like *director block*, but functions 
more as `play` command.

To register replay with *director map block* you need to open up 
*director map block*'s GUI (right click the block). Then input your new actor's 
recording id (file name from which actor would be played), and press "Add" 
button, after that a new entry will appear in the scroll box below "Add" button. 

You can edit following actor's properties: name tag, recording id, skin and 
invincibility.

When you'll register your actors, you can playback this block with playback 
button (just as with regular *director block*). Redstone hooks are also supported 
by *director map* block.

Note: *director map block* doesn't support cameras, yet. That would be cool, 
though. 

### Redstone Hooks

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

## Actors

Actors are the entities that you use to record your actions. Actors, by default, 
look like crash test dummies, but you may change their skin, by using the 
*actor configuration* item. First you'll have to put some skins into 
`minecraft/config/blockbuster/skins` folder, and then you can select a skin 
from this folder using GUI. In this GUI you can also change other properties such 
as name, recording file name, skin and invincibility. 

To record the actor, simply right click it, and start performing some sick 
movements. When you'll be done, you need to stop the recording either by 
right clicking actor again or entering `record` command with his name (actually, 
you can use any name for file, as long as you providing first argument).

For adventure maps makers: you can put skins into world's save folder 
`blockbuster/skins` to transfer the skins with the map.

Player records are saved in `blockbuster/records` folder in world's save folder.

Note: Blockbuster mod supports only 64x32 textured skins, for now.

## Cameras

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

## Commands

This mod provides two commands:

1. `record` command which records player's actions to file name provided specified 
   in first argument, and stops the recording (run `record` again)
2. `play` command which playbacks player's actions from a file specified with 
   actor's custom tag name and skin, 2nd through 4th are optional arguments 
   invulnerability, if it's 1, actor is invulnerable, if it's 0, actor is vulnurable 
3. `play-director` command which triggers playback of director block located on 
   specified position 

Examples:

    # Record player's actions to file named "football"
    /record football 
    
    # To stop the recording
    /record footbal
    
    # Play football recording with actor's custom tag name "DavidBeckham" and skin "DavidBeckhamsSkin"
    /play football DavidBeckham DavidBeckhamsSkin
    
    # Play football recording with actor's custom tag name "DavidBeckham" and skin "DavidBeckhamsSkin" and make actor invulnerable
    /play football DavidBeckham DavidBeckhamsSkin 1
    
    # Play a director block that is located on X: 19, Y: 4, Z:-100
    /play-director 19 4 -100

Note: all arguments (file name, actor's custom tag name, and actor's skin) can't 
have spaces. That's due to the fact how minecraft's command handler parses arguments.