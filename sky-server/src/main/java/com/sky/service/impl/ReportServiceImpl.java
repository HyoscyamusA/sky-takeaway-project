package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ReportMapper reportMapper;

//    /**
//     * 根据时间区间统计营业额
//     * @param begin
//     * @param end
//     * @return
//     */
//    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
//        List<LocalDate> dateList = new ArrayList<>();
//        dateList.add(begin);
//
//        while (!begin.equals(end)){
//            begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
//            dateList.add(begin);
//        }
//
//        List<Double> turnoverList = new ArrayList<>();
//        for (LocalDate date : dateList) {
//            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
//            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
//            Map map = new HashMap();
//            map.put("status", Orders.COMPLETED);
//            map.put("begin",beginTime);
//            map.put("end", endTime);
//            Double turnover = orderMapper.sumByMap(map);
//            turnover = turnover == null ? 0.0 : turnover;
//            turnoverList.add(turnover);
//        }
//
//        //数据封装
//        return TurnoverReportVO.builder()
//                .dateList(StringUtils.join(dateList,","))
//                .turnoverList(StringUtils.join(turnoverList,","))
//                .build();
//    }
//
//    /**
//     * 根据时间区间统计用户数量
//     * @param beginTime
//     * @param endTime
//     * @return
//     */
//    private Integer getUserCount(LocalDateTime beginTime, LocalDateTime endTime) {
//        Map map = new HashMap();
//        map.put("begin",beginTime);
//        map.put("end", endTime);
//        return userMapper.countByMap(map);
//    }
//
//    @Override
//    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
//        List<LocalDate> dateList = new ArrayList<>();
//        dateList.add(begin);
//
//        while (!begin.equals(end)){
//            begin = begin.plusDays(1);
//            dateList.add(begin);
//        }
//        List<Integer> newUserList = new ArrayList<>(); //新增用户数
//        List<Integer> totalUserList = new ArrayList<>(); //总用户数
//
//        for (LocalDate date : dateList) {
//            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
//            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
//            //新增用户数量 select count(id) from user where create_time > ? and create_time < ?
//            Integer newUser = getUserCount(beginTime, endTime);
//            //总用户数量 select count(id) from user where  create_time < ?
//            Integer totalUser = getUserCount(null, endTime);
//
//            newUserList.add(newUser);
//            totalUserList.add(totalUser);
//        }
//
//        return UserReportVO.builder()
//                .dateList(StringUtils.join(dateList,","))
//                .newUserList(StringUtils.join(newUserList,","))
//                .totalUserList(StringUtils.join(totalUserList,","))
//                .build();
//    }


    //营业额统计
    @Transactional

    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> localDateList = new ArrayList<>();
        List<Double> doubleList = new ArrayList<>();

        localDateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);

        }

        //查询每天的营业额
        for (LocalDate date : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = reportMapper.sumBytTurnoverMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            doubleList.add(turnover);
        }

        String dateListStr = StringUtils.collectionToCommaDelimitedString(localDateList);
        String turnoverListStr = StringUtils.collectionToCommaDelimitedString(doubleList);

        return TurnoverReportVO
                .builder()
                .dateList(dateListStr)
                .turnoverList(turnoverListStr)
                .build();
    }

    //用户统计
    @Transactional
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        localDateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }
        Integer total = 0;
        //select * from user where create_time > begin and create_time < end
        for (LocalDate date : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer newUser =  reportMapper.sumByUserMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
            total += newUser;
        }
        for (int i = 0; i < localDateList.size(); i++) {
            totalUserList.add(total);
        }
        String dateListStr = StringUtils.collectionToCommaDelimitedString(localDateList);
        String newUserListStr = StringUtils.collectionToCommaDelimitedString(newUserList);
        String totalUserListStr = StringUtils.collectionToCommaDelimitedString(totalUserList);
        return UserReportVO.builder()
                .dateList(dateListStr)
                .newUserList(newUserListStr)
                .totalUserList(totalUserListStr)
                .build();
    }



    /**
     * 根据时间区间统计订单数量
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end){
        List<LocalDate> localDateList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        localDateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }
        Integer validOrderCount = 0;
        Integer totalOrderCount = 0;
        for (LocalDate date : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Integer ordersDou = reportMapper.sumOrdersByMap(map);
            ordersDou = ordersDou == null ? 0 : ordersDou;
            validOrderCountList.add(ordersDou);
            validOrderCount += ordersDou;
            map.remove("status");
            Integer ordersAll = reportMapper.sumOrdersByMap(map);
            ordersAll = ordersAll == null ? 0 : ordersAll;
            orderCountList.add(ordersAll);
            totalOrderCount+=ordersAll;
        }
        String dateListStr = StringUtils.collectionToCommaDelimitedString(localDateList);
        String validOrderCountListStr = StringUtils.collectionToCommaDelimitedString(validOrderCountList);
        String orderCountListStr = StringUtils.collectionToCommaDelimitedString(orderCountList);
        Double orderCompletionRate =  validOrderCount/(totalOrderCount/1.0);

        return OrderReportVO.builder()
                .dateList(dateListStr) //日期，以逗号分隔
                .validOrderCountList(validOrderCountListStr)//每日有效订单数，以逗号分隔
                .orderCountList(orderCountListStr)//每日订单数，以逗号分隔
                .totalOrderCount(totalOrderCount) //订单总数
                .validOrderCount(validOrderCount)//有效订单数
                .orderCompletionRate(orderCompletionRate) //订单完成率
                .build();

    }
    /**
     * 根据时间区间统计指定状态的订单数量
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        Map map = new HashMap();
        map.put("status", status);
        map.put("begin",beginTime);
        map.put("end", endTime);
        return orderMapper.countByMap(map);
    }


}
