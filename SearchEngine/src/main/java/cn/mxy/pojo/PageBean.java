package cn.mxy.pojo;

import java.util.List;

public class PageBean {
    private List<ResultBean> data; //获取的完整表
    private Integer totalRecord; //表中一共有多少条数据
    private Integer pageSize; //每页最多有多少条，默认10条，用户传入
    private Integer currentPage; //当前页为第几页，默认为第一页，用户传入
    private Integer totalPage; //尾页（该表总共的页数），需要计算


    public List<ResultBean> getData() {
        return data;
    }

    public void setData(List<ResultBean> data) {
        this.data = data;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public Integer getPageSize() {
        this.pageSize = 10;//默认10条
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        //当currentPage<=1，则默认等于1
        this.currentPage = this.currentPage <= 1 ? 1:this.currentPage;
        //当currentPage>=总页数，则默认等于总页数
        this.currentPage = this.currentPage >= getTotalPage() ? getTotalPage():this.currentPage;
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPage() {
        this.totalPage = getTotalRecord() % getPageSize() == 0 ? getTotalRecord()/getPageSize():getTotalRecord()/getPageSize() + 1;
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

}
