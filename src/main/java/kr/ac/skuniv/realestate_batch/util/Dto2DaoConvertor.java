package kr.ac.skuniv.realestate_batch.util;

import kr.ac.skuniv.realestate_batch.domain.dto.openApiDto.BargainItemDto;
import kr.ac.skuniv.realestate_batch.domain.dto.openApiDto.CharterAndRentItemDto;
import kr.ac.skuniv.realestate_batch.domain.dto.openApiDto.ItemDto;
import kr.ac.skuniv.realestate_batch.domain.entity.BargainDate;
import kr.ac.skuniv.realestate_batch.domain.entity.BuildingEntity;
import kr.ac.skuniv.realestate_batch.domain.entity.CharterDate;
import kr.ac.skuniv.realestate_batch.domain.entity.RentDate;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

public class Dto2DaoConvertor {
    public static BuildingEntity newInstanceOfBuildingEntity(ItemDto itemDto, String buildingType) {
        return BuildingEntity.builder().city(Integer.parseInt(itemDto.getRegionCode().substring(0,2))).groop(Integer.parseInt(itemDto.getRegionCode().substring(2))).dong(itemDto.getDong())
                .name(itemDto.getName() != null ? itemDto.getName() : "null").area(itemDto.getArea()).floor(itemDto.getFloor()).type(buildingType)
                .buildingNum(itemDto.getBuildingNum() != null ? itemDto.getBuildingNum() : "null").constructYear(String.valueOf(itemDto.getConstructYear()))
                .bargainDates(new HashSet<>())
                .charterDates(new HashSet<>())
                .rentDates(new HashSet<>())
                .build();
    }
    public static BargainDate newInstanceOfBargainDate(BargainItemDto bargainItemDto){
        String price = bargainItemDto.getDealPrice().trim().replaceAll("[^0-9?!\\.]","");
        Date date = new GregorianCalendar(bargainItemDto.getYear(), bargainItemDto.getMonthly() - 1, Integer.parseInt(bargainItemDto.getDays())).getTime();

        return BargainDate.builder()
                .date(date)
                .price(price)
                .pyPrice(getPyPrice(bargainItemDto.getArea(), price)).build();
    }

    public static CharterDate newInstanceOfCharterDate(CharterAndRentItemDto charterAndRentItemDto){
        String price = charterAndRentItemDto.getGuaranteePrice().trim().replaceAll("[^0-9?!\\.]","");
        Date date = new GregorianCalendar(charterAndRentItemDto.getYear(), charterAndRentItemDto.getMonthly() - 1, Integer.parseInt(charterAndRentItemDto.getDays())).getTime();

        return CharterDate.builder().date(date)
                .pyPrice(getPyPrice(charterAndRentItemDto.getArea(), price))
                .build();
    }

    public static RentDate newInstanceOfRentDate(CharterAndRentItemDto charterAndRentItemDto){
        String price = charterAndRentItemDto.getMonthlyPrice().trim().replaceAll("[^0-9?!\\.]","");
        Date date = new GregorianCalendar(charterAndRentItemDto.getYear(), charterAndRentItemDto.getMonthly() - 1, Integer.parseInt(charterAndRentItemDto.getDays())).getTime();

        return RentDate.builder().date(date)
                .guaranteePrice(charterAndRentItemDto.getGuaranteePrice().trim().replaceAll("[^0-9?!\\.]",""))
                .monthlyPrice(price)
                .build();
    }
    private static Double getPyPrice(Double area, String price) {
        double py = 3.3;
        if(area == null) {
            return null;
        }
        double buildingPy = area / py;
        return Double.valueOf(price) / buildingPy;
    }
}
