package com.offcn.content.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.content.service.ContentService;
import com.offcn.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;
    @RequestMapping("/selectByCategoryId")
    public List<TbContent> selectByCategoryId(Long categoryId){
        return contentService.selectByCategoryId(categoryId);
    }

}
