package com.chiyuke.gridflux.util;

import com.chiyuke.gridflux.GridFlux;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EnergyText {
    private static final NumberFormat FE_FORMAT = NumberFormat.getIntegerInstance();
    private static final DecimalFormat MFE_FORMAT = new DecimalFormat("#,##0.##");
    private static final int MFE = 1_000_000;

    public static String format(int energy) {
        if (energy >= MFE) {
            return MFE_FORMAT.format(energy / (double) MFE) + " MFE";
        }
        return FE_FORMAT.format(energy) + " FE";
    }
}
