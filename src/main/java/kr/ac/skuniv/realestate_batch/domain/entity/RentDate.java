package kr.ac.skuniv.realestate_batch.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private BuildingEntity buildingEntity;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "guarantee_price")
    private String guaranteePrice;

    @Column(name = "monthly_price")
    private String monthlyPrice;
}
