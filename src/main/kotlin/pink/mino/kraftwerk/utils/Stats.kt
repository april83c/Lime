package pink.mino.kraftwerk.utils

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import com.mongodb.client.model.Sorts.descending
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.profiles.ProfileRepository
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Cache
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Caffeine
import me.lucko.helper.utils.Log
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import java.util.*
import java.util.concurrent.TimeUnit
class StatsPlayer(val player: OfflinePlayer) : Listener {
    var diamondsMined = 0
    var ironMined = 0
    var goldMined = 0

    var gamesPlayed = 0
    var kills = 0
    var wins = 0
    var deaths = 0

    var gapplesEaten = 0
    var timesCrafted = 0
    var timesEnchanted = 0
}

class Leaderboards : BukkitRunnable() {
    var timer = 1
    val plugin = JavaPlugin.getPlugin(Kraftwerk::class.java)
    val gamesPlayed =
        HologramsAPI.createHologram(plugin, Location(Bukkit.getWorld("Spawn"), -697.5, 108.5, 278.5))
    val wins =
        HologramsAPI.createHologram(plugin, Location(Bukkit.getWorld("Spawn"), -696.5, 108.5, 281.5))
    val kills =
        HologramsAPI.createHologram(plugin, Location(Bukkit.getWorld("Spawn"), -695.5, 108.5, 284.5))
    val diamondsMined =
        HologramsAPI.createHologram(plugin, Location(Bukkit.getWorld("Spawn"), -695.5, 108.5, 287.5))
    val goldMined = HologramsAPI.createHologram(
        plugin,
        Location(Bukkit.getWorld("Spawn"), -695.5, 108.5, 290.5)
    )
    val gapplesEaten = HologramsAPI.createHologram(
        plugin,
        Location(Bukkit.getWorld("Spawn"), -696.5, 108.5, 293.5)
    )
    val timesEnchanted = HologramsAPI.createHologram(
        plugin,
        Location(Bukkit.getWorld("Spawn"), -697.5, 108.5, 296.5)
    )

