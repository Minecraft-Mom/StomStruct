package mom.minecraft.stomstruct.core.handlers;

import net.minestom.server.instance.block.BlockHandler;

/**
 * Contains references to default block handlers
 */
public class BlockHandlers {
    public static BlockHandler BannerHandler;
    public static BlockHandler EndGatewayHandler;
    public static BlockHandler MobSpawnerHandler;
    public static BlockHandler PistonHandler;
    public static BlockHandler SignHandler;
    public static BlockHandler SkullHandler;
    public static BlockHandler DummyHandler;

    static {
        BannerHandler = new BannerHandler();
        EndGatewayHandler = new EndGatewayHandler();
        MobSpawnerHandler = new MobSpawnerHandler();
        PistonHandler = new PistonHandler();
        SignHandler = new SignHandler();
        SkullHandler = new SkullHandler();
        DummyHandler = new DummyHandler();
    }

    public static BlockHandler fromBlockName(String name) {
        switch (name) {
            case "minecraft:white_banner":
            case "minecraft:orange_banner":
            case "minecraft:magenta_banner":
            case "minecraft:light_blue_banner":
            case "minecraft:yellow_banner":
            case "minecraft:lime_banner":
            case "minecraft:pink_banner":
            case "minecraft:gray_banner":
            case "minecraft:light_gray_banner":
            case "minecraft:cyan_banner":
            case "minecraft:purple_banner":
            case "minecraft:blue_banner":
            case "minecraft:brown_banner":
            case "minecraft:green_banner":
            case "minecraft:red_banner":
            case "minecraft:black_banner":
            case "minecraft:white_wall_banner":
            case "minecraft:orange_wall_banner":
            case "minecraft:magenta_wall_banner":
            case "minecraft:light_blue_wall_banner":
            case "minecraft:yellow_wall_banner":
            case "minecraft:lime_wall_banner":
            case "minecraft:pink_wall_banner":
            case "minecraft:gray_wall_banner":
            case "minecraft:light_gray_wall_banner":
            case "minecraft:cyan_wall_banner":
            case "minecraft:purple_wall_banner":
            case "minecraft:blue_wall_banner":
            case "minecraft:brown_wall_banner":
            case "minecraft:green_wall_banner":
            case "minecraft:red_wall_banner":
            case "minecraft:black_wall_banner":
                return BannerHandler;
            case "minecraft:end_gateway":
                return EndGatewayHandler;
            case "minecraft:spawner":
                return MobSpawnerHandler;
            case "minecraft:piston":
            case "minecraft:sticky_piston":
            case "minecraft:piston_head":
            case "minecraft:moving_piston":
                return PistonHandler;
            case "minecraft:oak_sign":
            case "minecraft:spruce_sign":
            case "minecraft:birch_sign":
            case "minecraft:jungle_sign":
            case "minecraft:acacia_sign":
            case "minecraft:dark_oak_sign":
            case "minecraft:crimson_sign":
            case "minecraft:warped_sign":
            case "minecraft:oak_wall_sign":
            case "minecraft:spruce_wall_sign":
            case "minecraft:birch_wall_sign":
            case "minecraft:jungle_wall_sign":
            case "minecraft:acacia_wall_sign":
            case "minecraft:dark_oak_wall_sign":
            case "minecraft:crimson_wall_sign":
            case "minecraft:warped_wall_sign":
                return SignHandler;
            case "minecraft:player_head":
            case "minecraft:player_wall_head":
                return SkullHandler;
            default:
                return DummyHandler;
        }
    }
}
