package com.realtime.protection.server.v1.mapper;

import com.realtime.protection.server.v1.entity.TCommandTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (TCommandTask)表数据库访问层
 *
 * @author makejava
 * @since 2024-08-27 15:36:46
 */
@Mapper
public interface TCommandTaskMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TCommandTask queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param tCommandTask 查询条件
     * @return 对象列表
     */
    List<TCommandTask> queryAllByLimit(TCommandTask tCommandTask,
                                       @Param("page") int page,
                                       @Param("pageSize") Integer pageSize);

    /**
     * 统计总行数
     *
     * @param tCommandTask 查询条件
     * @return 总行数
     */
    long count(TCommandTask tCommandTask);

    /**
     * 新增数据
     *
     * @param tCommandTask 实例对象
     * @return 影响行数
     */
    int insert(TCommandTask tCommandTask);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<TCommandTask> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<TCommandTask> entities);

    /**
     * 修改数据
     *
     * @param tCommandTask 实例对象
     * @return 影响行数
     */
    int update(TCommandTask tCommandTask);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    Long count1(TCommandTask tCommandTask);

    List<TCommandTask> queryAllByLimit1(TCommandTask tCommandTask,
                                        @Param("page") int page,
                                        @Param("pageSize") Integer pageSize);
}

