package com.realtime.protection.server.v1.mapper;

import com.realtime.protection.server.v1.entity.TCommandReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * (TCommandReport)表数据库访问层
 *
 * @author makejava
 * @since 2024-09-02 09:23:28
 */
@Mapper
public interface TCommandReportMapper {

    /**
     * 通过commandID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TCommandReport queryByCommandId(Long id);

    /**
     * 新增数据
     *
     * @param tCommandReport 实例对象
     * @return 影响行数
     */
    int insert(TCommandReport tCommandReport);

    /**
     * 修改数据
     *
     * @param tCommandReport 实例对象
     * @return 影响行数
     */
    int update(TCommandReport tCommandReport);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

