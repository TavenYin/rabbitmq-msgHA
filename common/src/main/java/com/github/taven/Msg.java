package com.github.taven;

import java.io.Serializable;

/**
 * 消息
 */
public class Msg implements Serializable {
    private static final long serialVersionUID = -6902658577815998650L;
    private Integer id;
    private Object data;

    public Msg() {
    }

    public Msg(Integer id, Object data) {
        this.id = id;
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
