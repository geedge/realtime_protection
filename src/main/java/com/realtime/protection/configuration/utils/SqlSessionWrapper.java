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

    public <M, I, O> O startBatchSession(Class<M> mapperClass,
                                         Function<M, Function<I, O>> wrappedFunction,
                                         I arguments) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        M mapper = sqlSession.getMapper(mapperClass);
        try {
            O result = wrappedFunction.apply(mapper).apply(arguments);
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
