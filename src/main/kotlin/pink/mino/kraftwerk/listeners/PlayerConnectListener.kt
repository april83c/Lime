package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

class PlayerConnectListener : Listener {
    @EventHandler
    fun onPlayerConnect(e: PlayerLoginEvent) {
        val player = e.player
        if (SettingsFeature.instance.data!!.getBoolean("whitelist.enabled")) {
            if (!SettingsFeature.instance.data!!.getList("whitelist.list").contains(player.name.lowercase())) {
                if (!player.hasPermission("uhc.staff")) {
                    e.kickMessage = Chat.colored("&cYou aren't allowed to join while the whitelist is on.")
                    e.disallow(e.result, "You are not allowed to join while the whitelist is on!")
                } else {
                    e.allow()
                }
            } else {
                e.allow()
            }
        } else {
            e.allow()
        }
    }
}