package net.mehvahdjukaar.supplementaries.configs;

import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.client.renderers.entities.layers.QuiverLayer;
import net.mehvahdjukaar.supplementaries.integration.CompatHandler;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;


public class ClientConfigs {

    public static void init() {
    }

    public static final ConfigSpec CLIENT_SPEC;

    static WeakReference<ConfigBuilder> builderReference;

    static {
        ConfigBuilder builder = ConfigBuilder.create(Supplementaries.res("client"), ConfigType.CLIENT);
        builderReference = new WeakReference<>(builder);

        Blocks.init();
        Particles.init();
        General.init();
        Tweaks.init();
        Items.init();
        CLIENT_SPEC = builder.buildAndRegister();
    }

    public static class Items {

        private static void init() {
        }

        public static final Supplier<QuiverLayer.QuiverMode> QUIVER_RENDER_MODE;
        public static final Supplier<QuiverLayer.QuiverMode> QUIVER_SKELETON_RENDER_MODE;
        public static final Supplier<Double> QUIVER_ARMOR_OFFSET;
        public static final Supplier<Boolean> QUIVER_MOUSE_MOVEMENT;
        public static final Supplier<Boolean> QUIVER_OVERLAY;
        public static final Supplier<Integer> QUIVER_GUI_X;
        public static final Supplier<Integer> QUIVER_GUI_Y;
        public static final Supplier<Boolean> SLINGSHOT_OVERLAY;
        public static final Supplier<Boolean> SLINGSHOT_OUTLINE;
        public static final Supplier<Integer> SLINGSHOT_OUTLINE_COLOR;
        public static final Supplier<Double> SLINGSHOT_PROJECTILE_SCALE;
        public static final Supplier<Boolean> WRENCH_PARTICLES;
        public static final Supplier<Boolean> FLUTE_PARTICLES;

        static {
            ConfigBuilder builder = builderReference.get();

            builder.push("items");

            builder.push("slingshot");
            SLINGSHOT_OVERLAY = builder.comment("Adds an overlay to slingshots in gui displaying currently selected block")
                    .define("overlay", true);
            SLINGSHOT_OUTLINE = builder.comment("Render the block outline for distant blocks that are reachable with a slingshot enchanted with Stasis")
                    .define("stasis_block_outline", true);
            SLINGSHOT_OUTLINE_COLOR = builder.comment("An RGBA color for the block outline in hex format, for example 0x00000066 for vanilla outline colors")
                    .defineColor("block_outline_color", 0xffffff66);
            SLINGSHOT_PROJECTILE_SCALE = builder.comment("How big should a slingshot projectile look")
                    .define("projectile_scale", 0.5, 0, 1);
            builder.pop();

            builder.push("quiver");
            QUIVER_ARMOR_OFFSET = builder.comment("Z offset for quiver render when wearing armor. Useful for when you have custom armor bigger than vanilla to void clipping. Leave at -1 for automatic offset")
                    .define("armor_render_offset", -1d, -1d, 1);
            QUIVER_RENDER_MODE = builder.comment("How quivers should render onto players")
                    .define("render_mode", QuiverLayer.QuiverMode.THIGH);
            QUIVER_SKELETON_RENDER_MODE = builder.comment("How skeleton with quivers should render it")
                    .define("skeleton_render_mode", QuiverLayer.QuiverMode.THIGH);
            QUIVER_OVERLAY = builder.comment("Adds an overlay to quivers in gui displaying currently selected arrow")
                    .define("overlay", true);
            QUIVER_MOUSE_MOVEMENT = builder.comment("Allows using your mouse to select an arrow in the quiver GUI")
                    .define("mouse_movement_in_gui", true);
            QUIVER_GUI_X = builder.comment("Quiver GUI X offset from default position")
                    .define("gui_x_offset", 0, -1000, 1000);
            QUIVER_GUI_Y = builder.comment("Quiver GUI Y offset from default position")
                    .define("gui_y_offset", 0, -1000, 1000);
            builder.pop();

            builder.push("wrench");
            WRENCH_PARTICLES = builder.comment("Display visual particles when a block is rotated")
                    .define("turn_particles", true);
            builder.pop();

            builder.push("flute");
            FLUTE_PARTICLES = builder.comment("Display visual particles when a playing a flute")
                    .define("note_particles", true);
            builder.pop();

            builder.pop();
        }
    }

