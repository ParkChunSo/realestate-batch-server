package kr.ac.skuniv.realestate_batch.batch.item;

import kr.ac.skuniv.realestate_batch.domain.entity.BuildingEntity;
import kr.ac.skuniv.realestate_batch.repository.BuildingEntityRepository;
import kr.ac.skuniv.realestate_batch.util.OpenApiContents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@StepScope
@Configuration
public class RealestateItemWriter extends JpaItemWriter<List<BuildingEntity>> implements StepExecutionListener, InitializingBean{

    private String buildingType;

    @Autowired
    private BuildingEntityRepository buildingEntityRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Override
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        super.setEntityManagerFactory(this.entityManagerFactory);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext ctx = stepExecution.getExecutionContext();
        buildingType = (String) ctx.get(OpenApiContents.BUILDING_TYPE);
        log.info(buildingType + " 유형 데이터 저장 시작");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("WRITER STEP OVER");
        return ExitStatus.COMPLETED;
    }

    @Override
    protected void doWrite(EntityManager entityManager, List<? extends List<BuildingEntity>> items) {
        List<BuildingEntity> totalList = new ArrayList<>();

        for(List<BuildingEntity> list : items){
            totalList.addAll(list);
        }

        buildingEntityRepository.saveAll(totalList);
        buildingEntityRepository.flush();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
