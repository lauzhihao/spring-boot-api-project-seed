package com.xykj.koala.web;

import com.xykj.koala.core.Result;
import com.xykj.koala.core.ResultGenerator;
import com.xykj.koala.model.InsightStaffClass;
import com.xykj.koala.service.InsightStaffClassService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by @author CodeGenerator on @date 2018/04/18.
*/
@RestController
@RequestMapping("/insight/staff/class")
public class InsightStaffClassController {
    @Resource
    private InsightStaffClassService insightStaffClassService;

    @PostMapping
    public Result add(@RequestBody InsightStaffClass insightStaffClass) {
        insightStaffClassService.save(insightStaffClass);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        insightStaffClassService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PutMapping
    public Result update(@RequestBody InsightStaffClass insightStaffClass) {
        insightStaffClassService.update(insightStaffClass);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        InsightStaffClass insightStaffClass = insightStaffClassService.findById(id);
        return ResultGenerator.genSuccessResult(insightStaffClass);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<InsightStaffClass> list = insightStaffClassService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