    public static class Tweaks {

        private static void init() {
        }

        public static final Supplier<Boolean> COLORED_ARROWS;
        public static final Supplier<Boolean> COLORED_BREWING_STAND;
        public static final Supplier<Boolean> CLOCK_CLICK;
        public static final Supplier<Boolean> COMPASS_CLICK;
        public static final Supplier<Boolean> BOOK_GLINT;
        public static final Supplier<Boolean> BANNER_PATTERN_TOOLTIP;
        public static final Supplier<Boolean> MOB_HEAD_EFFECTS;

        static{
            ConfigBuilder builder = builderReference.get();

            builder.comment("Game tweaks")
                    .push("tweaks");
            COLORED_BREWING_STAND = builder.comment("Colors the brewing stand potion texture depending on the potions it's brewing.\n" +
                            "If using a resource pack add tint index from 0 to 3 to the 3 potion layers")
                    .define("brewing_stand_colors", true);
            COLORED_ARROWS = builder.comment("Makes tipped arrows show their colors when loaded with a crossbow")
                    .define("crossbows_colors", true);
            CLOCK_CLICK = builder.comment("Allow to right click with a clock to display current time in numerical form")
                    .define("clock_right_click", true);
            COMPASS_CLICK = builder.comment("Allow to right click with a compass to display current coordinates in numerical form")
                    .define("compass_right_click", false);
            BOOK_GLINT = builder.comment("Renders an enchantment glint on placeable enchanted books")
                    .define("placeable_books_glint", false);
            BANNER_PATTERN_TOOLTIP = builder.comment("Enables banner pattern tooltip image preview")
                    .define("banner_pattern_tooltip", true);
            MOB_HEAD_EFFECTS = builder.comment("Wearing mob heads will apply post processing")
                    .define("mob_head_shaders", true);
            builder.pop();
        }
    }

    public static class General {

        private static void init() {
        }

        public static final Supplier<Boolean> CONFIG_BUTTON;
        public static final Supplier<Boolean> TOOLTIP_HINTS;
        public static final Supplier<Boolean> PLACEABLE_TOOLTIP;

        public static final Supplier<Double> TEST1;
        public static final Supplier<Double> TEST2;
        public static final Supplier<Double> TEST3;

        static{
            ConfigBuilder builder = builderReference.get();

            builder.comment("General settings")
                    .push("general");
            CONFIG_BUTTON = builder.comment("Enable Quark style config button on main menu. Needs Configured installed to work")
                    .define("config_button", CompatHandler.CONFIGURED);
            TOOLTIP_HINTS = builder.comment("Show some tooltip hints to guide players through the mod")
                    .define("tooltip_hints", true);
            PLACEABLE_TOOLTIP = builder.comment("Show tooltips items that have been made placeable")
                    .define("placeable_tooltips", true);

            TEST1 = builder.comment("ignore this").define("test1", 0f, -10, 10);
            TEST2 = builder.comment("ignore this").define("test2", 0f, -10, 10);
            TEST3 = builder.comment("ignore this").define("test3", 0f, -10, 10);
            builder.pop();
        }
    }

    public enum GraphicsFanciness {
        FAST,
        FANCY,
        FABULOUS
    }

    public static class Blocks {

        private static void init() {
        }

        public static final Supplier<Double> BUBBLE_BLOCK_WOBBLE;
        public static final Supplier<Double> BUBBLE_BLOCK_GROW_SPEED;
        public static final Supplier<Boolean> PEDESTAL_SPIN;
        public static final Supplier<Boolean> PEDESTAL_SPECIAL;
        public static final Supplier<Double> PEDESTAL_SPEED;
        public static final Supplier<Boolean> SHELF_TRANSLATE;
        public static final Supplier<Double> WIND_VANE_POWER_SCALING;
        public static final Supplier<Double> WIND_VANE_ANGLE_1;
        public static final Supplier<Double> WIND_VANE_ANGLE_2;
        public static final Supplier<Double> WIND_VANE_PERIOD_1;
        public static final Supplier<Double> WIND_VANE_PERIOD_2;
        public static final Supplier<Boolean> CLOCK_24H;
        public static final Supplier<Boolean> GLOBE_RANDOM;
        public static final Supplier<Boolean> TIPPED_BAMBOO_SPIKES_TAB;

