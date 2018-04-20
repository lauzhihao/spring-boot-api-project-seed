package com.xykj.koala.web;

import com.xykj.koala.core.Result;
import com.xykj.koala.core.ResultGenerator;
import com.xykj.koala.model.KoalaSchool;
import com.xykj.koala.service.KoalaSchoolService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by @author CodeGenerator on @date 2018/04/18.
*/
@RestController
@RequestMapping("/koala/school")
public class KoalaSchoolController {
    @Resource
    private KoalaSchoolService koalaSchoolService;

    @PostMapping
    public Result add(@RequestBody KoalaSchool koalaSchool) {
        koalaSchoolService.save(koalaSchool);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        koalaSchoolService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PutMapping
    public Result update(@RequestBody KoalaSchool koalaSchool) {
        koalaSchoolService.update(koalaSchool);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        KoalaSchool koalaSchool = koalaSchoolService.findById(id);
        return ResultGenerator.genSuccessResult(koalaSchool);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<KoalaSchool> list = koalaSchoolService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
