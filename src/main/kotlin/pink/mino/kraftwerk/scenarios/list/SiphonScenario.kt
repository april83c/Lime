package pink.mino.kraftwerk.scenarios.list

import com.google.common.collect.ImmutableList
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState
import java.util.*
import kotlin.math.abs


class SiphonScenario : Scenario(
  "Siphon",
  "Whenever you get a kill, you will regenerate 2 hearts, gain 2 levels and get a random tier 1 enchanted book",
  "siphon",
  Material.POTION

) {
    val enchants: ImmutableList<Enchantment> = ImmutableList.of(
        Enchantment.DIG_SPEED,
        Enchantment.DURABILITY,
        Enchantment.PROTECTION_PROJECTILE,
        Enchantment.PROTECTION_ENVIRONMENTAL,
        Enchantment.DAMAGE_ALL
    )

    fun getRandom(from: Int, to: Int): Int {
        return if (from < to) from + Random().nextInt(abs(to - from)) else from - Random().nextInt(abs(to - from))
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (!enabled) return
        val killer: Player = event.entity.killer ?: return
        killer.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 100, 1, true, true))
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val meta = item.itemMeta as EnchantmentStorageMeta
        meta.addStoredEnchant(enchants[Random().nextInt(enchants.size)], getRandom(1, 3), true)
        item.itemMeta = meta
        killer.inventory.addItem(item)
        killer.level = killer.level + 2
    }

}