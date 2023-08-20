### Changelog 1.18.2-7.0.9.15 (2023-08-20)
* Add/fix compatibility with Modern UI 3.7.+
* Improve Traditional Chinese translations (@StarskyXIII)
* Update vi_vn.json (@ZzThanhBaozZ)

### Changelog 1.18.2-7.0.8.12 (2022-11-18)
* Fix crash when charging curios slots

### Changelog 1.18.2-7.0.7.8 (2022-08-13)
* Remove protocol check on Modern UI if absent

### Changelog 1.18.2-7.0.6.7 (2022-07-21)
* Fix GUI background image not connected
* Fix disconnect button in selection tab always invisible
* Fix double shift check working even when typing chars
* Improve security check with wireless charging
* Allow to load values when editing a single device in connections tab
* Optimize disconnecting this device in connections tab

### Changelog 1.18.2-7.0.5.6 (2022-07-20)
* Fix owner access display in selection tab
* Fix command permission check

### Changelog 1.18.2-7.0.4.5 (2022-07-19)
* Add command to set super admin
* Adjust flux storage default configs
* Allow flux plug to receive energy in any case
* Allow super admin to fill up a flux storage
* Fix energy duplication glitch
* Fix TOP info not shown in dedicated server
* Fix storage energy not synced to client via connections tab
* Fix disconnect button still visible when no networks

### Changelog 1.18.2-7.0.3.4 (2022-07-16)
* Update localizations
* Update GUI artworks
* Add disconnect button to Network Selection tab
* Add new Network Connections tab
* Add back chunk loading
* Give super admins more privileges
* Fix priority not work immediately

### Changelog 1.18.2-7.0.2.3 (2022-06-21)
* Fix wireless charging bugs
* Update JEI API
* Change wireless settings to individual player
* Enhance and update localization
* Enhance network packet security
* Enhance overall GUI flexibility
* Optimize network packet handling
* Optimize energy transfer system
* Optimize data synchronization
* Optimize client cache usage
* Fix Flux Storage lighting glitch
* Fix capability invalidation

### Changelog 1.16.5-6.1.7.12 (2021-04-22)
* Fix network packet security leaks
* Fix super admin can't transfer ownership to self

### Changelog 1.16.5-6.1.6.11 (2021-01-20)
* Make flux plugs and points waterlogged
* Fix server config not synced to client
* Fix possible crash with some blocks

### Changelog 1.16.4-6.1.5.10 (2020-11-14)
* Add feedback text animation
* Move super admin text from action bar into GUI
* Disable players to be charged by multiple networks
* Fix GUI not closed when block removed
* Fix possible strong reference bug
* Make devices able to disconnect in any case

### Changelog 1.16.4-6.1.3.8 (2020-11-10)
* Fix chunk loading flag not updated when re-entering world

### Changelog 1.16.4-6.1.2.7 (2020-11-10)
* Update localization
* Improve GUI
* Remove debug logs

### Changelog 1.16.4-6.1.1.6 (2020-11-08)
* Fix various GUI bugs
* Optimize GUI code

### Changelog 1.16.4-6.1.0.5 (2020-11-06)
* Update to 1.16.4 (1.16.3 works as well)
* Add energy capacity tooltip for storages
* Improve text display
* Optimize GUI code
* Fix possible crash
* Fix diffuse lighting in GUI
* Fix flux storage rendering
* Fix network buffer limiter

### Changelog 1.16.3-6.0.1.4 (2020-10-26)
* Improve controller transfer logic
* Fix crash caused by bad API implementations of other mods
* Fix client only method crash

### Changelog 1.16.3-6.0.0.3 (2020-10-25)
* Fix some packets without security check
* Fix chunk loading system
* Fix blacklist system
* Fix many other bugs
* Add support for Curios API
* Enhance flux storage capacity limit to Long.MAX_VALUE
* Optimize data synchronization and client cache
* Optimize network transfer
* More code standardizing and optimization

#### Changelog 5.0.3

* Bug Fixed: Storage render glitch after reducing the capacity
* Bug Fixed: Client flux networks cache won't be cleared when logging out a server
* Added: Lava particles when creating flux and there's a chance to turn obsidian into cobblestone

#### Changelog 5.0.1

* Improvement: Clicking on "Please select a network" will take you to the Network Selection tab
* Improvement: All "Navigation Prompts" will redirect you to the suggested tab
* Feature Fixed: Chunk Loading is now working again
* Bug Fixed: GUI's would crash when opened, or when sending packets - Client + Server Crash

#### Changelog 5.0.0

