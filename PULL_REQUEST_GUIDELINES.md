Thank you for contributing to the mods. The maintainer will review your changes and reach out to you for further information.

**It would be best if you join the [McHorse's Discord server](https://discord.gg/qfxrqUF) so we can discuss your changes and the official update process.**

Here are some guidelines for pull requests to prevent bugs and problems.

1. Do not submit **massive** pull requests with 10k lines of code changed. The maintainer has to review it and the harder you make the review process the less the maintainer will be fond of the PR.
   <br><br>Massive PRs also lead to a ton of change requests and comments because more lines of code are changed. You should rather try and split up your changes into multiple pull requests. Change things one by one... don't try to change the whole world in one Pull Request. 

2. McHorse has not followed encapsulation everywhere but that does not mean you get a free pass to not adhere to the core principles of OOP. So when you add a new class you should encapsulate its attributes. Also, this has been misrepresented in many places but encapsulation does not mean you are forced to automatically add getters and setters for every single thing. It is about hiding the internal implementation details to make the code maintainable.
   <br><br>If you are not sure about encapsulation because it collides with existing modules / code or has design issues, come to the [McHorse's Discord server](https://discord.gg/qfxrqUF) and we can talk about it and find a solution.

3. If you update your `gradle` version or change other things in the gradle files to run your dev environment, like `build.gradle`, do not commit them!
   <br><br>**Example what you should not do and what the maintainer will not accept as a PR:**
   - randomly changing dependency versions **without any need**
   - removing important gradle tasks or other things **without any need**
   
   If you think something should be changed in `build.gradle`, come to the [McHorse's Discord server](https://discord.gg/qfxrqUF) and we can talk about it. **The maintainer won't accept changes that break the building / compilation process and other important things.**

4. If you do not know what something does, what a certain piece of code is used for etc., do not change it radically or even remove it. Come to the [McHorse's Discord server](https://discord.gg/qfxrqUF) and we can talk about it and help you understand what it does.
   <br><br>The maintainer will not accept changes that just **randomly without any reason remove or break existing working modules. This behaviour from others has caused many bugs in the past.**

5. If your changes cause bugs, you, as a responsible, polite and mature developer, should also help fixing them.

6. It would be nice if you document your code. Of course, good code should be sufficient as documentation and unecessary comments are also bad, but adding javadocs to utility methods is probably a good idea.
   <br><br>Also documenting things that are not clear directly and your workarounds around Minecraft's limitatons help future developers understand it and not potentially changing it because they didn't know your thought process and causing the bugs you tried to prevent.
   <br><br>For coremodding you should document why and what you did with the bytecode, so future developers can understand it and it would also help with portability.

7. Very big updates with potential bugs will first be released as dev builds on the [McHorse's Discord server](https://discord.gg/qfxrqUF) for testing and the responsible developer should help with moderating them to fix newly found bugs.
