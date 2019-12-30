package kr.ac.skuniv.realestate_batch.batch.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JpaItemWriter;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JpaItemListWriter<T> extends JpaItemWriter<List<T>> {
    private JpaItemWriter<T> jpaItemWriter;

    public JpaItemListWriter(JpaItemWriter<T> jpaItemWriter) {
        this.jpaItemWriter = jpaItemWriter;
    }

    @Override
    public void write(List<? extends List<T>> items) {
        List<T> totalList = new ArrayList<>();

        for(List<T> list : items){
            totalList.addAll(list);
        }

        log.info(totalList.size() + "의 크기 데이터 저장 시작");
        jpaItemWriter.write(totalList);
        log.info("WRITER COMPLETE");
    }

}
