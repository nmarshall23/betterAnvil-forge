betterAnvil-forge
=================

A fork of Vdvman1's BetterAnvil Mod. There is no release for Minecraft 1.7.10. And after tweaking my tools using squidutils I find the Anvil XP costs have gone up significantly.

I will be revising the math on costs of fixing Enchanted tools. I have a different vision then Vdvman1, in how the Anvil should work.

Anvil Funtions:

* Rename a Item
* Restore a unit of durablity to an item.


Repair Costs
------------

Repairing non-enchanted items does not cost XP.

Repairing a item using units of its material restores a percentage of it's total durability based on how many items it took to craft it. So a one unit restores 33% of a pickaxe and 50% of a sword. Repairing Armor restores 25% of it. In addtion if the item is enchanted 


Repair amount Formulate
-----------------------

percentage each unit restores =  ( 1 / Number of Material used to craft item )  * (enchantablityFactor)

itemType = for Armor = 9, for Sword or Tool = 12
enchantablityFactor = Max( Item's enchantablity / itemType , 1) 


Level Costs Formulate
---------------------

base cost for enchantment is found by the inverse of enchantment weight. Ranging from 1, 2, 4, 6.

Level cost for each unit of repair = (base cost for enchantment * number of levels) / 2


Notes
-----

This means that the repair costs are based on the enchantments on the item being repaired.
This leads to tools made of highter enchantablity, needing less levels spend on repairing them. 


Features:

Renaming items is free.
Anvils do not break.
Removed the Prior Work penalty.
Enchantability of the item will matter.
I am thinking that items with higher Enchantability will receive a significant bonus repair amount. Or should it be cheaper to add Enchants to them?
