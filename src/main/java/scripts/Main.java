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

import java.util.*;
import java.util.stream.Collectors;

public final class Main extends JavaPlugin implements Listener {

    public Inventory Pointer;
    public List<Player> plays = new ArrayList<Player>();
    public List<Player> plays2 = new ArrayList<Player>();
    public List<Inventory> invs = new ArrayList<Inventory>();
    public Map<Player, Integer> invnumber = new HashMap<Player, Integer>();
    public Map<Integer, List<Inventory>> worldsInvs = new HashMap<Integer, List<Inventory>>();
    public Inventory inv;
    public Integer k = 0;
    public boolean ifManyInvs = false;
    public String name;
    public String previous;
    public String next;
    public String message;
    public String dontFind;
    public String notOnline;
    public String itemName;
    public int time;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        reload_config();
        this.getServer().getPluginManager().registerEvents(this, this);
        run();
    }


    public void reload_config() {
        name = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("name"));
        previous = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("previous"));
        next = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("next"));
        message = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("message"));
        dontFind = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("dontFind"));
        notOnline = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("notOnline"));
        itemName = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("itemName"));
        time = getConfig().getInt("time");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("getcompass")) {
            if (!(sender instanceof Player)) {
                System.out.println("Command not compatible with console");
                return true;
            }
            if (args.length == 0) {
                Player player = (Player) sender;
                player.getInventory().addItem(item());
                return true;
            }
            if (args.length > 1) {
                sender.sendMessage("wrong usage of the command. Try /getcompass <nick>");
            } else {
                Player p =  getServer().getPlayer(args[0]);
                if (p == null) {
                    sender.sendMessage(notOnline);
                    return true;
                }
                p.getInventory().addItem(item());
            }
        }
        return true;
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
        if (event.getView().getTitle().equals(name)) {
            if (event.getCurrentItem() != null) {
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
                    player.sendMessage(dontFind);
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
                player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.UNDERLINE + message + player2.getDisplayName());
                player.closeInventory();
                player.setCompassTarget(player2.getLocation());
                plays.add(player);
                plays2.add(player2);
            }
        }


        if (ifManyInvs) {
            for (int i = 0; i != invs.size(); i++) {
                if (invs.contains(event.getInventory())) {
                    if (event.getSlot() > 44) {
                        event.setCancelled(true);
                        Player player = (Player) event.getWhoClicked();
                        if (event.getSlot() == 45 && invnumber.get(player) - 1 != -1) {
                            invnumber.put(player, invnumber.get(player) - 1);
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().openInventory(worldsInvs.get(i).get(invnumber.get(player)));

                        } else if (event.getSlot() == 53 && invnumber.get(player) + 1 <= invs.size() - 1) {
                            invnumber.put(player, invnumber.get(player) + 1);
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().openInventory(worldsInvs.get(i).get(invnumber.get(player)));
                        }
                    }
                }
            }
        } else if (event.getInventory() == inv) {

        }


        }


    public ItemStack item() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.ITALIC + "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + itemName);
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Legendary IV");
        meta.setUnbreakable(true);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }


    public Inventory createInv(List<Inventory> invent) {
        Inventory inv = Bukkit.createInventory(null, 54,
                ChatColor.DARK_PURPLE + name + (invent.size() + 1) + "/" + invent.size() + 1);

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + previous);
        item.setItemMeta(meta);
        inv.setItem(45, item);

        meta.setDisplayName(ChatColor.GOLD + next);
        item.setItemMeta(meta);
        inv.setItem(53, item);

        item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        meta = item.getItemMeta();
        meta.setDisplayName(" ");
        inv.setItem(52, item);
        inv.setItem(51, item);
        inv.setItem(50, item);
        inv.setItem(49, item);
        inv.setItem(48, item);
        inv.setItem(47, item);
        inv.setItem(46, item);
        return inv;
    }


    private void createInventory(Player player) {
        ifManyInvs = false;
        if (getServer().getOnlinePlayers().size() > 10)  {
            Inventory inv = Bukkit.createInventory(null, 9, name);
        } else if (getServer().getOnlinePlayers().size() > 19) {
            Inventory inv = Bukkit.createInventory(null, 18, name);
        } else if (getServer().getOnlinePlayers().size() > 28) {
            Inventory inv = Bukkit.createInventory(null, 27, name);
        } else if (getServer().getOnlinePlayers().size() > 37) {
            Inventory inv = Bukkit.createInventory(null, 36, name);
        } else if (getServer().getOnlinePlayers().size() > 46) {
            Inventory inv = Bukkit.createInventory(null, 45, name);
        } else if (getServer().getOnlinePlayers().size() > 55) {
            Inventory inv = Bukkit.createInventory(null, 54, name);
        } else if (getServer().getOnlinePlayers().size() < 55) {
            Inventory inv = createInv(invs);
            int x = 0;
            for (int i = 0; i <= player.getWorld().getPlayers().size() - 1; i++) {
                x++;
                if (x == 45) {
                    x = 0;
                    invs.add(inv);
                    inv.clear();
                }
                ItemStack item = getPlayerHead(player.getWorld().getPlayers().get(i).getName());
                inv.setItem(x, item);
            }
            ifManyInvs = true;
        }


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
        }.runTaskTimer(this, 0, time);
    }
}












