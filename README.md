# Savegame parser for Diablo II: Resurrected examples

This project contains some example implementations of the [D2RSavegameParser library](https://github.com/Paladijn/d2rsavegameparser).  
It requires JDK 21 to compile. If you don't have it grab the latest version of your preferred distribution or grab one from https://sdkman.io/jdks (I usually stick to Azul or Temurin, all of them should work).

Currently the following examples are available:
### List all characters with the socket reward quest still available.
Call io.github.paladijn.d2rsavegameparser.examples.Main with the following arguments
> socket "C:\Users\Paladijn\Documents\Save Games\Diablo II Resurrected"  

Which will result in a list of all character files, along with the difficulty, that still have the socket quest reward available. This will save you some time running another one to Normal A5.

### List all set items (with proper translations)
Call io.github.paladijn.d2rsavegameparser.examples.Main with the following arguments
> sets "C:\Users\Paladijn\Documents\Save Games\Diablo II Resurrected" enUS

This will result in a list of all set items (including double ones and the ones in shared stash) and their location on the character:  
Naj's Circlet -> MuleSetsOne.d2s at STORED (INVENTORY) [2, 4]  
Sander's Riprap -> MuleSetsOne.d2s at STORED (STASH) [3, 2]  
Vidala's Snare -> SharedStashSoftCoreV2.d2i at STORED (STASH) [0, 6]  
etc. etc.

The following languages are supported: enUS, deDE, esES, esMX, frFR, itIT, jaJP, koKR, plPL, ptBR, ruRU, zhCN, zhTW
