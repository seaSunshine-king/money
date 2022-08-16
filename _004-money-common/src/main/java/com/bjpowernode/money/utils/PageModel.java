package com.bjpowernode.money.utils;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页的数据，里面包含了
 *      当前页的学生的集合数据
 *      总的记录数
 *      总的页数
 *      当前页
 *      每页显示的记录数
 *      首页?
 *      末页?
 * @author Administrator
 *
 */
public class PageModel<T> implements Serializable {
    //用户id
    private Integer userId;
    //产品类型
    private Integer ptype;
    //当前页
    private Integer currentPage;
    //总页数
    private Integer totalPage;
    //每页显示多少条数据
    private Integer pageSize;
    //数据总条数
    private Integer totalSize;
    //当前页的数据集合
    private List<T> list;

    public PageModel() {
        this.pageSize = 9;
    }

    public PageModel(Integer ptype, Integer currentPage, Integer totalPage, Integer pageSize, Integer totalSize, List<T> list) {
        this.ptype = ptype;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.pageSize = pageSize;
        this.totalSize = totalSize;
        this.list = list;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPtype() {
        return ptype;
    }

    public void setPtype(Integer ptype) {
        this.ptype = ptype;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "userId=" + userId +
                ", ptype=" + ptype +
                ", currentPage=" + currentPage +
                ", totalPage=" + totalPage +
                ", pageSize=" + pageSize +
                ", totalSize=" + totalSize +
                ", list=" + list +
                '}';
    }
}
