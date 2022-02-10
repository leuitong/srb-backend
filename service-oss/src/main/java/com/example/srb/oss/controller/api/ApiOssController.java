package com.example.srb.oss.controller.api;

import com.example.srb.common.exception.BusinessException;
import com.example.srb.common.result.R;
import com.example.srb.common.result.ResponseEnum;
import com.example.srb.oss.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@Api(tags = "文件上传")
@CrossOrigin
@RestController
@RequestMapping("/api/oss")
@Slf4j
public class ApiOssController {

    @Resource
    private OssService ossService;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R upload(
            @ApiParam(value= "文件", required = true)
            @RequestParam("file") MultipartFile file,

            @ApiParam(value = "模块", required = true)
            @RequestParam("module") String module){
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String url = ossService.upload(inputStream, module, originalFilename);
            return R.ok().message("文件上传成功").data("url", url);
        } catch (IOException e) {
            log.error("文件上传失败");
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @ApiOperation("删除oss文件")
    @DeleteMapping("/remove")
    public R remove(
            @ApiParam(value = "要删除的文件", required = true)
            @RequestParam("url") String url){
        ossService.removeFile(url);
        return R.ok().message("删除成功");
    }
}
