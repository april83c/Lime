package pink.mino.kraftwerk.features.options

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk


class ConfigOptionHandler {
    companion object {
        var configOptions = ArrayList<ConfigOption>()

        fun setup() {
            addOption(AbsorptionOption())
            addOption(AntiStoneOption())
            addOption(GoldenHeadsOption())
            addOption(NotchAppleOption())
            addOption(HorsesOption())
            addOption(SplitEnchantsOption())
            addOption(BookshelvesOption())
            configOptions.sortWith(Comparator.comparing(ConfigOption::name))
        }

        fun getOptions(): ArrayList<ConfigOption> {
            return configOptions
        }

        fun getOption(id: String?): ConfigOption? {
            for (configOption in configOptions) {
                if (configOption.id == id) {
                    return configOption
                }
            }
            return null
        }

        private fun addOption(configOption: ConfigOption) {
            configOptions.add(configOption)
            Bukkit.getPluginManager().registerEvents(configOption, JavaPlugin.getPlugin(Kraftwerk::class.java))
            if (configOption.command) {
                JavaPlugin.getPlugin(Kraftwerk::class.java).getCommand(configOption.commandName).executor = configOption.executor
            }
        }
    }
}