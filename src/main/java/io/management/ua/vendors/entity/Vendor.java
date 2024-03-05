package io.management.ua.vendors.entity;

import io.management.ua.utility.TimeUtil;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "vendors")
@FieldNameConstants
public class Vendor {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "joining_date")
    private ZonedDateTime joiningDate = TimeUtil.getCurrentDateTime();
    @Column(name = "revoking_date")
    private ZonedDateTime revokingDate;
    @Column(name = "picture_url")
    private String pictureUrl;
    @Column(name = "website", unique = true)
    private String website;
    @Column(name = "phone", unique = true)
    private String phone;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "revoked")
    private Boolean revoked;
}
