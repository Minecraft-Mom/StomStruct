package mom.minecraft.momstruct.extension.Commands;

import mom.minecraft.momstruct.core.structure.AbstractStructure;
import mom.minecraft.momstruct.core.structure.VanillaStructure;
import mom.minecraft.momstruct.extension.MomStructExtension;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeBlockPosition;
import net.minestom.server.entity.Player;

public class StructureCommand extends Command {
    public StructureCommand() {
        super("structure", "struct");

        ArgumentEnum<actions> actionArgument = ArgumentType.Enum("action", actions.class);
        actionArgument.setFormat(ArgumentEnum.Format.LOWER_CASED);
        ArgumentString pathArgument = ArgumentType.String("path");
        ArgumentRelativeBlockPosition pos1Argument = ArgumentType.RelativeBlockPosition("pos1");
        ArgumentRelativeBlockPosition pos2Argument = ArgumentType.RelativeBlockPosition("pos2");


        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage("Structures directory: \"" + VanillaStructure.STRUCTURE_DIRECTORY + "\"");
            sender.sendMessage("/" + context.getCommandName() + " load <path>: Load a structure file to your clipboard");
            sender.sendMessage("/" + context.getCommandName() + " save: Save structure from your clipboard to file");
            sender.sendMessage("/" + context.getCommandName() + " build: Build structure from your clipboard");
            sender.sendMessage("/" + context.getCommandName() + " info: View details of structure from your clipboard");
            sender.sendMessage("/" + context.getCommandName() + " create <path> <pos1> <pos2>: New structure from instance");
        }));

        addSyntax(((sender, context) -> {
            Player player = sender.asPlayer();

            switch(context.get(actionArgument)) {
                case INFO:
                    if (MomStructExtension.playerLoadedStructures.containsKey(player.getUuid())) {
                        VanillaStructure structure = MomStructExtension.playerLoadedStructures.get(player.getUuid());
                        player.sendMessage("Structure Info:");
                        player.sendMessage("    File: " + structure.getStructureFilePath());
                        player.sendMessage("  Status: " + structure.getStatus());
                        player.sendMessage("    Size: " + structure.getWidth() + ", " + structure.getHeight() + ", " + structure.getLength());
                        player.sendMessage("  Blocks: " + structure.getBlockCount());
                        player.sendMessage("Palettes: " + structure.getPaletteCount());
                        player.sendMessage(" Version: " + structure.getDataVersion());
                    }
                    break;

                case BUILD:
                    if (MomStructExtension.playerLoadedStructures.containsKey(player.getUuid())) {
                        VanillaStructure structure = MomStructExtension.playerLoadedStructures.get(player.getUuid());
                        if (structure.getStatus() != AbstractStructure.Status.LOADED)
                            player.sendMessage("Error: Structure Status: " + structure.getStatus());
                        else
                        {
                            structure.build(player.getPosition(), player.getInstance());
                        }
                    }
                    break;

                case SAVE:
                    if (MomStructExtension.playerLoadedStructures.containsKey(player.getUuid())) {
                        VanillaStructure structure = MomStructExtension.playerLoadedStructures.get(player.getUuid());
                        if (structure.getStatus() != AbstractStructure.Status.LOADED)
                            player.sendMessage("Error: Structure Status: " + structure.getStatus());
                        else
                        {
                            structure.write();
                            if (structure.getStatus() == AbstractStructure.Status.BAD_WRITE)
                                player.sendMessage("Error: Structure Status: " + structure.getStatus());
                            else
                                player.sendMessage("Structure written to disk");
                        }
                    }
                    break;
                case CREATE:
                case LOAD:
                    player.sendMessage("Invalid Command Syntax");
                    break;
            }
        }), actionArgument);

        addSyntax((sender, context) -> {
            Player player = sender.asPlayer();

            switch(context.get(actionArgument)) {
                case LOAD:
                    VanillaStructure structure = new VanillaStructure(context.get(pathArgument));
                    MomStructExtension.playerLoadedStructures.put(player.getUuid(), structure);
                    structure.read();
                    if (structure.getStatus() != AbstractStructure.Status.LOADED)
                        player.sendMessage("Error: Structure Status: " + structure.getStatus());
                    else
                        player.sendMessage("Structure loaded from disk");

                    break;

                case INFO:
                case BUILD:
                case CREATE:
                    player.sendMessage("Invalid Command Syntax");
                    break;
            }
        }, actionArgument, pathArgument);

        addSyntax((sender, context) -> {
            Player player = sender.asPlayer();

            switch (context.get(actionArgument)) {
                case CREATE:
                    VanillaStructure s = new VanillaStructure(
                            context.get(pos1Argument).from(player),
                            context.get(pos2Argument).from(player),
                            player.getInstance(),
                            context.get(pathArgument));
                    MomStructExtension.playerLoadedStructures.put(player.getUuid(), s);
                    break;

                case INFO:
                case BUILD:
                case LOAD:
                    player.sendMessage("Invalid Command Syntax");
                    break;
            }
        }, actionArgument, pathArgument, pos1Argument, pos2Argument);
    }

    public enum actions {
        LOAD,
        SAVE,
        BUILD,
        INFO,
        CREATE
    }
}