        public static final Supplier<GraphicsFanciness> FLAG_FANCINESS;
        public static final Supplier<Boolean> FLAG_BANNER;
        public static final Supplier<Integer> FLAG_PERIOD;
        public static final Supplier<Double> FLAG_WAVELENGTH;
        public static final Supplier<Double> FLAG_AMPLITUDE;
        public static final Supplier<Double> FLAG_AMPLITUDE_INCREMENT;
        public static final Supplier<List<String>> TICKABLE_MOBS;

        public static final Supplier<Boolean> FAST_SIGNS;
        public static final Supplier<Boolean> FAST_LANTERNS;
        public static final Supplier<Boolean> TURN_TABLE_PARTICLES;
        public static final Supplier<Boolean> SPEAKER_BLOCK_MUTE;
        public static final Supplier<Double> ROPE_WOBBLE_AMPLITUDE;
        public static final Supplier<Double> ROPE_WOBBLE_PERIOD;

        static {

            ConfigBuilder builder = builderReference.get();

            builder.comment("""
                            Tweak and change the various block animations.
                            Only cosmetic stuff in here so to leave default if not interested.
                            Remember to delete this and server configs and let it refresh every once in a while since I might have tweaked it""")
                    .push("blocks");

            builder.push("globe");
            GLOBE_RANDOM = builder.comment("Enable a random globe texture for each world").define("random_world", true);
            builder.pop();

            builder.push("clock_block");
            CLOCK_24H = builder.comment("Display 24h time format. False for 12h format").define("24h_format", true);
            builder.pop();

            builder.push("pedestal");
            PEDESTAL_SPIN = builder.comment("Enable displayed item spin")
                    .define("spin", true);
            PEDESTAL_SPEED = builder.comment("Spin speed")
                    .define("speed", 2.0, 0, 100);
            PEDESTAL_SPECIAL = builder.comment("Enable special display types for items like swords, tridents or end crystals")
                    .define("fancy_renderers", true);
            builder.pop();

            builder.push("bubble_block");
            BUBBLE_BLOCK_WOBBLE = builder.comment("Wobbling intensity. set to 0 to disable")
                    .define("wobble", 0.2, 0, 1);
            BUBBLE_BLOCK_GROW_SPEED = builder.comment("How fast it grows when created. 1 to be instant")
                    .define("grow_speed", 0.4, 0, 1);
            builder.pop();

            builder.push("item_shelf");
            SHELF_TRANSLATE = builder.comment("Translate down displayed 3d blocks so that they are touching the shelf.\n" +
                            "Note that they will not be centered vertically this way")
                    .define("supported_blocks", true);
            builder.pop();

            builder.push("wind_vane");
            WIND_VANE_POWER_SCALING = builder.comment("""
                            Wind vane animation swings according to this equation:\s
                            angle(time) = max_angle_1*sin(2pi*time*pow/period_1) + <max_angle_2>*sin(2pi*time*pow/<period_2>)
                            where:
                             - pow = max(1,redstone_power*<power_scaling>)
                             - time = time in ticks
                             - redstone_power = block redstone power
                            <power_scaling> = how much frequency changes depending on power. 2 means it spins twice as fast each power level (2* for rain, 4* for thunder)
                            increase to have more distinct indication when weather changes""")
                    .define("power_scaling", 3.0, 1.0, 100.0);
            WIND_VANE_ANGLE_1 = builder.comment("Amplitude (maximum angle) of first sine wave")
                    .define("max_angle_1", 30.0, 0, 360);
            WIND_VANE_ANGLE_2 = builder.define("max_angle_2", 10.0, 0, 360);
            WIND_VANE_PERIOD_1 = builder.comment("Base period in ticks at 0 power of first sine wave")
                    .define("period_1", 450.0, 0.0, 2000.0);
            WIND_VANE_PERIOD_2 = builder.comment("This should be kept period_1/3 for a symmetric animation")
                    .define("period_2", 150.0, 0.0, 2000.0);
            builder.pop();

            builder.push("flag");
            FLAG_PERIOD = builder.comment("How slow a flag will oscillate. (Period of oscillation)\n" +
                            "Lower value = faster oscillation")
                    .define("slowness", 100, 0, 10000);
            FLAG_WAVELENGTH = builder.comment("How wavy the animation will be in pixels. (Wavelength)")
                    .define("wavyness", 4d, 0.001, 100);
            FLAG_AMPLITUDE = builder.comment("How tall the wave lobes will be. (Wave amplitude)")
                    .define("intensity", 1d, 0d, 100d);
            FLAG_AMPLITUDE_INCREMENT = builder.comment("How much the wave amplitude increases each pixel. (Amplitude increment per pixel)")
                    .define("intensity_increment", 0.3d, 0, 10);
            FLAG_FANCINESS = builder.comment("At which graphic settings flags will have a fancy renderer: 0=fast, 1=fancy, 2=fabulous")
                    .define("fanciness", GraphicsFanciness.FABULOUS);
            FLAG_BANNER = builder.comment("Makes flags render as sideways banner. Ignores many of the previously defined configs")
                    .define("render_as_banner", false);
            builder.pop();
            //TODO: add more(hourGlass, sawying blocks...)

            builder.push("captured_mobs").comment("THIS IS ONLY FOR VISUALS! To allow more entities in cages you need to edit the respective tags!");

            TICKABLE_MOBS = builder.comment("A list of mobs that can be ticked on client side when inside jars. Mainly used for stuff that has particles. Can cause issues and side effects so use with care")
                    .define("tickable_inside_jars", Arrays.asList("iceandfire:pixie", "druidcraft:dreadfish", "druidcraft:lunar_moth", "alexsmobs:hummingbird"));

            builder.pop();

            builder.push("wall_lantern");
            FAST_LANTERNS = builder.comment("Makes wall lantern use a simple block model instead of the animated tile entity renderer. This will make them render much faster but will also remove the animation" +
                            "Note that this option only affect lanterns close by as the one far away render as fast by default")
                    .define("fast_lanterns", false);
            builder.pop();

            builder.push("hanging_sign");
            FAST_SIGNS = builder.comment("Makes hanging signs use a simple block model instead of the animated tile entity renderer. This will make them render much faster but will also remove the animation" +
                            "Note that this option only affect lanterns close by as the one far away render as fast by default")
                    .define("fast_signs", false);
            builder.pop();

            builder.push("bamboo_spikes");
            TIPPED_BAMBOO_SPIKES_TAB = builder.comment("Populate the creative inventory with all tipped spikes variations")
                    .define("populate_creative_tab", true);
            builder.pop();

            builder.push("turn_table");
            TURN_TABLE_PARTICLES = builder.comment("Display visual particles when a block is rotated")
                    .define("turn_particles", true);
            builder.pop();

            builder.push("speaker_block");
            SPEAKER_BLOCK_MUTE = builder.comment("Mute speaker block incoming narrator messages and displays them in chat instead")
                    .define("mute_narrator", false);
            builder.pop();

            builder.push("rope");
            ROPE_WOBBLE_AMPLITUDE = builder.comment("Amplitude of rope wobbling effect")
                    .define("wobbling_amplitude", 1.2d, 0, 20);
            ROPE_WOBBLE_PERIOD = builder.comment("Period of rope wobbling effect")
                    .define("wobbling_period", 12d, 0.01, 200);
            builder.pop();

            builder.pop();
        }
    }


    public static class Particles {

        private static void init() {
        }

        public static final Supplier<Integer> TURN_INITIAL_COLOR;
        public static final Supplier<Integer> TURN_FADE_COLOR;

        static{

            ConfigBuilder builder = builderReference.get();
            builder.comment("Particle parameters")
                    .push("particles");


            builder.comment("Rotation particle")
                    .push("turn_particle");

            TURN_INITIAL_COLOR = builder.comment("An RGBA color")
                    .defineColor("initial_color", 0x2a77ea);
            TURN_FADE_COLOR = builder.comment("An RGBA color")
                    .defineColor("fade_color", 0x32befa);

            builder.pop();

            builder.pop();
        }
    }

}
