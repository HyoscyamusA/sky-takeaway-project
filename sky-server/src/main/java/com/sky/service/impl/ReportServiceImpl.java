package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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

    @Transactional
    @Override
    public SalesTop10ReportVO getTopTen(LocalDate begin, LocalDate end) {
        List<String> nameList = new ArrayList<>();
        List<String> numberList = new ArrayList<>();
        List<SalesTop10ReportVO> topTenList = reportMapper.getTopTen();

        for (SalesTop10ReportVO salesTop10ReportVO : topTenList) {
            nameList.add(salesTop10ReportVO.getNameList());
            numberList.add(salesTop10ReportVO.getNumberList());
        }

        String nameListStr = StringUtils.collectionToCommaDelimitedString(nameList);
        String numberListStr = StringUtils.collectionToCommaDelimitedString(numberList);

        return SalesTop10ReportVO.builder()
                .nameList(nameListStr)
                .numberList(numberListStr)
                .build();
    }
    @Autowired
    private WorkspaceService workspaceService;
    /**导出近30天的运营数据报表
     * @param response
     **/
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        //查询概览运营数据，提供给Excel模板文件
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin,LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获得Excel文件中的一个Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            //获得第4行
            XSSFRow row = sheet.getRow(3);
            //获取单元格
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //准备明细数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.flush();
            out.close();
            excel.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
