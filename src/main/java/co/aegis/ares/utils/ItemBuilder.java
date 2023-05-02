package co.aegis.ares.utils;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static co.aegis.ares.utils.StringUtils.colorize;

public class ItemBuilder implements Cloneable, Supplier<ItemStack> {

    private ItemStack itemStack;
    private ItemMeta itemMeta;
    @Getter
    private final List<String> lore = new ArrayList<>();
    private boolean doLoreize = true;
    private boolean update;


    public ItemBuilder material(Material material) {
        itemStack.setType(material);
        itemMeta = itemStack.getItemMeta();
        return this;
    }

    public Material material() {
        return itemStack.getType();
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    @Override
    public ItemStack get() {
        return build();
    }

    public ItemStack build() {
        if (update) {
            buildLore();
            if (itemMeta != null)
                itemStack.setItemMeta(itemMeta);
            return itemStack;
        } else {
            ItemStack result = itemStack.clone();
            buildLore();
            if (itemMeta != null)
                result.setItemMeta(itemMeta);
            return result;
        }
    }

    public void buildLore() {
        if (lore.isEmpty())
            return; // don't override Component lore
        lore.removeIf(Objects::isNull);
        List<String> colorized = new ArrayList<>();
        for (String line : lore)
            if (doLoreize)
                colorized.addAll(StringUtils.loreize(colorize(line)));
            else
                colorized.add(colorize(line));
        itemMeta.setLore(colorized);

        itemStack.setItemMeta(itemMeta);
        itemMeta = itemStack.getItemMeta();
    }

}
