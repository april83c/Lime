package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import pink.mino.kraftwerk.utils.Chat

class ServerListPingListener : Listener {
    @EventHandler
    fun onServerListPing(e: ServerListPingEvent) {
        val text = Chat.colored("        &l&aRaimu&r - &fa 1.8.x Reddit-UHC server&r\n      &7join our discord: &9&nraimu.space/discord")
        e.motd = text
    }
}