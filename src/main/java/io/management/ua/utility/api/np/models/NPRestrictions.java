package io.management.ua.utility.api.np.models;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <a href="https://novaposhta.ua/vidpravlennia_z_yakymy_maksymalnymy_rozmiramy_dopuskaiutsia_do_transportuvannia">...</a>
 */
@Data
public class NPRestrictions {
    public static final Restriction posterminalRestriction =
            new Restriction(BigDecimal.valueOf(20L),
                    BigDecimal.valueOf(50L),
                    BigDecimal.valueOf(30L),
                    BigDecimal.valueOf(40L));
    public static final Restriction smallDepartment =
            new Restriction(BigDecimal.valueOf(5L),
                    BigDecimal.valueOf(60L),
                    BigDecimal.valueOf(60L),
                    BigDecimal.valueOf(60L));
    public static final Restriction department =
            new Restriction(BigDecimal.valueOf(30L),
                    BigDecimal.valueOf(60L),
                    BigDecimal.valueOf(60L),
                    BigDecimal.valueOf(60L));
    public static final Restriction cargoDepartment =
            new Restriction(BigDecimal.valueOf(Long.MAX_VALUE),
                    BigDecimal.valueOf(170L),
                    BigDecimal.valueOf(300L),
                    BigDecimal.valueOf(300L));

    public record Restriction(BigDecimal maxWeightPerPlace, BigDecimal maxHeightPerPlace, BigDecimal maxWidthPerPlace, BigDecimal maxLengthPerPlace) {
    }
}
