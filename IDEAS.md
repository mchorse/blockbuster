# Ideas for next Blockbuster versions

There's some ideas that I want to implement in next Blockbuster mod: 

* Record cameras (basic movement and jumps between cameras), so you wouldn't need
  to follow the actors by yourself every time
* Advanced cameras configuration
    * Setting custom path
    * Setting look target (always align towards target)
* Add more of recording stuff like:
    * Use and throw potions
    * Set shit on fire with flint and steel
    * Kick asses with diamond sword/axe
    * Use put stuff in chests
    * Use furnances
* Scripted recording addition to player's recording
    * Explanation: Some of the stuff isn't possible when you use player's 
      recording, like killing other actor or player. Scripted recording will 
      add a new spin to your machinimas and cinematics. 
      
      With scripted recording you'll be able to specify different behavior like: 
      run over there, and then fight other actor in the sight, if he lowers your HP to 
      5, run like a pussy, and jump into lava. Something like that. 
* Area redstone sensor (to avoid using pressure plates)
* Make commands accept spaced strings (this one is simple)
* Support of multi-player world skins
* Rewrite recording code (pixel -> vector, I'll get what I mean)
* Custom actor models (not only player-like, but also custom one)
    * If you gonna do it, also supply minecraft vanilla mobs with skins and 
      create a tool to create those models (that's to me)
    * Also add the ability to morph player into this entity
* Custom actor actions (like dancing, waving with hands like crazy person, or making a blow kiss)
* Animating actor skins (basically to create illusion of lip syncing or eyes blinking)  

## Things needed to be done before update

This is my list for what I should do before releasing an update:

- Make sure that everything works
- Make sure that version number is everywhere correct
    - In `Blockbuster.java`
    - In `build.gradle`
    - In `mcmod.info`
- Make a github release (tag)
- Post update message and update original post on minecraftforum.net
- Post update message and update original post on minecraftforge.net
- Post link in twitter