package com.xiangshui.server.mapper;

import com.xiangshui.server.domain.mysql.Partner;
import com.xiangshui.server.example.PartnerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PartnerMapper {
    int countByExample(PartnerExample example);

    int deleteByExample(PartnerExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Partner record);

    int insertSelective(Partner record);

    List<Partner> selectByExample(PartnerExample example);

    Partner selectByPrimaryKey(@Param("id") Integer id, @Param("fields") String fields);

    int updateByExampleSelective(@Param("record") Partner record, @Param("example") PartnerExample example);

    int updateByExample(@Param("record") Partner record, @Param("example") PartnerExample example);

    int updateByPrimaryKeySelective(Partner record);

    int updateByPrimaryKey(Partner record);
}