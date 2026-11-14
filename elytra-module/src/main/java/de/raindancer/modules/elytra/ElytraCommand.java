package de.raindancer.modules.elytra;

import de.raindancer.modules.elytra.look.ElytraLook;
import de.raindancer.modules.elytra.look.LooksMenu;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/** {@code /elytra} opens the looks; {@code /elytra look <name|none>} puts one on without the menu. Open to all: who may wear one is {@link ElytraServices#mayWear}. */
public final class ElytraCommand implements BasicCommand {

    private final Supplier<ElytraServices> services;

    public ElytraCommand(Supplier<ElytraServices> services) {
        this.services = services;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        ElytraServices live = services.get();
        if (!(source.getSender() instanceof Player player)) {
            live.messages().send(source.getSender(), "elytra.only-a-player");
            return;
        }
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("look"))) {
            new LooksMenu(live, player, null).open();
            return;
        }
        if (args.length != 2 || !args[0].equalsIgnoreCase("look")) {
            live.messages().send(player, "elytra.usage");
            return;
        }
        if (ElytraLook.isPlainWord(args[1])) {
            live.wear(player, null);
            return;
        }
        ElytraLook.parse(args[1]).ifPresentOrElse(look -> live.wear(player, look),
                () -> live.messages().send(player, "elytra.unknown-look", "typed", args[1], "looks",
                        String.join(", ", Arrays.stream(ElytraLook.values()).map(ElytraLook::id).toList())));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length <= 1) {
            options.add("look");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("look")) {
            Arrays.stream(ElytraLook.values()).map(ElytraLook::id).forEach(options::add);
            options.add("none");
            options.add("none");
        }
        String typed = args.length == 0 ? "" : args[args.length - 1].toLowerCase(Locale.ROOT);
        return options.stream().filter(option -> option.startsWith(typed)).toList();
    }
}
