package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class MessageCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            Chat.sendMessage(sender as Player, "&cYou need a user to send a message to.")
            return false
        }
        if (args.size < 2) {
            Chat.sendMessage(sender as Player, "&cYou need a message to send to the user.")
            return false
        }

        var message = ""
        for (i in 1 until args.size) message += args[i] + " "
        val target = Bukkit.getPlayer(args[0])
        if (target == null) {
            Chat.sendMessage(sender as Player,"&cYou need a valid user to send this to.")
            return false
        }
        val player = sender as Player
        Chat.sendMessage(sender, "&7To: &f${target.displayName} &8- &7$message")
        Chat.sendMessage(target, "&7From: &f${player.displayName} &8- &7$message")
        player.playSound(player.location, Sound.NOTE_PLING, 10.toFloat(), 0.toFloat())
        target.playSound(player.location, Sound.NOTE_PLING, 10.toFloat(), 0.toFloat())
        return true
    }

}