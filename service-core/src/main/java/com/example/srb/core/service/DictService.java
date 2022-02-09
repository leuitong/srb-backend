package com.example.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.srb.core.pojo.entity.Dict;
import com.example.srb.core.pojo.entity.dto.ExcelDictDTO;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author Tong
 * @since 2022-01-24
 */
public interface DictService extends IService<Dict> {
    void importData(InputStream inputStream);

    List<ExcelDictDTO> listDictData();

    List<Dict> listByParentId(Long parentId);
}
