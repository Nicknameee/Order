package io.management.ua.vendors.service;

import io.management.resources.models.Image;
import io.management.resources.service.ImageHostingService;
import io.management.ua.annotations.DefaultNumberValue;
import io.management.ua.exceptions.DefaultException;
import io.management.ua.exceptions.NotFoundException;
import io.management.ua.products.repository.ProductRepository;
import io.management.ua.utility.ExportUtil;
import io.management.ua.utility.TimeUtil;
import io.management.ua.utility.api.enums.Folder;
import io.management.ua.vendors.dto.CreateVendorDTO;
import io.management.ua.vendors.dto.UpdateVendorDTO;
import io.management.ua.vendors.dto.VendorFilter;
import io.management.ua.vendors.entity.Vendor;
import io.management.ua.vendors.mapper.VendorMapper;
import io.management.ua.vendors.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class VendorService {
    @PersistenceContext(unitName = "database")
    private final EntityManager entityManager;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final VendorMapper vendorMapper;
    private final ImageHostingService imageHostingService;

    public List<Vendor> getVendors(@RequestBody @Valid VendorFilter vendorFilter,
                                   @DefaultNumberValue Integer page,
                                   @DefaultNumberValue(number = 100) Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vendor> query = criteriaBuilder.createQuery(Vendor.class);
        Root<Vendor> root = query.from(Vendor.class);

        List<Predicate> predicates = new ArrayList<>();

        if (vendorFilter != null) {
            if (vendorFilter.getJoiningDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Vendor.Fields.joiningDate),
                        vendorFilter.getJoiningDateFrom()));
            }

            if (vendorFilter.getJoiningDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Vendor.Fields.joiningDate),
                        vendorFilter.getJoiningDateTo()));
            }

            if (vendorFilter.getIsRevoked() != null) {
                if (vendorFilter.getIsRevoked()) {
                    predicates.add(criteriaBuilder.isNull(root.get(Vendor.Fields.revokingDate)));
                } else {
                    predicates.add(criteriaBuilder.isNotNull(root.get(Vendor.Fields.revokingDate)));
                }
            }
        }

        query.where(predicates.toArray(Predicate[]::new));

        return entityManager.createQuery(query).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
    }

    @Cacheable(cacheNames = "vendorsCache", key = "#vendorId", sync = true)
    public Vendor getVendorById(UUID vendorId) {
        return vendorRepository.findById(vendorId).orElseThrow(() -> new NotFoundException("Vendor with ID {} was not found", vendorId));
    }

    @CachePut(cacheNames = "vendorsCache", key = "#result.id")
    public Vendor saveVendor(@Valid CreateVendorDTO createVendorDTO) {
        Vendor vendor = vendorMapper.dtoToEntity(createVendorDTO);
        vendor.setRevoked(false);

        return vendorRepository.save(vendor);
    }

    @CachePut(cacheNames = "vendorsCache", key = "#vendorId")
    public Vendor setVendorPicture(UUID vendorId, MultipartFile picture) {
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new NotFoundException("Vendor with ID {} was not found", vendorId));

        if (vendor.getPictureUrl() != null) {
            imageHostingService.deleteImage(vendor.getPictureUrl());
            vendor.setPictureUrl(null);
        }

        if (picture != null) {
            Image image = imageHostingService.uploadImage(picture, Folder.VENDORS + vendorId);
            vendor.setPictureUrl(image.getSecureUrl());
        }

        return vendorRepository.save(vendor);
    }

    @Transactional
    @CachePut(cacheNames = "vendorsCache", key = "#result.id")
    public Vendor updateVendor(@Valid UpdateVendorDTO updateVendorDTO) {
        Vendor vendor = vendorRepository.findById(updateVendorDTO.getVendorId()).orElseThrow(() -> new NotFoundException("Vendor with ID {} was not found", updateVendorDTO.getVendorId()));

        if (updateVendorDTO.getName() != null) {
            vendor.setName(updateVendorDTO.getName());
        }

        if (updateVendorDTO.getWebsite() != null && Pattern.matches(io.management.ua.utility.Pattern.WEBSITE, updateVendorDTO.getWebsite())) {
            vendor.setWebsite(updateVendorDTO.getWebsite());
        }

        if (updateVendorDTO.getPhone() != null && Pattern.matches(io.management.ua.utility.Pattern.PHONE, updateVendorDTO.getPhone())) {
            vendor.setPhone(updateVendorDTO.getPhone());
        }

        if (updateVendorDTO.isRevoke() && vendor.getRevokingDate() == null) {
            vendor.setRevoked(true);
            vendor.setRevokingDate(TimeUtil.getCurrentDateTime());
            productRepository.blockVendorProducts(vendor.getId());
        } else if (!updateVendorDTO.isRevoke() && vendor.getRevokingDate() != null) {
            vendor.setRevoked(false);
            vendor.setRevokingDate(null);
            productRepository.unblockVendorsProducts(vendor.getId());
        }

        return vendorRepository.save(vendor);
    }

    public void exportVendors(VendorFilter vendorFilter, String filename, HttpServletResponse httpServletResponse) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Vendors");
            sheet.setDefaultColumnWidth(50);

            httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s.xlsx", ExportUtil.validateFilename(filename)));
            OutputStream outputStream = httpServletResponse.getOutputStream();

            int page = 1;
            int limitPerPage = 500;
            List<Vendor> vendors;

            do {
                vendors = getVendors(vendorFilter, page, limitPerPage);

                if (!vendors.isEmpty()) {
                    Row headerRow = sheet.createRow(0);
                    Field[] fields = vendors.get(0).getClass().getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        headerRow.createCell(i).setCellValue(ExportUtil.convertFieldNameToTitle(fields[i].getName()));
                    }

                    int rowNum = 1;

                    for (Vendor vendor : vendors) {
                        Row row = sheet.createRow(rowNum++);
                        int colNum = 0;
                        for (Field field : fields) {
                            field.setAccessible(true);
                            try {
                                Object value = field.get(vendor);
                                if (value != null) {
                                    ExportUtil.setCellValue(row.createCell(colNum++), value);
                                } else {
                                    row.createCell(colNum++).setCellValue("NONE");
                                }
                            } catch (IllegalAccessException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                    workbook.write(outputStream);
                    page++;
                } else {
                    if (page == 1) {
                        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline");
                    }
                }
            } while (!vendors.isEmpty());

            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new DefaultException("Exception occurred while exporting");
        }
    }
}
