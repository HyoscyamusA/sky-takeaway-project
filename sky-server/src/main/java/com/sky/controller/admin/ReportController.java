package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * 报表
 */
@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "统计报表相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;
//
//    /**
//     * 营业额数据统计
//     *
//     * @param begin
//     * @param end
//     * @return
//     */
//    @GetMapping("/turnoverStatistics")
//    @ApiOperation("营业额数据统计")
//    public Result<TurnoverReportVO> turnoverStatistics(
//            @DateTimeFormat(pattern = "yyyy-MM-dd")
//            LocalDate begin,
//            @DateTimeFormat(pattern = "yyyy-MM-dd")
//            LocalDate end) {
//        return Result.success(reportService.getTurnover(begin, end));
//    }
//
//    /**
//     * 用户数据统计
//     * @param begin
//     * @param end
//     * @return
//     */
//    @GetMapping("/userStatistics")
//    @ApiOperation("用户数据统计")
//    public Result<UserReportVO> userStatistics(
//            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
//            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
//
//        return Result.success(reportService.getUserStatistics(begin,end));
//    }
//    /**
//     * 订单数据统计
//     * @param begin
//     * @param end
//     * @return
//     */
//    @GetMapping("/ordersStatistics")
//    @ApiOperation("用户数据统计")
//    public Result<OrderReportVO> orderStatistics(
//            @DateTimeFormat(pattern = "yyyy-MM-dd")
//            LocalDate begin,
//            @DateTimeFormat(pattern = "yyyy-MM-dd")
//            LocalDate end){
//
//        return Result.success(reportService.getOrderStatistics(begin,end));
//    }
//


    //营业额统计
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);
    }

    //用户统计
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        UserReportVO userReportVO = reportService.getUserStatistics(begin,end);
        return Result.success(userReportVO);
    }

    //订单统计
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        OrderReportVO orderReportVO = reportService.getOrdersStatistics(begin,end);
        return Result.success(orderReportVO);
    }

    //查询销量排名top10
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> getTopTen(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        SalesTop10ReportVO salesTop10ReportVO = reportService.getTopTen(begin,end);
        return Result.success(salesTop10ReportVO);
    }
    /**
     * 导出运营数据报表
     * @param response
     */
    @GetMapping("/export")
    @ApiOperation("导出运营数据报表")
    public void export(HttpServletResponse response){
        reportService.exportBusinessData(response);
    }

}
