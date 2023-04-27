package scripts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Main extends JavaPlugin implements Listener {

    public Inventory Pointer;
    public List<Player> plays = new ArrayList<Player>();
    public List<Player> plays2 = new ArrayList<Player>();
    public Integer k = 0;


    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(this, this);
        run();

    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("getcompass")) {
            if (!(sender instanceof Player)) {
                System.out.println("Command not conatible with console (dont judge my spelling)");
                return true;
            }
            Player player = (Player) sender;
            player.getInventory().addItem(item());
            return true;
        }
        return false;
    }


    @EventHandler
    public void clickitem(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COMPASS) &&
                    event.getPlayer().getInventory().getItemInMainHand().equals(item())) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    createInventory(event.getPlayer());
                    event.getPlayer().openInventory(Pointer);
                }
            }
        }
    }


    @EventHandler
    public void comes(PlayerQuitEvent event) {
        if (!plays.contains(event.getPlayer()))
            return;
        for (int i = 0; i <= plays.size() - 1; i++) {
            if (plays.get(i) == event.getPlayer()) {
                plays.remove(i);
                plays2.remove(i);
                return;
            }
        }
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Pointer"))
            return;
        if(event.getCurrentItem() == null)
            return;
        if(event.getCurrentItem().getItemMeta() == null)
            return;
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        if (event.getClickedInventory().getType() == InventoryType.PLAYER)
            return;

        k = -1;
        for (int i = 0; i <= player.getWorld().getPlayers().size() - 1; i++) {
            if (player.getWorld().getPlayers().get(i).getName().equals(event.getCurrentItem().getItemMeta().getDisplayName())) {
                k = i;
                break;
            }
        }

        if (k == -1) {
            player.sendMessage("sorry bro, didint found that guy");
            return;
        }

        Player player2 = player.getWorld().getPlayers().get(k);
        for (int i = 0; i <= plays.size() - 1; i++) {
            if (player == player2)
                return;
        }

        if (!plays.contains(player))
            for (int i = 0; i <= plays.size() - 1; i++) {
                if (plays.get(i) == player) {
                    plays.remove(i);
                    plays2.remove(i);
                    return;
                }
            }
        player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.UNDERLINE + "Scigasz " + player2.getDisplayName());
        player.closeInventory();
        player.setCompassTarget(player2.getLocation());
        plays.add(player);
        plays2.add(player2);
        return;
        }


    public ItemStack item() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.ITALIC + "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Legendarny mega compass");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "Nazywali mnie glupcem");
        lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "Bylem pokazywany palcem przez" );
        lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "tak zwanych filozofow i poetow." );
        lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "Lecz sie nie poddalem, znalazlem" );
        lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "wszystko co kiedykolwiek szukalem" );
        lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "Jestem prowadzony przez kompas" );
        lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "Jacka Wrobla." );
        lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "Co prowadzi do tego czego" );
        lore.add(ChatColor.ITALIC + "" + ChatColor.GOLD + "Najbardziej pragniesz" );
        lore.add(ChatColor.GRAY + "Legendarnosc IV");
        meta.setUnbreakable(true);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }


    private void createInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "Pointer");

        for (int i = 0; i <= player.getWorld().getPlayers().size() - 1; i++) {
            ItemStack item = getPlayerHead(player.getWorld().getPlayers().get(i).getName());
            inv.setItem(i, item);
        }
        Pointer = inv;
    }


    public ItemStack getPlayerHead(String player) {

        boolean isNewVersion = Arrays.stream(Material.values())
                .map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
        Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
        ItemStack item = new ItemStack(type, 1);

        if (!isNewVersion)
            item.setDurability((short) 3);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player);
        meta.setDisplayName(player);
        item.setItemMeta(meta);

        return item;
    }


    public void run() {
        new BukkitRunnable() {
            public void run() {
                for (int i = 0; i <= plays.size() - 1; i++) {
                    plays.get(i).setCompassTarget(plays2.get(i).getLocation());
                }
            }
        }.runTaskTimer(this, 0, 5);
    }
}