    override fun run() {
        timer -= 1
        if (timer == 0) {
            try {
                for (hologram in HologramsAPI.getHolograms(plugin)) {
                    hologram.clearLines()
                }
                gamesPlayed.appendTextLine(Chat.colored("&c&lGames Played"))
                gamesPlayed.appendTextLine(Chat.guiLine)
                wins.appendTextLine(Chat.colored("&c&lWins"))
                wins.appendTextLine(Chat.guiLine)
                kills.appendTextLine(Chat.colored("&c&lKills"))
                kills.appendTextLine(Chat.guiLine)
                diamondsMined.appendTextLine(Chat.colored("&c&lDiamonds Mined"))
                diamondsMined.appendTextLine(Chat.guiLine)
                goldMined.appendTextLine(Chat.colored("&c&lGold Mined"))
                goldMined.appendTextLine(Chat.guiLine)
                gapplesEaten.appendTextLine(Chat.colored("&c&lGapples Eaten"))
                gapplesEaten.appendTextLine(Chat.guiLine)
                timesEnchanted.appendTextLine(Chat.colored("&c&lTimes Enchanted"))
                timesEnchanted.appendTextLine(Chat.guiLine)
                with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("stats")) {
                    val gp = this.find().sort(descending("gamesPlayed")).limit(10)
                    for ((index, document) in gp.withIndex()) {
                        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).getService(ProfileRepository::class.java).lookupProfile(document["uuid"] as UUID).get()
                        if (document["gamesPlayed"] as Int != 0) gamesPlayed.appendTextLine(Chat.colored("&e${index + 1}. &f${profile.name.get()} &8- &b${document["gamesPlayed"] as Int}"))
                    }
                    if (gamesPlayed.size() == 2) {
                        gamesPlayed.appendTextLine(Chat.colored("&7No data yet x_x"))
                    }
                    val w = this.find().sort(descending("wins")).limit(10)
                    for ((index, document) in w.withIndex()) {
                        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).getService(ProfileRepository::class.java).lookupProfile(document["uuid"] as UUID).get()
                        if (document["wins"] as Int != 0) wins.appendTextLine(Chat.colored("&e${index + 1}. &f${profile.name.get()} &8- &b${document["wins"] as Int}"))
                    }
                    if (wins.size() == 2) {
                        wins.appendTextLine(Chat.colored("&7No data yet x_x"))
                    }
                    val k = this.find().sort(descending("kills")).limit(10)
                    for ((index, document) in k.withIndex()) {
                        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).getService(ProfileRepository::class.java).lookupProfile(document["uuid"] as UUID).get()
                        if (document["kills"] as Int != 0) kills.appendTextLine(Chat.colored("&e${index + 1}. &f${profile.name.get()} &8- &b${document["kills"] as Int}"))
                    }
                    if (kills.size() == 2) {
                        kills.appendTextLine(Chat.colored("&7No data yet x_x"))
                    }
                    val d = this.find().sort(descending("diamondsMined")).limit(10)
                    for ((index, document) in d.withIndex()) {
                        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).getService(ProfileRepository::class.java).lookupProfile(document["uuid"] as UUID).get()
                        if (document["diamondsMined"] as Int != 0) diamondsMined.appendTextLine(Chat.colored("&e${index + 1}. &f${profile.name.get()} &8- &b${document["diamondsMined"] as Int}"))
                    }
                    if (diamondsMined.size() == 2) {
                        diamondsMined.appendTextLine(Chat.colored("&7No data yet x_x"))
                    }
                    val g = this.find().sort(descending("gapplesEaten")).limit(10)
                    for ((index, document) in g.withIndex()) {
                        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).getService(ProfileRepository::class.java).lookupProfile(document["uuid"] as UUID).get()
                        if (document["gapplesEaten"] as Int != 0) gapplesEaten.appendTextLine(Chat.colored("&e${index + 1}. &f${profile.name.get()} &8- &b${document["gapplesEaten"] as Int}"))
                    }
                    if (gapplesEaten.size() == 2) {
                        gapplesEaten.appendTextLine(Chat.colored("&7No data yet x_x"))
                    }
                    val t = this.find().sort(descending("timesEnchanted")).limit(10)
                    for ((index, document) in t.withIndex()) {
                        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).getService(ProfileRepository::class.java).lookupProfile(document["uuid"] as UUID).get()
                        if (document["timesEnchanted"] as Int != 0) timesEnchanted.appendTextLine(Chat.colored("&e${index + 1}. &f${profile.name.get()} &8- &b${document["timesEnchanted"] as Int}"))
                    }
                    if (timesEnchanted.size() == 2) {
                        timesEnchanted.appendTextLine(Chat.colored("&7No data yet x_x"))
                    }
                    val gm = this.find().sort(descending("goldMined")).limit(10)
                    for ((index, document) in gm.withIndex()) {
                        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).getService(ProfileRepository::class.java).lookupProfile(document["uuid"] as UUID).get()
                        if (document["goldMined"] as Int != 0) goldMined.appendTextLine(Chat.colored("&e${index + 1}. &f${profile.name.get()} &8- &b${document["goldMined"] as Int}"))
                    }
                    if (goldMined.size() == 2) {
                        goldMined.appendTextLine(Chat.colored("&7No data yet x_x"))
                    }
                }
                gamesPlayed.appendTextLine(Chat.guiLine)
                wins.appendTextLine(Chat.guiLine)
                kills.appendTextLine(Chat.guiLine)
                diamondsMined.appendTextLine(Chat.guiLine)
                goldMined.appendTextLine(Chat.guiLine)
                gapplesEaten.appendTextLine(Chat.guiLine)
                timesEnchanted.appendTextLine(Chat.guiLine)
            } catch (e: MongoException) {
                e.printStackTrace()
            }
            timer = 300
        }
    }
}
class StatsHandler : Listener {
    private val statsPlayerMap: Cache<UUID, StatsPlayer> = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterAccess(6, TimeUnit.HOURS)
        .build()
    init {
        Events.subscribe(PlayerLoginEvent::class.java, EventPriority.MONITOR)
            .filter { it.result == PlayerLoginEvent.Result.ALLOWED }
            .handler { event ->
                val statsPlayer = StatsPlayer(event.player)
                updateCache(statsPlayer)
                Schedulers.async().run { loadPlayerData(statsPlayer) }
            }
        Events.subscribe(PlayerQuitEvent::class.java, EventPriority.MONITOR)
            .handler { event ->
                val statsPlayer = statsPlayerMap.getIfPresent(event.player.uniqueId)
                if (statsPlayer != null) {
                    Schedulers.async().run { savePlayerData(statsPlayer) }
                }
            }
    }

