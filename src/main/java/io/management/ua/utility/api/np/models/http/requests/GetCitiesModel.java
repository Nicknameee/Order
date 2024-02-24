package io.management.ua.utility.api.np.models.http.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetCitiesModel {
    /**
     * Номер сторінки для відображення
     */
    @JsonProperty("Page")
    private String page;
    @JsonProperty("FindByString")
    private String findByString;
    /**
     * Параметр "Warehouse" із значенням "1 или 0" дозволить відобразити лише ті населені пункти в яких наявні відділення "Нова Пошта".
     */
    @JsonProperty("Warehouse")
    private String warehouse;
    /**
     * Кількість записів на сторінці. Працює разом з параметром Page
     */
    @JsonProperty("Limit")
    private String limit;
}
