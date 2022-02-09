package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class UHCFeature : Listener {
    fun start(mode: String) {
        when (mode) {
            "ffa" -> {
                GameState.setState(GameState.WAITING)
                Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")).time = 1000
                Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")).setGameRuleValue("doDaylightCycle", false.toString())
                SettingsFeature.instance.data!!.set("game.pvp", true)
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Starting a &cFFA&7 UHC game... now freezing players."))
                for (player in Bukkit.getOnlinePlayers()) {
                    player.playSound(player.location, Sound.WOOD_CLICK, 10F, 1F)
                    player.health = 20.0
                    player.foodLevel = 20
                    player.saturation = 20F
                    player.gameMode = GameMode.SURVIVAL
                    player.inventory.clear()
                    player.inventory.armorContents = null
                    player.itemOnCursor = ItemStack(Material.AIR)
                    val openInventory = player.openInventory
                    if (openInventory.type == InventoryType.CRAFTING) {
                        openInventory.topInventory.clear()
                    }
                    val effects = player.activePotionEffects
                    for (effect in effects) {
                        player.removePotionEffect(effect.type)
                    }
                }
                freeze()
                ScatterFeature.scatter("ffa", Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")), SettingsFeature.instance.data!!.getInt("pregen.border"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel")
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 45 &cStarting in ${Chat.dash}&f")
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cc")
                    unfreeze()
                    Bukkit.broadcastMessage(Chat.colored(Chat.line))
                    for (player in Bukkit.getOnlinePlayers()) {
                        Chat.sendCenteredMessage(player, "&c&lUHC")
                    }
                    Bukkit.broadcastMessage(" ")
                    GameState.setState(GameState.INGAME)
                    for (player in Bukkit.getOnlinePlayers()) {
                        Chat.sendCenteredMessage(player, "&7You may &abegin&7! The host for this game is &c${SettingsFeature.instance.data!!.getString("game.host")}&7!")
                        Chat.sendCenteredMessage(player, "&7Scenarios: &fcoming soon lol&7")
                        Chat.sendCenteredMessage(player, " ")
                        Chat.sendCenteredMessage(player, "&cFinal Heal&7 is in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal")} minutes&7.")
                        Chat.sendCenteredMessage(player, "&cPvP&7 is enabled in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal") + SettingsFeature.instance.data!!.getInt("game.events.pvp")} minutes&7.")
                        Chat.sendCenteredMessage(player, "&7Finally, &cMeetup&7 is enabled in &c${SettingsFeature.instance.data!!.getInt("game.events.final-heal") + SettingsFeature.instance.data!!.getInt("game.events.pvp") + SettingsFeature.instance.data!!.getInt("game.events.meetup")} minutes&7.")
                        player.playSound(player.location, Sound.ENDERDRAGON_GROWL, 10F, 1F)
                    }
                    Bukkit.broadcastMessage(Chat.colored(Chat.line))
                    Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")).setGameRuleValue("doDaylightCycle", true.toString())
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel")
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer ${SettingsFeature.instance.data!!.getInt("game.events.final-heal") * 60} &cFinal Heal is in ${Chat.dash}&f")
                    for (player in Bukkit.getOnlinePlayers()) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 100, true, true))
                    }
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        for (player in Bukkit.getOnlinePlayers()) {
                            player.health = player.maxHealth
                            player.foodLevel = 20
                            player.saturation = 20F
                            Chat.sendMessage(player, Chat.line)
                            Chat.sendCenteredMessage(player, "&c&lUHC")
                            Chat.sendMessage(player, " ")
                            Chat.sendCenteredMessage(player, "&7All players have been healed & fed.")
                            Chat.sendCenteredMessage(player, "&cPvP&7 is enabled in &c${SettingsFeature.instance.data!!.getString("game.events.pvp")} minutes&7.")
                            Chat.sendMessage(player, Chat.line)
                            player.playSound(player.location, Sound.BURP, 10F, 1F)
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel")
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer ${SettingsFeature.instance.data!!.getInt("game.events.pvp") * 60} &cPvP is enabled in ${Chat.dash}&f")
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            SettingsFeature.instance.data!!.set("game.pvp", false)
                            for (player in Bukkit.getOnlinePlayers()) {
                                Chat.sendCenteredMessage(player, "&c&lUHC")
                                Chat.sendMessage(player, " ")
                                Chat.sendCenteredMessage(player, "&7PvP has been &aenabled&7.")
                                Chat.sendCenteredMessage(player, "&cMeetup&7 is enabled in &c${SettingsFeature.instance.data!!.getString("game.events.meetup")} minutes&7.")
                                Chat.sendMessage(player, Chat.line)
                                player.playSound(player.location, Sound.ANVIL_LAND, 10F, 1F)
                            }
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel")
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer ${SettingsFeature.instance.data!!.getInt("game.events.meetup") * 60} &cMeetup happens in ${Chat.dash}&f")
                        }, (SettingsFeature.instance.data!!.getInt("game.events.pvp") * 60) * 20.toLong())
                    }, (SettingsFeature.instance.data!!.getInt("game.events.final-heal") * 60) * 20.toLong())
                }, 900L)
            }
            "teams" -> {

            }
            else -> {

            }
        }
    }

    fun freeze() {
        for (player in Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 999999999, 10, true, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 999999999, 100, true, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 999999999, -100, true, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999999, 1000, true, false))
        }
    }

    fun unfreeze() {
        for (player in Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.SLOW)
            player.removePotionEffect(PotionEffectType.JUMP)
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
            player.removePotionEffect(PotionEffectType.BLINDNESS)
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (GameState.currentState == GameState.WAITING) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (GameState.currentState == GameState.WAITING) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (e.entity.world.name != "Arena") return
        if (e.entityType === EntityType.PLAYER && e.damager != null && e.damager.type === EntityType.ARROW && (e.damager as Arrow).shooter === e.entity) {
            e.isCancelled = true
        }
        if (e.damager.type == EntityType.PLAYER) {
            if ((e.damager as Player).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                (e.damager as Player).removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
            }
        }
    }
}