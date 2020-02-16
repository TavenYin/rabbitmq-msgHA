package com.github.taven;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MsgService {
    public static final Integer MSG_NEW = 0;
    public static final Integer MSG_CONFIRM = 1;
    public static final Integer MSG_ERROR = 2;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 创建消息
     *
     * @param jsonObject 可序列化为json的对象
     * @return
     */
    @Transactional
    public Msg create(Object jsonObject) {
        Msg msg = new Msg(null, jsonObject);
        String jsonData = JSON.toJSONString(jsonObject, SerializerFeature.WriteClassName);

        String insertSql = "insert into mq_msg (status, json_data, try_times, create_time) values (?,?,?, UNIX_TIMESTAMP())";
        jdbcTemplate.update(insertSql, MSG_NEW, jsonData, 0);

        Integer id = jdbcTemplate.queryForObject("SELECT last_insert_id() as id", Integer.class);
        msg.setId(id);
        return msg;
    }

    public void producerAck(Integer msgId, boolean ack) {
        String sql = ack ? "update mq_msg set status = 1, try_times = try_times+1 where id = ?" : "update mq_msg set try_times = try_times+1 where id = ?";
        jdbcTemplate.update(sql, msgId);
    }

    public void updateStatus(Integer msgId, Integer status) {
        String sql = "update mq_msg set status = ? where id = ?";
        jdbcTemplate.update(sql, status, msgId);
    }

    public void consumerAck(Integer msgId) {

    }

}
