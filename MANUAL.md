# Manual

For version 1.2.

## Player's recording

The player's actions can be recorded via the `/action record` command or by 
interacting (right clicking the) with actors.

The following actions are supported by this mod:

* Basic walking, jumping, looking, sprinting, swinging with left hand, and
  sneaking
* Interacting with blocks (opening doors/gates, pushing buttons, toggling
  levers), blocks like furnaces, chests and crafting table aren't supported
* Placing/breaking blocks
* Holding items in both arms
* Equipping armor
* Send (formatted messages) in chat (use `[` instead of `§` for formatting)
* Mounting entities
* Shooting arrows
* Flying the elytra

Recorded actions are stored in the `blockbuster/records` folder in world's save
folder.

Recorded player actions can be played either by `action play` command or by using 
*playback button* when an actor entity is tied to director block.

See Commands section for more reference about the commands.

**Side note**: this mod records only player's actions, not his visual look, so
recording yourself with morphing mods, or mods that modify your player model
won't affect the playbacked actors (actors will not look like a chicken or will
not have hat or cape).

## Director block

*Director block* is a special block, which comes with this mod, that
responsible for managing the scene (like a real life director). Its purpose is to tie
the actors together.

With the power of the *director block* you can playback all registered actors
together without having to setup big contraptions from redstone and command
blocks.

To register an actor to the *director block*, you should use *register item*. 
Right click on actor, to take a sample of entity's UUID onto *register item*, 
and then right click *register item* on the *director block*. If you have 
succesfully registered the actor, you'll get a message in the chat that 
actor was succesfully registered.

When you register your actors, you'll be able to use full the potential of 
*director block*. The recorded actors will be played when you'll start
recording another actor.

To playback the scene, you need to attach a *playback button* to *director block*. 
Simply right click *director block* while holding *playback button* in
your hand, and use the *playback button* (right click while holding *playback*
item), to playback the scene.

You can manage director block's cast by right clicking the director block, the
GUI will pop up. You'll see a scroll list with entries of registered entities,
their name, icon and two buttons for managing the entity (edit or remove). 
In previous version you would have to break the block to reset the scene.

By the way, all of these items are available in creative "Blockbuster" tab.

### Director map block

*Director map block* is another variation of *director block* designed for
adventure maps (cinematics FTW). It's just like *director block*, but functions
more as `play` command.

To register replay with *director map block* you need to open up the *director map
block*'s GUI (right click the block). Then input your new actor's recording id
(file name from which actor would be played), and press the "Add" button, after that
a new entry will appear in the scroll box below the "Add" button. 

You can edit the following actor's properties: name tag, recording id, skin and
invincibility.

When you'll register your actors, you can playback this block with playback
button (just as with regular *director block*). Redstone hooks are also
supported by *director map* block.

### Redstone Hooks

When the *director block* starts playing the scene, it emits a redstone signal on the
west side of the block (the side of the block where a play white triangle is
drawn).  When the *director block* stops playing the scene, it emits redstone signal
on the east side of the block (the side of the block where a stop white square
is drawn).

This can be useful for resetting the scene. You might want to explode a TNT while
recording your scene, so to avoid tedious rebuilding, you can use stop hook
(when block stops playing) and attach redstone with command block that resets
the scene (using the `/clone` command, for example).

Or maybe you want start playing a tune, or summon some zombies when the director
starts playing, no worries, use the redstone play hook. 

## Actors

Actors are the entities that you use to playback your actions. Actors, by
default, look like crash test dummies, but you may change their skin using the
*actor configuration* item. First you'll have to put some skins into the
`minecraft/config/blockbuster/skins` folder, and then you can select a skin from
this folder using the GUI. In this GUI you can also change other properties such as
name, recording file name, skin and invincibility. 

To record the actor, you must attach it first to a *director block*, and simply
right click it (the actor), and start performing some sick movements. When you'll be done,
you need to stop the recording either by right clicking actor again or entering
`record` command with his name (actually, you can use any name for file, as long
as you provide the first argument).

For adventure maps makers: you can put your skins into world's save folder
`blockbuster/skins` to transfer the skins with the map, but they won't work in
multiplayer.

Player records are saved in `blockbuster/records` folder in world's save folder.

Note: Blockbuster mod supports only 64x32 textured skins, for now.

## Cameras

ToDo: update this section, add a video, pls.

## Commands

This mod provides following commands:

### Action command

Action command records player's actions to given file, or playbacks recorded 
player's actions from file with optionally specified name tag, skin name and 
invulnerability flag. General command's syntax:

	/action <play|stop> <record_name>

