package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import pink.mino.kraftwerk.utils.Chat

class ServerListPingListener : Listener {
    @EventHandler
    fun onServerListPing(e: ServerListPingEvent) {
        val text = Chat.colored("&a&lraimu&7&l.space &8(&a1.8.x&8)\n&oReddit UHC server")
        e.motd = text
    }
}