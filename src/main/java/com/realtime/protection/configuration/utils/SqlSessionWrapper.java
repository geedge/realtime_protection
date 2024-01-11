package com.realtime.protection.configuration.utils;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class SqlSessionWrapper {

    private final SqlSessionFactory sqlSessionFactory;

    public SqlSessionWrapper(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * 启动批量SQL会话
     *
     * @param mapperClass   MyBatis Mapper类型
     * @param batchFunction 批量函数（批量添加、批量删除、批量更新等）
     * @param arguments     函数附带的所有参数，可以使用Map进行包装
     * @param <M>           Mapper class
     * @param <I>           Function input
     * @param <O>           Function output
     * @return 被包装的批量函数返回值
     */
    public <M, I, O> O startBatchSession(Class<M> mapperClass,
                                         Function<M, Function<I, O>> batchFunction,
                                         I arguments) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        M mapper = sqlSession.getMapper(mapperClass);
        try {
            O result = batchFunction.apply(mapper).apply(arguments);
            sqlSession.commit();
            sqlSession.clearCache();

            return result;
        } catch (Exception e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
    }
}
