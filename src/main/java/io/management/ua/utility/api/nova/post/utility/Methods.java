package io.management.ua.utility.api.nova.post.utility;

import org.springframework.data.util.Pair;

public class Methods {
    public static final Pair<String, String> WAREHOUSES = Pair.of("getWarehouses", "Address");
    public static final Pair<String, String> DELIVERY_COST = Pair.of("getDocumentPrice", "InternetDocument");
    public static final Pair<String, String> CITY_ID = Pair.of("getCities", "Address");
    public static final Pair<String, String> CITIES = Pair.of("getSettlements", "AddressGeneral");
}
