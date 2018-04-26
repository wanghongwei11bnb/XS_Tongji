package com.xiangshui.server.mapper;

import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.example.OpExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OpMapper {
    int countByExample(OpExample example);

    int deleteByExample(OpExample example);

    int deleteByPrimaryKey(String username);

    int insert(Op record);

    int insertSelective(Op record);

    List<Op> selectByExample(OpExample example);

    Op selectByPrimaryKey(@Param("id") String username, @Param("columnList") String columnList);

    int updateByExampleSelective(@Param("record") Op record, @Param("example") OpExample example);

    int updateByExample(@Param("record") Op record, @Param("example") OpExample example);

    int updateByPrimaryKeySelective(Op record);

    int updateByPrimaryKey(Op record);
}