package io.management.ua.utility.api.nova.post.models.http.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetCityRefModel {
    /**
     * Номер сторінки для відображення
     */
    @JsonProperty("Page")
    private String page;
    /**
     * Пошук по назві міста
     */
    @JsonProperty("FindByString")
    private String city;
    /**
     * Кількість записів на сторінці. Працює разом з параметром Page
     */
    @JsonProperty("Limit")
    private String limit;
}
