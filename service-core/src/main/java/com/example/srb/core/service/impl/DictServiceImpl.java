package com.example.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.srb.core.listener.ExcelDictDTOListener;
import com.example.srb.core.mapper.DictMapper;
import com.example.srb.core.pojo.entity.Dict;
import com.example.srb.core.pojo.entity.dto.ExcelDictDTO;
import com.example.srb.core.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Tong
 * @since 2022-01-24
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * Excel数据导入
     * @param inputStream Excel文件
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("Excel导入成功");
    }

    /**
     * Excel数据导出
     * @return 数据对象
     */
    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = baseMapper.selectList(null);
        //创建ExcelDictDTO列表，将Dict列表转换成ExcelDictDTO列表
        ArrayList<ExcelDictDTO> excelDictDTOList = new ArrayList<>(dictList.size());
        dictList.forEach(dict -> {

            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict, excelDictDTO);
            excelDictDTOList.add(excelDictDTO);
        });
        return excelDictDTOList;
    }

    /**
     * 树形列表展示
     * @param parentId 父级id
     * @return 数据内容
     */
    @Override
    public List<Dict> listByParentId(Long parentId) {
        // 若redis连接不上，会抛异常，但业务不能终止，可以从数据库中查询，因此需要try catch捕获
        try {
            log.info("从redis获取数据");
            // 首先查询redis中是否有数据列表
            List<Dict> redisDictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);
            // 若存在直接返回数据列表
            if (redisDictList != null) {
                return redisDictList;
            }
        } catch (Exception e) {
            log.error("redis服务器异常:" + ExceptionUtils.getStackTrace(e));
        }
        // 若不存在则查询数据库
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        dictList.forEach(dict -> {
            boolean hasChildren = hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        });

        // 将数据存入redis
        try {
            log.info("数据存入redis");
            redisTemplate.opsForValue().set("srb:core:dictList:" + parentId, dictList, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis服务器异常:" + ExceptionUtils.getStackTrace(e));
        }

        // 返回数据列表
        return dictList;
    }

    private boolean hasChildren(Long id) {
        // 判断一个ID下有没有子节点
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