Alternative command's syntax for `/action play`:

	/action play <record_name> [custom_name] [skin_name] [is_invulnerable] 

`[is_invulnerable]` is a flag, it's either 1 (for) or 0.

### Director command

Director command plays or stops director (or director map block) at specified 
position. Command's syntax:

    /director <play|stop> <x> <y> <z>

### Camera command

**Attention**: camera command is a client-side command, so don't use it in 
command blocks.

Camera command allows you to manage camera profile and its camera fixtures. 
This command has a lot of sub-commands which are covered below.

#### Saving and loading camera profiles

With camera command you can save and load camera profiles using following syntax 
of camera command:

	/camera <save|load> <filename>

#### Starting and stopping camera profiles

Camera command gives you ability to start or stop camera's playback (based on 
camera's profile fixtures). General syntax for starting and stopping camera 
profile:

	/camera <start|stop>

Alternatively you can use the default key bindings `Z` (for starting the camera) 
and `X` (for stopping the camera).

#### Teleporting to camera fixture

Sometimes you may want to teleport to a camera fixture. You can use following 
camera command syntax to teleport to specific camera fixture:

	/camera goto <index> [progress]

`[progress]` is an optional argument that allows you to set progress for 
fixtures like circular or path. For example, if you want to teleport in the 
middle of the path or circular fixture, you can type this command:

	/camera goto 0 0.5

#### Clear camera profile

You might want to remove all fixtures from currently loaded camera profile. To 
remove all fixtures, use following syntax of camera command:

	/camera clear

#### Camera fixture management

There's lots of sub-commands for camera fixture management. Let's start off with 
adding camera fixtures to camera profile.

##### Adding camera fixtures to camera profile

Command's syntax for adding camera fixtures:

	/camera add <idle|path|look|follow|circular> <duration> [values...]

The first argument is the type of fixture you want to add, and second one is the 
duration of camera fixture that you adding, and sets the values of camera fixture. 
For every type of fixture, there's different requirements for `[values...]` argument,  
but they're optional since all of these `[values...]` have default values. 

See "Editing camera fixtures" section for more information about these  
`[values...]`. 

##### Editing camera fixtures

Command's syntax for editing camera fixtures:

	/camera edit <index> [values...]

Again these `[values...]`, let's find out what do they mean. Every type of 
commands have its own properties, like position. When you add or edit fixtures, 
fixtures have two input sources which they can use to extract required information: 
`[values...]` and player himself.

Only `circular` command actually uses `[values...]`, other fixtures use only 
the player as input source.

So there's the list of possible `[values...]` for fixture types:

* Circular fixture – `/camera edit <index> <distance> <circles>`, `<distance>` 
  means how many blocks away to offset the circular motion from center point, 
  and `<circles>` means for how many degrees to spin around the center point.

##### Removing camera fixtures

To remove the camera fixture from camera profile, simply use following camera 
command syntax:

	/camera remove <index>

##### Moving camera fixtures

Let's say you want to move the camera fixture at the end or to beginning, then 
you'll need to use following camera command syntax:

	/camera move <from> <to>

The argument names are pretty self-explanatory. To move fixture from index 2 
to beginning (index 0), you'll use following command:

	/camera move 2 0  

##### Setting fixture's duration

To set camera fixture's duration, simply use following camera command syntax:

	/camera duration [index] [duration]

Duration of camera fixture is measured in milliseconds. 1000 milliseconds is 
basically 1 second.

If you want to know the duration of current bound camera profile, simply 
invoke this command without arguments:

	/camera duration

If you want to know the duration of camera fixture in current camera profile, 
simply specify the index of camera fixture, and the command will output the 
duration of given camera fixture:

	/camera duration 0

##### Configuring points in path fixture

There's only one type of camera fixture that requires more than just adding 
or editing itself, and it's the path fixture. Path fixture lets you create 
smooth camera movements, but to configure these paths you need to modify the 
path itself.

The syntax for managing path points in path fixture is following:

	/camera path <fixture_index> [remove_point]

**Note**: you need to create path camera fixture first, before managing the 
path fixture.

If you'll provide only the `<fixture_index>` argument, the command will add a 
point to path fixture based on the player's position and rotation. If you'll 
provide also a `[remove_point]` argument, it will remove point from path 
fixture at given index.

### Side note regarding string arguments 

All arguments (file name, actor's custom tag name, and actor's skin) can't
have spaces. That's due to the fact how minecraft's command handler parses
arguments.
