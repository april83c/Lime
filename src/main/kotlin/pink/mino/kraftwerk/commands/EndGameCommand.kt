package pink.mino.kraftwerk.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Activity
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.Stats
import java.awt.Color

class EndGameCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff")) {
                Chat.sendMessage(sender, "${Chat.prefix} &cYou don't have permission to use this command.")
                return false
            }
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "&cYou can't do this right now.")
            return false
        }
        val winners = SettingsFeature.instance.data!!.getStringList("game.winners")
        if (winners.isEmpty()) {
            Chat.sendMessage(sender, "&cYou have no winners set! You need to set them using /winner <player>!")
            return false
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv load Spawn")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv load Arena")
        for (player in Bukkit.getOnlinePlayers()) {
            if (winners.contains(player.name)) {
                player.sendTitle(Chat.colored("&6&lVICTORY!"), Chat.colored("&7Congratulations, you won the game!"))
                Stats.addWin(player)
                Chat.sendMessage(player, "&b&oSuccessfully saved your stats...")

            } else {
                player.sendTitle(Chat.colored("&c&lGAME OVER!"), Chat.colored("&7The game has concluded!"))
            }
        }
        val host = Bukkit.getPlayer(SettingsFeature.instance.data!!.getString("game.host"))
        val embed = EmbedBuilder()
        embed.setColor(Color(255, 61, 61))
        embed.setTitle(SettingsFeature.instance.data!!.getString("matchpost.host"))
        embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
        embed.addField("Winners", winners.joinToString(",", "", "", -1, "...") {
            "**$it** [${
                SettingsFeature.instance.data!!.getInt(
                    "game.kills.${Bukkit.getPlayer(it)}"
                )
            }]"
        }, false)
        embed.addField("Matchpost", "https://hosts.uhc.gg/m/${SettingsFeature.instance.data!!.getInt("matchpost.id")}", false)
        Discord.instance!!.getTextChannelById(937811334106583040)!!.sendMessageEmbeds(embed.build()).queue()
        Discord.instance!!.presence.activity = Activity.playing("applejuice.bar")
        SettingsFeature.instance.data!!.set("game.winners", ArrayList<String>())
        SettingsFeature.instance.data!!.set("game.list", ArrayList<String>())
        SettingsFeature.instance.data!!.set("game.kills", null)
        SettingsFeature.instance.data!!.set("matchpost.opens", null)
        SettingsFeature.instance.saveData()
        Bukkit.broadcastMessage(Chat.colored(Chat.line))
        for (player in Bukkit.getOnlinePlayers()) {
            SpawnFeature.instance.send(player)
            Chat.sendCenteredMessage(player, "&c&lGAME OVER!")
            Chat.sendMessage(player, " ")
            Chat.sendCenteredMessage(player, "&7Congratulations to the winners: &f${winners.joinToString(", ")}&7!")
            Chat.sendCenteredMessage(player, "&7The server will restart in &f45 seconds&7.")
        }
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart")
        }, 900L)
        Bukkit.broadcastMessage(Chat.colored(Chat.line))
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl off")
        return true
    }

}