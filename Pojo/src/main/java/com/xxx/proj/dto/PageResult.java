package com.xxx.proj.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月01日 18:25:00
 */
public class PageResult implements Serializable {
    private Long total;
    private List rows;

    public PageResult() {
    }

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
