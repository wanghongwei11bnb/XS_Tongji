package com.xiangshui.server.mapper;

import com.xiangshui.server.domain.mysql.PartnerAreaKey;
import com.xiangshui.server.example.PartnerAreaExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PartnerAreaMapper {
    int countByExample(PartnerAreaExample example);

    int deleteByExample(PartnerAreaExample example);

    int deleteByPrimaryKey(PartnerAreaKey key);

    int insert(PartnerAreaKey record);

    int insertSelective(PartnerAreaKey record);

    List<PartnerAreaKey> selectByExample(PartnerAreaExample example);

    int updateByExampleSelective(@Param("record") PartnerAreaKey record, @Param("example") PartnerAreaExample example);

    int updateByExample(@Param("record") PartnerAreaKey record, @Param("example") PartnerAreaExample example);
}