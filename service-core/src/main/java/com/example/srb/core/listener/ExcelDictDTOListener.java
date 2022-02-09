package com.example.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.srb.core.mapper.DictMapper;
import com.example.srb.core.pojo.entity.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {

    private DictMapper dictMapper;

    private List<ExcelDictDTO> list = new ArrayList<>();
    private static final int BATCH_COUNT = 5;

    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(ExcelDictDTO excelDictDTO, AnalysisContext analysisContext) {
        log.info("解析到一条记录：{}", excelDictDTO);
        list.add(excelDictDTO);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 一次性存储
        saveData();
        log.info("所有数据解析完毕");

    }

    private void saveData() {
        log.info("{} 条数据被存到数据库", list.size());
        // 调用mapper层的save方法
        dictMapper.insertBatch(list);
        log.info("{} 条数据被存到数据库完毕", list.size());

    }
}
