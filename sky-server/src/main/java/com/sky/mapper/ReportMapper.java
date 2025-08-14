package com.sky.mapper;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {

    Double sumBytTurnoverMap(Map map);

    Integer sumByUserMap(Map map);

    Integer sumOrdersByMap(Map mapCompleted);

    @Select("select od.name as nameList,sum(od.number) as numberList FROM order_detail od left outer join orders o on od.order_id = o.id where o.`status` = 5 GROUP BY name ORDER BY sum(od.number) desc limit 10")
    List<SalesTop10ReportVO> getTopTen();

    Integer sumOrdersLikeTime(Integer status,String dateStr);

    @Select("select count(*) from user where create_time like #{dateStr} ")
    Integer sumNewUserLikeTime(String dateStr);

    @Select("select sum(amount) from orders where  status = 5 and order_time like  #{dateStr}")
    Double sumAmountLikeTime(String dateStr);

    @Select("select count(*) from dish where status = #{status}")
    Integer sumDishs(Integer status);

    @Select("select count(*) from setmeal where status = #{status}")
    Integer sumSetmeals(Integer status);


    Integer getOrderByStatus(Integer status);
}
