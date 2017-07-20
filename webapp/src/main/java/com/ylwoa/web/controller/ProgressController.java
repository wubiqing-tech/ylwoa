package com.ylwoa.web.controller;

import com.google.common.collect.Maps;
import com.ylwoa.model.Excel;
import com.ylwoa.progress.IProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.ylwoa.common.Commons.ACTIVE_STATE;
import static com.ylwoa.common.Commons.INACTIVE_STATE;

/**
 * Created by wubiqing on 2017/7/20.
 */
@RequestMapping("/progress")
@Controller
public class ProgressController {
    @Autowired
    private IProgressService progressService;

    private static transient Logger log = LoggerFactory.getLogger(ProgressController.class);

    @RequestMapping(value = "/list/{pageNo}")
    public ModelAndView progressList(@PathVariable String pageNo) {
        ModelAndView mv = new ModelAndView("/progress/list");
        Map<String, Object> paras = Maps.newHashMap();
        List<Excel> progressList;
        try {
            paras.put("deleteFlg", ACTIVE_STATE);
            progressList = progressService.getList(paras);
        } catch (Exception e) {
            log.error("progressList error", pageNo, e);
            mv.addObject("success", false);
            mv.addObject("pageData", null);
            return mv;
        }
        mv.addObject("success", true);
        mv.addObject("pageData", progressList);
        if ("0".equals(pageNo)) {
            mv.addObject("pageNo", pageNo);
        } else {
            mv.addObject("pageNo", "9999");
        }

        return mv;
    }

    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public ModelAndView toAdd() {
        ModelAndView mv = new ModelAndView("/progress/add");
        return mv;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add(Excel progress) {
        ModelAndView mv = new ModelAndView("/progress/list");
        try {
            Date now = new Date();
            progress.setUpdateUserId(1);
            progress.setUpdateTime(now);
            progress.setCreateUserId(1);
            progress.setCreateTime(now);
            progress.setDeleteFlg(ACTIVE_STATE);
            progressService.insertExcel(progress);
            mv.setViewName("forward:/progress/list/0");
        } catch (Exception e) {
            log.error("add error", progress, e);
        }

        return mv;
    }


    @RequestMapping(value = "/toView/{excelId}", method = RequestMethod.GET)
    public ModelAndView toView(@PathVariable String excelId) {
        ModelAndView mv = new ModelAndView("/progress/view");
        Map<String, Object> paras = Maps.newHashMap();
        paras.put("id", excelId);
        try {
            List<Excel> progressList = progressService.getList(paras);
            if (null != progressList && progressList.size() > 0) {
                mv.addObject("success", true);
                mv.addObject("data", progressList.get(0));
            }
        } catch (Exception e) {
            log.error("toView error", excelId, e);
            mv.addObject("success", false);
        }
        return mv;
    }

    @RequestMapping(value = "/delete/{excelId}")
    public ModelAndView delete(@PathVariable String excelId) {
        ModelAndView mv = new ModelAndView("/progress/list");
        try {
            Excel progress = new Excel();
            progress.setId(Long.parseLong(excelId));
            progress.setDeleteFlg(INACTIVE_STATE);
            progressService.updateExcel(progress);
            mv.setViewName("forward:/progress/list/9999");
        } catch (Exception e) {
            log.error("delete error", excelId, e);
        }

        return mv;
    }

    @RequestMapping(value = "/toEdit/{excelId}")
    public ModelAndView toEdit(@PathVariable String excelId) {
        ModelAndView mv = new ModelAndView("/progress/edit");
        Map<String, Object> paras = Maps.newHashMap();
        paras.put("id", excelId);
        try {
            List<Excel> progressList = progressService.getList(paras);
            if (null != progressList && progressList.size() > 0) {
                mv.addObject("success", true);
                mv.addObject("data", progressList.get(0));
            }
        } catch (Exception e) {
            log.error("toEdit error", excelId, e);
            mv.addObject("success", false);
        }
        return mv;
    }

    @RequestMapping(value = "/edit")
    public ModelAndView edit(Excel progress) {
        ModelAndView mv = new ModelAndView("/progress/list");
        Excel progressForUpdate = new Excel();
        progressForUpdate.setId(progress.getId());
        progressForUpdate.setUpdateUserId(1);
        progressForUpdate.setUpdateTime(new Date());
        try {
            progressService.updateExcel(progressForUpdate);
            mv.setViewName("forward:/progress/list/9999");
        } catch (Exception e) {
            log.error("edit error", progress, e);
            mv.addObject("success", false);
        }
        return mv;
    }
}
