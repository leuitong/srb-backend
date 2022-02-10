package com.example.srb.core.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.example.srb.common.exception.BusinessException;
import com.example.srb.common.result.R;
import com.example.srb.common.result.ResponseEnum;
import com.example.srb.core.pojo.entity.Dict;
import com.example.srb.core.pojo.entity.dto.ExcelDictDTO;
import com.example.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Api(tags = "积分等级管理")
@CrossOrigin
@RestController
@RequestMapping("/admin/core/dict")
@Slf4j
public class AdminDictController {
    @Resource
    private DictService dictService;

    @ApiOperation("Excel数据批量导入")
    @PostMapping("/import")
    public R batchImport(
            @ApiParam(value = "Excel数据字典文件", required = true)
            @RequestParam("file") MultipartFile multipartFile) {
        try {
            dictService.importData(multipartFile.getInputStream());
            return R.ok().message("Excel文件导入成功");
        } catch (Exception e) {
            log.error("Excel文件导入失败");
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }

    }

    @ApiOperation("Excel数据批量导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系

        String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.listDictData());
    }

    /*树形数据的两种加载方案
        方案一：非延迟加载
        需要后端返回的数据结构中包含嵌套数据，并且嵌套数据放在children属性中


        方案二：延迟加载
        不需要后端返回数据中包含嵌套数据，并且要定义布尔属性hasChildren，表示当前节点是否包含子数据
        如果hasChildren为true，就表示当前节点包含子数据
        如果hasChildren为false，就表示当前节点不包含子数据
        如果当前节点包含子数据，那么点击当前节点的时候，就需要通过load方法加载子数据*/
    @ApiOperation("根据上级id获取子节点数据列表")
    @GetMapping("/listByParentId/{parentId}")
    public R listByParentId(
            @ApiParam(value = "上级节点id", required = true)
            @PathVariable Long parentId){

        List<Dict> dictList = dictService.listByParentId(parentId);
        return R.ok().data("list", dictList);
    }

}