* Warning: Chunk loading won't work for now
* Warning: Blacklisting won't work for now
* Warning: IC2 + Gregtech are currently not supported, until they port
* Added: Extra recipe for Flux - crushing Redstone with Obsidian on a Flux Block
* Added: Capability for Energy Transfers up to MAX_LONG - this requires mods adding support
* Improvement: JEI integration, is now animated and works with texture packs
* Improvement: Pop ups now have a cleaner look
* Improvement: Flux Storage rendering will now have better performance
* Improvement: Packet Optimizations
* Minor: Fixed typos in Flux tool tip
* Bug Fixed: Flux Plugs would sometimes ignore their Transfer Limit when transferring from the buffer

------

PORTED TO 1.15.2

------

#### Changelog 4.1.1.34 (2021-01-23)

* Backport the new energy transfer system from 1.16.5 to 1.12.2: 
  including two-way buffering, enhancing GTEU transfer, it now can transfer
  almost infinite vols and amps as long as you can supply enough energy,
  but no more than the max voltage or amperage of machines can accept
* Improvement: JEI animated recipe view and works with resource packs
* Improvement: Flux Storage rendering will now have better performance
* Improvement: More understandable tooltips for plugs and points
* Balancing: Disable players to be charged by multiple networks
* Minor: Cleanup code, update Forge to 2854, update to ForgeGradle 3

#### Changelog 4.0.15 (1.12.2)

* Bug Fixed: Storage render glitch after reducing the capacity
* Added: New tooltips for flux connectors
* Added: Brazilian Portuguese pt_BR.lang (Focamacho)
* Updated: zh_CN.lang, ru_RU.lang (Smollet777)

#### Changelog 4.0.14

* Improvement: Recuded storage render lag
* Improvement: Add back old flux recipe (in config)
* Bug Fixed: Flux point can't transfer over 2^31-1 RF/t

#### Changelog 4.0.13

* Added: Integration with OpenComputers
* Added: Item blacklist with metadata

#### Changelog 4.0.12

* Added: GUIs for Configurator & Admin Configurator
* Added: Average Tick Statistic to Network Statistics tab for checking network performance.
* Improvement: Super Admin, is now enabled inside the Admin Configurator's GUI
* Improvement: Added config option to change the OP level required to activate Super Admin.
* Improvement: Wireless Charging GUI is now more in-keeping with the network color theme.
* Improvement: Some GUI Optimisations
* Bug Fixed: Configurator copying priority settings incorrectly
* Bug Fixed: Configurator not pasting settings Flux Storage

#### Changelog 4.0.11

* Added: Integration with The One Probe - Configurable in the config
* Added: Integration with JEI for showing flux recipe
* Added: de_DE localization (Caaruzo)

#### Changelog 4.0.10

* Improvement: Set limit and surge for flux storage
* Improvement: New feedback when batch editing
* Bug Fixed: A serious bug in server
* Bug Fixed: Render glitch in statistics tab sometimes
* Bug Fixed: Incorrect statistics data
* Bug Fixed: Incorrect energy data in connections tab

#### Changelog 4.0.9

* New: Animated chart for network statistics interface
* New: Add back block blacklist with metadata
* Improvement: Once again performance improvement
* Improvement: Some GUI adjustments
* Bug Fixed: Completely fixed GT transfer bugs
* Bug Fixed: Not update client name cache after being edited

#### Changelog 4.0.8

* New: Pages label button and can jump to hovered page directly
* New & Improvement: More smart network members setting
* Improvement: Keep old data file
* Improvement: Some GUI adjustments
* Improvement: A config for the amount of max forced chunks
* Bug Fixed: GT transfer bugs *(still not connect to wires now, you should connect to a transformer or battery buffer)
* Bug Fixed: Not update client color cache after being edited
* Bug Fixed: Size of multi-page GUI elements check
* Bug Fixed: Many...

#### Changelog 4.0.7

* A great performance improvement.
* ALL GUIs has completely reworked.
* Added support for GregTech Community Edition EU.
* Added ability for flux points/plugs to work as chunk loaders themselves.
* Added ability to batch edit network connections in connections tab.
* Added ability to set negative priority for flux points/plugs.
* Added ability to configure wireless charging in all network connectors.
* Added a way to clear NBT data by crafting again.
* Added ingame mod configuration and no need to restart.
* Added network color buttons to select easily and can right click to customize.
* Added new gui button sound effects.
* Changed the usage of admin configurator to super admin permission.
* Changed network security type to password verification system.
* Changed default flux recipe to be more mechanized.
* Enhanced flux tiles security that they can't be modified without permission.
* Enhanced and optimized network packets that update all data more effective.
* Enhanced custom network color editing that make it within proper range.
* Reduced unnecessary nbt to stack flux items as much as possible.
* Optimized and weakened wireless charging and fixed some bugs.
* Unified all types of energy and no longer support single type of energy transfer.
* Fixed flux storage rendering which they looks same with different level in inventory.
* Fixed crashing with AE energy cell, and no longer directly support AE2.
* Fixed item duplication when using displacement wand.
* Removed support for Tesla.
* Removed redstone control.
* Removed blacklist temporarily.