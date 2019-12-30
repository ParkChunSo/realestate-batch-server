package kr.ac.skuniv.realestate_batch.batch.item;

import kr.ac.skuniv.realestate_batch.domain.dto.BargainDto;
import kr.ac.skuniv.realestate_batch.domain.dto.CharterAndRentDto;
import kr.ac.skuniv.realestate_batch.domain.dto.openApiDto.BuildingDealDto;
import kr.ac.skuniv.realestate_batch.util.OpenApiContents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Slf4j
@Configuration
@StepScope
@RequiredArgsConstructor
@PropertySource("classpath:serviceKey.yaml")
public class RealestateItemReader implements ItemReader<BuildingDealDto>, StepExecutionListener {

    @Value("${serviceKeyGun}")
    private String serviceKey;
    private final RestTemplate restTemplate;
    private Iterator<URI> uriIterator;

    private String currentBuildingType;
    private String currentDealType;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("READER STEP START");
        ExecutionContext ctx = stepExecution.getExecutionContext();
        String currentUri = (String) ctx.get(OpenApiContents.URL);
        currentBuildingType = (String) ctx.get(OpenApiContents.BUILDING_TYPE);
        currentDealType = (String) ctx.get(OpenApiContents.DEAL_TYPE);

        Iterator<String> regionCodeIterator = OpenApiContents.regionMap.keySet().iterator();
        ArrayList<String> dateIterator = new ArrayList<>(OpenApiContents.dateMap);

        List<URI> urlList = new ArrayList<>();
        URI uri;
        while(regionCodeIterator.hasNext()) {
            StringBuilder sb = new StringBuilder();
            String currentRegionCode = regionCodeIterator.next();
            for (String currentDate : dateIterator) {
                try {
                    uri = new URI(sb.append(String.format(currentUri, serviceKey, currentRegionCode, currentDate)).toString());
                    log.info(uri.toString());
                    urlList.add(uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        uriIterator = urlList.iterator();
    }

    @Override
    public BuildingDealDto read() {
        long start = System.currentTimeMillis();

        while(uriIterator.hasNext()){
            URI uri = uriIterator.next();
            if (currentDealType.equals(OpenApiContents.BARGAIN_NUM)){
                BargainDto bargainDto = restTemplate.getForObject(uri, BargainDto.class);
                bargainDto.setDealType(currentDealType);
                bargainDto.setBuildingType(currentBuildingType);

                return bargainDto;
            }
            CharterAndRentDto charterAndRentDto;
            try {
                charterAndRentDto = restTemplate.getForObject(uri, CharterAndRentDto.class);
            }catch (Exception e){
                log.error("데이터 불러오는데 문제가 발생함.");
                log.error(uri.toString());
                continue;
            }

            charterAndRentDto.setDealType(currentDealType);
            charterAndRentDto.setBuildingType(currentBuildingType);

            return charterAndRentDto;
        }

        long end = System.currentTimeMillis();

        log.info("URL READ SUCCESS(" + (end-start)/1000 +" 초 걸림)");
        return null;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("READER STEP OVER");
        return null;
    }
}