    fun savePlayerData(statsPlayer: StatsPlayer) {
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("stats")) {
                val filter = Filters.eq("uuid", statsPlayer.player.uniqueId)
                val document = Document("uuid", statsPlayer.player.uniqueId)
                    .append("diamondsMined", statsPlayer.diamondsMined)
                    .append("ironMined", statsPlayer.ironMined)
                    .append("goldMined", statsPlayer.goldMined)

                    .append("gamesPlayed", statsPlayer.gamesPlayed)
                    .append("kills", statsPlayer.kills)
                    .append("wins", statsPlayer.wins)
                    .append("deaths", statsPlayer.deaths)

                    .append("gapplesEaten", statsPlayer.gapplesEaten)
                    .append("timesCrafted", statsPlayer.timesCrafted)
                    .append("timesEnchanted", statsPlayer.timesEnchanted)
                this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
                Log.info("Saved stats for ${statsPlayer.player.name}")
            }
        } catch (e: MongoException) {
            Log.severe("Error saving player data for ${statsPlayer.player.name}", e)
        }
    }

    fun getStatsPlayer(player: Player): StatsPlayer? {
        Objects.requireNonNull(player, "player")
        return statsPlayerMap.getIfPresent(player.uniqueId)
    }

    fun lookupStatsPlayer(player: OfflinePlayer): StatsPlayer {
        Objects.requireNonNull(player, "player")
        val sPlayer = StatsPlayer(player)
        updateCache(sPlayer)
        loadPlayerData(sPlayer)
        return sPlayer
    }

    fun loadPlayerData(statsPlayer: StatsPlayer) {
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("stats")) {
                val playerData = find(Filters.eq("uuid", statsPlayer.player.uniqueId)).first()
                if (playerData != null) {
                    statsPlayer.diamondsMined = playerData.getInteger("diamondsMined")
                    statsPlayer.ironMined = playerData.getInteger("ironMined")
                    statsPlayer.goldMined = playerData.getInteger("goldMined")

                    statsPlayer.deaths = playerData.getInteger("deaths")
                    statsPlayer.kills = playerData.getInteger("kills")
                    statsPlayer.gamesPlayed = playerData.getInteger("gamesPlayed")
                    statsPlayer.wins = playerData.getInteger("wins")

                    statsPlayer.gapplesEaten = playerData.getInteger("gapplesEaten")
                    statsPlayer.timesCrafted = playerData.getInteger("timesCrafted")
                    statsPlayer.timesEnchanted = playerData.getInteger("timesEnchanted")
                    Log.info("Loaded stats for ${statsPlayer.player.name}.")
                } else {
                    Log.info("Could not load stats for ${statsPlayer.player.name}.")
                }
            }
        } catch (e: MongoException) {
            e.printStackTrace()
        }
    }

    private fun updateCache(statsPlayer: StatsPlayer) {
        this.statsPlayerMap.put(statsPlayer.player.uniqueId, statsPlayer)
    }
}