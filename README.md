# NPCLib
API for create NPCs without nametags in Spigot/Bukkit. It's compatible with 1.15



## Examples
```java
//First create the skin
NPCSkin skin = NPCSkinBuilder.fromUsername(this.plugin, p.getName());
NPCSkin skin = NPCSkinBuilder.fromUUID(this.plugin, p.getUniqueId().toString());
NPCSkin skin = NPCSkinBuilder.fromMineskin(this.plugin, 131234);

//Now create the NPC ussing the skin
NPC npc = NPCLib.createNPC(main.getPlugin(), p.getLocation(), skin);


