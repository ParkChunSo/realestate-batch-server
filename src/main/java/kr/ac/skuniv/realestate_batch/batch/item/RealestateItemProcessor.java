package kr.ac.skuniv.realestate_batch.batch.item;

import kr.ac.skuniv.realestate_batch.domain.dto.BargainDto;
import kr.ac.skuniv.realestate_batch.domain.dto.CharterAndRentDto;
import kr.ac.skuniv.realestate_batch.domain.dto.openApiDto.BargainItemDto;
import kr.ac.skuniv.realestate_batch.domain.dto.openApiDto.BuildingDealDto;
import kr.ac.skuniv.realestate_batch.domain.dto.openApiDto.CharterAndRentItemDto;
import kr.ac.skuniv.realestate_batch.domain.entity.BargainDate;
import kr.ac.skuniv.realestate_batch.domain.entity.BuildingEntity;
import kr.ac.skuniv.realestate_batch.domain.entity.CharterDate;
import kr.ac.skuniv.realestate_batch.domain.entity.RentDate;
import kr.ac.skuniv.realestate_batch.util.Dto2DaoConvertor;
import kr.ac.skuniv.realestate_batch.util.OpenApiContents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@StepScope
@Configuration
@RequiredArgsConstructor
public class RealestateItemProcessor implements ItemProcessor<BuildingDealDto, List<BuildingEntity>>, StepExecutionListener {
    private String buildingType;
    private List<BuildingEntity> buildingEntities = new ArrayList<>();
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext ctx = stepExecution.getExecutionContext();
        buildingType = (String) ctx.get(OpenApiContents.BUILDING_TYPE);
        log.info(buildingType + " 유형 PROCESSOR START");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public List<BuildingEntity> process(BuildingDealDto buildingDealDto) throws Exception {
        if(buildingDealDto.getDealType().equals(OpenApiContents.BARGAIN_NUM)){
            BargainDto bargainDto = (BargainDto) buildingDealDto;
            for (BargainItemDto bargainItemDto : bargainDto.getBody().getItem()){
                BuildingEntity buildingEntity = Dto2DaoConvertor.newInstanceOfBuildingEntity(bargainItemDto, this.buildingType);
                BargainDate bargainDate = Dto2DaoConvertor.newInstanceOfBargainDate(bargainItemDto);
                saveData(buildingEntity, bargainDate);
            }
        }else {
            CharterAndRentDto charterAndRentDto = (CharterAndRentDto) buildingDealDto;
            for (CharterAndRentItemDto charterAndRentItemDto : charterAndRentDto.getBody().getItem()) {
                BuildingEntity buildingEntity = Dto2DaoConvertor.newInstanceOfBuildingEntity(charterAndRentItemDto, this.buildingType);
                if (Integer.parseInt(charterAndRentItemDto.getMonthlyPrice().trim().replaceAll("[^0-9?!\\.]","")) == 0) {
                    CharterDate charterDate = Dto2DaoConvertor.newInstanceOfCharterDate(charterAndRentItemDto);
                    saveData(buildingEntity, charterDate);
                }else {
                    RentDate rentDate = Dto2DaoConvertor.newInstanceOfRentDate(charterAndRentItemDto);
                    saveData(buildingEntity, rentDate);
                }
            }
        }
        return buildingEntities;
    }

    private void saveData(BuildingEntity buildingEntity, BargainDate data){
        boolean isContained = false;
        for(int i = 0 ; i < buildingEntities.size() ; i++){
            BuildingEntity tmp = buildingEntities.get(i);
            if(buildingEntity.getBuildingNum().equals(tmp.getBuildingNum())
                    && buildingEntity.getFloor() == tmp.getFloor()
                    && buildingEntity.getName().equals(tmp.getName())
                    && buildingEntity.getDong().equals(tmp.getDong())
                    && buildingEntity.getArea().equals(tmp.getArea())) {

                data.setBuildingEntity(buildingEntities.get(i));
                tmp.getBargainDates().add(data);
                isContained = true;
                break;
            }
        }
        if(!isContained){
            data.setBuildingEntity(buildingEntity);
            buildingEntity.getBargainDates().add(data);
            buildingEntities.add(buildingEntity);
        }
    }

    private void saveData(BuildingEntity buildingEntity, CharterDate data){
        boolean isContained = false;
        for(int i = 0 ; i < buildingEntities.size() ; i++){
            BuildingEntity tmp = buildingEntities.get(i);
            if(buildingEntity.getBuildingNum().equals(tmp.getBuildingNum())
                    && buildingEntity.getFloor() == tmp.getFloor()
                    && buildingEntity.getName().equals(tmp.getName())
                    && buildingEntity.getDong().equals(tmp.getDong())
                    && buildingEntity.getArea().equals(tmp.getArea())) {

                data.setBuildingEntity(buildingEntities.get(i));
                tmp.getCharterDates().add(data);
                isContained = true;
                break;
            }
        }
        if(!isContained){
            data.setBuildingEntity(buildingEntity);
            buildingEntity.getCharterDates().add(data);
            buildingEntities.add(buildingEntity);
        }
    }

    private void saveData(BuildingEntity buildingEntity, RentDate data){
        boolean isContained = false;
        for(int i = 0 ; i < buildingEntities.size() ; i++){
            BuildingEntity tmp = buildingEntities.get(i);
            if(buildingEntity.getBuildingNum().equals(tmp.getBuildingNum())
                    && buildingEntity.getFloor() == tmp.getFloor()
                    && buildingEntity.getName().equals(tmp.getName())
                    && buildingEntity.getDong().equals(tmp.getDong())
                    && buildingEntity.getArea().equals(tmp.getArea())) {

                data.setBuildingEntity(buildingEntities.get(i));
                tmp.getRentDates().add(data);
                isContained = true;
                break;
            }
        }
        if(!isContained){
            data.setBuildingEntity(buildingEntity);
            buildingEntity.getRentDates().add(data);
            buildingEntities.add(buildingEntity);
        }
    }
}
