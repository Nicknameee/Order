package io.management.ua.address.attributes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AddressPart {
    COUNTRY_SENDER,
    REGION_SENDER,
    CITY_SENDER,
    POST_DEPARTMENT_NUMBER_SENDER,
    COUNTRY_RECIPIENT,
    REGION_RECIPIENT,
    CITY_RECIPIENT,
    POST_DEPARTMENT_NUMBER_RECIPIENT,
    POST_DEPARTMENT_TYPE_RECIPIENT,
    STREET_RECIPIENT,
    BUILDING_RECIPIENT
}
