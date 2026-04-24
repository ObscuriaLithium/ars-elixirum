* Added **elixir trait system** and **ingredient role system**. Each ingredient role carries its own trait, which is inherited by the resulting elixir. The role itself is determined by the ingredient’s order in the recipe. Traits directly define how an elixir behaves, how it is applied, and how stable it is. The goal of this system is to create a meaningful alchemical environment, where understanding and mastering it allows you to brew exactly the elixirs you need
* Added **Splash Elixirs**, **Lingering Elixirs**, and **Witch Totems of Undying** infused with elixir effects. Splash and Lingering variants are obtained through the elixir trait system
* Complete refactor of the **player alchemy profile structure**. It now uses a component-based system designed for future expansion of mod features, including recipe books, catalogs, and a passive skill tree. However, this is a **breaking change and profiles from previous versions will be reset**
* Improved elixir effect list tooltip, now includes icons
* Optimized collection UI, significantly reduced memory usage
* You no longer see the list of potential effect ingredients until the effect is discovered
* Fixed side effect mechanics and their activation chance
* Improved mod localization support; all strings are now fully externalized into language files
* Significantly reduced save data size by using identifiers instead of names
* Added additional translations