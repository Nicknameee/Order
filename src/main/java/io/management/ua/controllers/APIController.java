package io.management.ua.controllers;

import io.management.ua.response.Response;
import io.management.ua.utility.CodeGenerator;
import io.management.ua.utility.api.nova.post.service.NovaPostDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/util/v1/services")
@Slf4j
@RequiredArgsConstructor
public class APIController {
    private final NovaPostDeliveryService novaPostDeliveryService;

    @GetMapping("/post/nova/cities")
    public Response<?> getCities(@RequestParam String findByString,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer limit) {
        return Response.ok(novaPostDeliveryService.getCities(findByString, page ,limit));
    }

    @GetMapping("/post/nova/warehouses")
    public Response<?> getWarehouses(@RequestParam String cityName,
                                     @RequestParam(required = false) String findByString,
                                     @RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer limit) {
        return Response.ok(novaPostDeliveryService.getWarehouses(cityName, findByString, page, limit));
    }

    @GetMapping("/qr")
    public ResponseEntity<?> getQr(@RequestParam String productId) {
        byte[] imageBytes = CodeGenerator.generateQRCode("http://localhost:3333/product?productId=" + productId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
