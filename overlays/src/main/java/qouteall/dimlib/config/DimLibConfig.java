package qouteall.dimlib.config;

/**
 * Replaces upstream's MidnightConfig-based class (DimLib, CC0). The only option is kept as a plain static
 * flag so every reference in the imported code still compiles, without adding another library.
 */
public class DimLibConfig {
    public static boolean suppressExperimentalWarning = false;
}
