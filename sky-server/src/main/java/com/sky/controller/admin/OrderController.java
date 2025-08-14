package com.sky.controller.admin;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;

import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
public class OrderController {
    @Autowired
    private OrderService ordersService;

    //订单搜索
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = ordersService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    //各个状态的订单数量统计
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO orderStatisticsVO = ordersService.statistics();
        return Result.success(orderStatisticsVO);
    }

    //查询订单详情
    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Long id) {
        OrderVO orderVO = ordersService.details(id);
        return Result.success(orderVO);
    }

    //接单
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        ordersService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    //拒单
    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        System.out.println("ordersRejectionDTO="+ordersRejectionDTO);
        ordersService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    //派送订单
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id) {
        ordersService.delivery(id);
        return Result.success();
    }
    //取消订单
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        ordersService.adminCancel(ordersCancelDTO);
        return Result.success();
    }

    //完成订单
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id) {
        ordersService.complete(id);
        return Result.success();

    }
}