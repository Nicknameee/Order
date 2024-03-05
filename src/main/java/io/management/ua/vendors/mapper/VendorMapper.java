package io.management.ua.vendors.mapper;

import io.management.ua.vendors.dto.CreateVendorDTO;
import io.management.ua.vendors.entity.Vendor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VendorMapper {
    Vendor dtoToEntity(CreateVendorDTO createVendorDTO);
}
