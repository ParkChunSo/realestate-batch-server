package kr.ac.skuniv.realestate_batch.batch;


import kr.ac.skuniv.realestate_batch.batch.item.*;
import kr.ac.skuniv.realestate_batch.domain.dto.openApiDto.BuildingDealDto;
import kr.ac.skuniv.realestate_batch.domain.entity.BuildingEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class RealestateJobConfiguration extends DefaultBatchConfigurer {

    private final RealestateItemReader realestateItemReader;
    private final RealestateItemProcessor realestateItemProcessor;
    private final RealestateItemWriter realestateItemWriter;
    private final RealestatePartitioner realestatePartitioner;

    private final EntityManagerFactory entityManagerFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobLauncher jobLauncher;


    private final DataSource dataSource;

    @Bean
    public Job apiCallJob() {
        return jobBuilderFactory.get("apiCallJob2")
                .start(apiCallPartitionStep())
                .build();
    }

    @Bean
    public Step apiCallPartitionStep() throws UnexpectedInputException, ParseException {
        return stepBuilderFactory.get("apiCallPartitionStep")
                .partitioner("apiCallPartitionStep", realestatePartitioner)
                .step(apiCallStep())
                .gridSize(6)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step apiCallStep() {
        return stepBuilderFactory.get("apiCallStep").<BuildingDealDto, List<BuildingEntity>>chunk(3)
                .reader(realestateItemReader)
                .processor(realestateItemProcessor)
                .writer(writer())
                .transactionManager(jpaTransactionManager())
                .build();
    }

    private JpaItemListWriter<BuildingEntity> writer(){
        JpaItemWriter<BuildingEntity> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);

        return new JpaItemListWriter<>(writer);
    }

    @Bean
    public JobExecutionListener listener() {
        return new JobListener();
    }

    @Bean
    @Primary
    public JpaTransactionManager jpaTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    @Bean
    public TaskExecutor taskExecutor(){
        return new SimpleAsyncTaskExecutor("batch_partitioner_");
    }
}
