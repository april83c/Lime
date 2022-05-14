package pink.mino.kraftwerk.features

import me.lucko.helper.utils.Log
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team


class TeamsFeature private constructor() {
    val sb: Scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    private val teams = ArrayList<Team>()
    val colors = ArrayList<String>()

    /**
     * Gets a list of all teams.
     * @return the list of teams.
     */
    fun getTeams(): List<Team> {
        return teams
    }

    /**
     * Leaves the current team of the player.
     * @param player the player leaving.
     */
    fun leaveTeam(player: Player) {
        if (getTeam(player) != null) {
            getTeam(player)!!.removePlayer(player)
        }
    }

    /**
     * Joins a team.
     * @param teamName the team joining.
     * @param player the player joining.
     */
    fun joinTeam(teamName: String?, player: Player?) {
        val team: Team = sb.getTeam(teamName)
        team.addPlayer(player)
    }

    fun deleteTeam(team: Team) {
        Log.info("Automatically deleted ${team.name} because it was empty.")
        teams.remove(team)
        team.unregister()
    }

    fun resetTeams() {
        for (team in teams) {
            team.unregister()
        }
        teams.clear()
    }

    fun createTeam(player: Player): Team {
        // Create a new team with name.
        val name = "UHC${teams.size + 1}"
        val team = sb.registerNewTeam(name)

        // Remove randomly selected color.
        if (colors.isEmpty()) setupColors()
        val color = colors.random()
        colors.remove(color)

        // Set up color & misc.
        team.prefix = color
        team.suffix = "§r"
        team.displayName = color + "Team #${teams.size + 1}"
        team.setAllowFriendlyFire(true)
        team.setCanSeeFriendlyInvisibles(true)

        // Add player.
        team.addPlayer(player)

        // Add to list.
        teams.add(team)
        Log.info("Created ${team.name} for ${player.name}.")
        return team
    }

    /**
     * Gets the team of a player.
     * @param player the player wanting.
     * @return The team.
     */
    fun getTeam(player: Player): Team? {
        return player.scoreboard.getPlayerTeam(player)
    }

    /**
     * Sets up all the teams.
     */
    fun setupColors() {
        for (color in ChatColor.values()) {
            if (color == ChatColor.MAGIC) continue
            if (color == ChatColor.RESET) continue
            if (color == ChatColor.STRIKETHROUGH) continue
            if (color == ChatColor.UNDERLINE) continue
            if (color == ChatColor.BOLD) continue
            if (color == ChatColor.ITALIC) continue
            colors.add(color.toString())
        }
        colors.shuffle()
        val li = ArrayList<String>()
        for (color in colors) {
            li.add(color + ChatColor.ITALIC)
            li.add(color + ChatColor.BOLD)
            li.add(color + ChatColor.UNDERLINE)
            li.add(color + ChatColor.ITALIC + ChatColor.BOLD)
            li.add(color + ChatColor.ITALIC + ChatColor.UNDERLINE)
            li.add(color + ChatColor.ITALIC + ChatColor.BOLD + ChatColor.UNDERLINE)
            li.add(color + ChatColor.BOLD + ChatColor.UNDERLINE)
        }
        li.shuffle()
        colors.addAll(li)
        Log.info("Setup teams colors.")
    }

    companion object {
        val manager = TeamsFeature()
    }
}